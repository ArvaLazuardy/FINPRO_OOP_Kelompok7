package com.mrpororo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mrpororo.systems.LeaderboardClient;
import com.mrpororo.systems.RunTimer;

import java.util.Collections;
import java.util.List;

/** Temp keyboard-driven UI */
public class LeaderboardPanel {
    private final FitViewport viewport = new FitViewport(960, 540);
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final LeaderboardClient client = new LeaderboardClient();
    private List<LeaderboardClient.Entry> entries = Collections.emptyList();
    private RunTimer result;
    private boolean open, loading, submitting, submitted, prompting;
    private int generation;
    private String status = "";
    private String submitStatus = "";
    private String nameText = "";
    private InputProcessor previousInput;
    private final InputAdapter nameInput = new InputAdapter() {
        @Override public boolean keyTyped(char character) {
            if (!prompting) return false;
            if (character >= 32 && character <= 126 && nameText.length() < 24) {
                nameText += character;
            }
            return true;
        }
        @Override public boolean keyDown(int keycode) {
            if (prompting && keycode == Input.Keys.BACKSPACE && !nameText.isEmpty()) {
                nameText = nameText.substring(0, nameText.length() - 1);
                return true;
            }
            return false;
        }
    };

    public void open(RunTimer completedRun) {
        close();
        open = true;
        previousInput = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(nameInput);
        result = completedRun; // null means read-only practice leaderboard
        refresh();
    }

    public void close() {
        generation++; // Ignore callbacks belonging to the previous panel/run.
        if (open && Gdx.input.getInputProcessor() == nameInput) {
            Gdx.input.setInputProcessor(previousInput);
        }
        previousInput = null;
        nameText = "";
        open = loading = submitting = submitted = prompting = false;
        entries = Collections.emptyList();
        result = null;
        status = submitStatus = "";
    }

    public boolean isOpen() { return open; }
    public boolean canLeave() { return !submitting && !prompting; }

    private void refresh() {
        if (loading) return;
        int token = generation;
        loading = true;
        status = "Loading leaderboard...";
        client.getLeaderboard(rows -> {
            if (token != generation) return;
            entries = rows;
            loading = false;
            status = rows.isEmpty() ? "No records yet" : "Leaderboard loaded";
        }, error -> {
            if (token != generation) return;
            loading = false;
            status = error + " - press L to retry";
        });
    }

    private void submitName() {
        String name = nameText.trim();
        if (name.isEmpty() || name.length() > 24) {
            submitStatus = "Name must contain 1-24 characters.";
            return;
        }
        prompting = false;
        submitting = true;
        int token = generation;
        submitStatus = "Submitting record...";
        client.submitRecord(name, result, () -> {
            if (token != generation) return;
            submitting = false;
            submitted = true;
            submitStatus = "Record saved!";
            // Invalidate any GET begun before the POST completed.
            generation++;
            loading = false;
            refresh();
        }, error -> {
            if (token != generation) return;
            submitting = false;
            submitStatus = error + " - press ENTER to retry";
        });
    }

    public void render() {
        if (prompting) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) prompting = false;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) submitName();
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.L)) refresh();
            if (result != null && !submitted && !submitting
                    && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) prompting = true;
        }
        Gdx.gl.glClearColor(.06f, .12f, .20f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.setColor(Color.CYAN);
        font.getData().setScale(2f);
        font.draw(batch, result == null ? "FROZEN TIME LEADERBOARD" : "VICTORY - FROZEN TIME", 35, 505);
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        if (result != null) {
            font.draw(batch, "Clear: " + RunTimer.formatTime(result.getCompletionTimeMs())
                + "     Actual: " + RunTimer.formatTime(result.getRealTimeMs())
                + "     Parries: " + result.getSuccessfulParries()
                + "     Freezes: " + result.getFreezeCount(), 35, 460);
            font.draw(batch, prompting ? "Name: " + nameText + "_  (ENTER to save)" :
                submitted ? "Record submitted" : submitting ? "Submitting..." : "ENTER - Submit your result", 35, 433);
        } else {
            font.draw(batch, "Practice / read-only view - no record to submit", 35, 460);
        }
        font.draw(batch, "#     Player", 35, 395);
        font.draw(batch, "Clear time", 410, 395);
        font.draw(batch, "Actual time", 575, 395);
        font.draw(batch, "Parries / Freezes", 745, 395);
        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            LeaderboardClient.Entry e = entries.get(i);
            float y = 369 - i * 24;
            font.setColor(i == 0 ? Color.GOLD : Color.WHITE);
            font.draw(batch, Integer.toString(i + 1), 35, y);
            font.draw(batch, e.playerName.replace('\n', ' ').replace('\r', ' '), 85, y);
            font.draw(batch, RunTimer.formatTime(e.completionTimeMs), 410, y);
            font.draw(batch, RunTimer.formatTime(e.realTimeMs), 575, y);
            font.draw(batch, e.successfulParries + " / " + e.freezeCount, 745, y);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, status, 35, 105);
        font.draw(batch, submitStatus, 35, 80);
        font.draw(batch, prompting ? "Type name (ASCII, max 24) | Backspace - Delete | Esc - Cancel" :
            "L - Refresh     R - Start a new ranked run", 35, 30);
        batch.end();
    }

    public void renderTimer(RunTimer timer, boolean frozen) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.setColor(frozen ? Color.CYAN : Color.WHITE);
        font.draw(batch, "Clear time: " + RunTimer.formatTime(timer.getCompletionTimeMs())
            + (frozen ? "  [FROZEN]" : ""), 20, 320);
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void resize(int width, int height) { viewport.update(width, height, true); }
    public void dispose() { close(); font.dispose(); batch.dispose(); }
}
