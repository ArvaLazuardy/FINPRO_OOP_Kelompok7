package com.mrpororo.levels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RunGunLevel extends Level {

    private static final float WORLD_WIDTH = 960f;
    private static final float GROUND_HEIGHT = 88f;

    private float levelDuration = 120f;

    public RunGunLevel() {
        super();
    }

    @Override
    public void load() {
        elapsedTime = 0;
        isCompleted = false;
        isPaused = false;

        onStart();
    }

    @Override
    protected void onStart() {
        // Reset kondisi khusus Run Gun Level.
    }

    @Override
    protected void updateLevel(float delta) {
        if (getElapsedTime() >= levelDuration) {
            complete();
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Snowy ground
        renderer.setColor(0.80f, 0.92f, 0.98f, 1f);
        renderer.rect(0, 0, WORLD_WIDTH, GROUND_HEIGHT);

        // Snow surface
        renderer.setColor(0.95f, 0.99f, 1f, 1f);
        renderer.rect(0, GROUND_HEIGHT - 12f,
            WORLD_WIDTH, 12f);

        // Floating platforms
        renderer.setColor(0.70f, 0.88f, 0.94f, 1f);
        renderer.rect(200, 160, 180, 20);
        renderer.rect(480, 230, 180, 20);
        renderer.rect(730, 160, 140, 20);

        renderer.end();
    }

    public float getLevelDuration() {
        return levelDuration;
    }

    public float getProgress() {
        return Math.min(getElapsedTime() / levelDuration, 1f);
    }

    public void setLevelDuration(float duration) {
        if (duration > 0) {
            levelDuration = duration;
        }
    }
}
