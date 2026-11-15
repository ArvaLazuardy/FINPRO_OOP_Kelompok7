package com.mrpororo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrpororo.levels.Level;
import com.mrpororo.levels.RunGunLevel;
import com.mrpororo.ui.FreezeOverlayRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mrpororo.Main;
import com.mrpororo.ui.GameHUD;

/**
 * Temporary playable game screen. Player, enemies, and collisions are placeholders
 * until the other team members provide their actual classes.
 */
public class GameScreen extends ScreenAdapter {
    public static final float WORLD_WIDTH = 960;
    public static final float WORLD_HEIGHT = 540;

    private static final float MOVE_SPEED = 280f;
    private static final float JUMP_SPEED = 520f;
    private static final float GRAVITY = 1200f;

    // Keep a reference for future menu / victory screen transitions.
    private final Main game;
    private final FitViewport viewport;
    private final ShapeRenderer renderer;
    private final Level currentLevel;

    // Placeholder shapes, not final Player/Enemy/Platform implementations.
    private final Rectangle ground = new Rectangle(0, 0, WORLD_WIDTH, 88);
    private final Rectangle platform = new Rectangle(350, 190, 195, 20);
    private final Rectangle player = new Rectangle(110, ground.height, 40, 55);
    private final Rectangle enemy = new Rectangle(730, ground.height, 45, 45);

    private final GameHUD gameHUD;
    private final FreezeOverlayRenderer freezeOverlayRenderer;
    private float verticalSpeed = 0f;
    private boolean onGround = true;

    public GameScreen(Main game) {
        this.game = game;
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        renderer = new ShapeRenderer();

        currentLevel = new RunGunLevel();
        currentLevel.start();

        gameHUD = new GameHUD();

        freezeOverlayRenderer = new FreezeOverlayRenderer();
    }

    @Override
    public void render(float delta) {
        // Limit large jumps in movement when dragging or restoring the window.
        delta = Math.min(delta, 1f / 30f);

        handleInput(delta);
        updatePlayer(delta);
        currentLevel.update(delta);
        draw();
        gameHUD.render(100f, 100f, 35f, 100f);
        handleInput(delta);
        updatePlayer(delta);
        currentLevel.update(delta);
        freezeOverlayRenderer.update(delta);
        draw();
    }

    private void handleInput(float delta) {
        float direction = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction += 1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            freezeOverlayRenderer.setActive(!freezeOverlayRenderer.isActive());
        }

        player.x += direction * MOVE_SPEED * delta;
        player.x = MathUtils.clamp(player.x, 0, WORLD_WIDTH - player.width);

        if (onGround && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            verticalSpeed = JUMP_SPEED;
            onGround = false;
        }
    }

    private void updatePlayer(float delta) {
        float previousBottom = player.y;
        verticalSpeed -= GRAVITY * delta;
        player.y += verticalSpeed * delta;
        onGround = false;

        // Basic one-way landing on the elevated platform.
        float platformTop = platform.y + platform.height;
        boolean overlapsHorizontally =
            player.x + player.width > platform.x && player.x < platform.x + platform.width;
        if (verticalSpeed <= 0f && overlapsHorizontally
            && previousBottom >= platformTop && player.y <= platformTop) {
            player.y = platformTop;
            verticalSpeed = 0f;
            onGround = true;
        }

        // Basic landing on the ground.
        float groundTop = ground.y + ground.height;
        if (player.y <= groundTop) {
            player.y = groundTop;
            verticalSpeed = 0f;
            onGround = true;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.56f, 0.79f, 0.94f, 1f);
        viewport.apply();
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Snowy ground and floating platform.
        renderer.setColor(0.80f, 0.92f, 0.98f, 1f);
        renderer.rect(ground.x, ground.y, ground.width, ground.height);
        renderer.setColor(0.95f, 0.99f, 1f, 1f);
        renderer.rect(ground.x, ground.height - 12f, ground.width, 12f);
        renderer.setColor(0.70f, 0.88f, 0.94f, 1f);
        renderer.rect(platform.x, platform.y, platform.width, platform.height);

        // Temporary penguin/player rectangle and enemy rectangle.
        renderer.setColor(0.14f, 0.27f, 0.40f, 1f);
        renderer.rect(player.x, player.y, player.width, player.height);
        renderer.setColor(0.93f, 0.98f, 1f, 1f);
        renderer.rect(player.x + 9f, player.y + 7f, player.width - 18f, player.height - 16f);
        renderer.setColor(0.30f, 0.51f, 0.66f, 1f);
        renderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);

        renderer.end();
        freezeOverlayRenderer.setProjectionMatrix(viewport.getCamera().combined);
        freezeOverlayRenderer.render(WORLD_WIDTH, WORLD_HEIGHT);

        gameHUD.setProjectionMatrix(viewport.getCamera().combined);
        gameHUD.render(100f, 100f, 35f, 100f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        currentLevel.dispose();
        gameHUD.dispose();
        freezeOverlayRenderer.dispose();
    }
}
