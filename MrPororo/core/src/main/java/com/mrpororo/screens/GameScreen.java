package com.mrpororo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mrpororo.Main;
import com.mrpororo.levels.Level;
import com.mrpororo.levels.RunGunLevel;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.systems.*;
import com.mrpororo.ui.FreezeOverlayRenderer;
import com.mrpororo.ui.GameHUD;

public class GameScreen implements Screen {

    private static final float VIEW_WIDTH = 960f;
    private static final float VIEW_HEIGHT = 540f;

    private final Main game;

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final ParrySystem parrySystem;
    private final TimeManager timeManager;
    private final BulletManager bulletManager;
    private final CollisionReferee collisionReferee;

    private final LevelManager levelManager;
    private Player player;

    private final GameHUD hud;
    private final FreezeOverlayRenderer freezeOverlay;

    private float dummyShootTimer;
    private float playerShootTimer;

    public GameScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        AssetManager.getInstance().init(); // daftarkan region tileset sebelum level dibuat
        SoundManager.getInstance().init();

        parrySystem = new ParrySystem();
        timeManager = new TimeManager();
        bulletManager = new BulletManager();
        collisionReferee = new CollisionReferee();

        levelManager = new LevelManager();
        hud = new GameHUD();
        freezeOverlay = new FreezeOverlayRenderer();
    }

    @Override
    public void show() {
        loadLevel(LevelManager.LevelType.TEST); // ganti ke RUN_GUN / BOSS sesuai kebutuhan
    }

    private void loadLevel(LevelManager.LevelType type) {
        levelManager.load(type);
        respawnPlayer();
        parrySystem.reset();
        timeManager.reset();
        bulletManager.clear();
    }

    private void respawnPlayer() {
        Level level = levelManager.getCurrentLevel();
        player = EntityFactory.createPlayer(level.getSpawnX(), level.getSpawnY());
        player.setWorldWidth(level.getWorldWidth());
        player.setJumpForce(550f); // supaya bisa mencapai ledge
        updateCamera();
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1f / 30f);
        Level level = levelManager.getCurrentLevel();

        handleDebugKeys();

        boolean wasFrozen = timeManager.isFrozen();
        float enemyDelta = timeManager.getEnemyDelta(delta);
        if (wasFrozen && !timeManager.isFrozen()) SoundManager.getInstance().stopFreezeAudio();

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if (parrySystem.beginParry()) SoundManager.getInstance().startParry();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && parrySystem.isMeterFull() && timeManager.activateFreeze()) {
            parrySystem.consumeFullMeter();
            SoundManager.getInstance().startFreezeAudio();
        }

        // TEMPORARY BULLET TESTING
        if (levelManager.getCurrentType() == LevelManager.LevelType.TEST) {
            dummyShootTimer -= enemyDelta;
            if (dummyShootTimer <= 0f) {
                bulletManager.spawnEnemyBullet(850f, 117f, -190f, 0f, 10, true);
                dummyShootTimer = 2f;
            }
        }

        // ---- UPDATE ----
        level.update(dt);
        player.update(dt, level.getPlatforms(), bulletManager);
        parrySystem.update(delta);
        timeManager.update(delta);

        bulletManager.update(delta, enemyDelta, level.getWorldWidth(), level.getWorldHeight());
        if (collisionReferee.resolveParries(player.getParryHitbox(), bulletManager.getActiveEnemyBullets(), parrySystem)) {
            SoundManager.getInstance().successfulParry();
        }
        collisionReferee.resolvePlayerHits(player, bulletManager.getActiveEnemyBullets());
        bulletManager.cleanup(level.getWorldWidth(), level.getWorldHeight());

        if (player.getY() < -200f) respawnPlayer(); // jatuh keluar dunia

        if (level instanceof RunGunLevel) {
            ((RunGunLevel) level).checkFinish(player.getX());
        }
        if (level.isCompleted()) {
            LevelManager.LevelType before = levelManager.getCurrentType();
            levelManager.nextLevel();
            if (levelManager.getCurrentType() != before) respawnPlayer(); // hanya jika level benar-benar berganti
            level = levelManager.getCurrentLevel();
        }

        updateCamera();

        // Render
        renderWorld();
    }

    private void drawPlayerHealth() {
        float ratio = player.getHealth() / player.getMaxHealth();
        float x = camera.position.x - VIEW_WIDTH / 2f + 20f;
        float y = VIEW_HEIGHT - 40f;
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(x - 2f, y - 2f, 254f, 20f);
        shapeRenderer.setColor(0.2f, 0.75f, 0.3f, 1f);
        shapeRenderer.rect(x, y, 250f * ratio, 16f);
    }

    /** Kamera mengikuti player secara horizontal, dibatasi lebar dunia level. */
    private void updateCamera() {
        float worldWidth = levelManager.getCurrentLevel().getWorldWidth();
        float half = VIEW_WIDTH / 2f;
        float targetX = player.getX() + player.getWidth() / 2f;

        camera.position.x = worldWidth <= VIEW_WIDTH ? half : MathUtils.clamp(targetX, half, worldWidth - half);
        camera.position.y = VIEW_HEIGHT / 2f;
        camera.update();
    }

    private void renderWorld() {
        Level level = levelManager.getCurrentLevel();
        Color bg = level.getBackgroundColor();
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        level.render(batch, shapeRenderer); // level mengurus begin/end sendiri

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.render(shapeRenderer);
        drawPlayerHealth();

        if (parrySystem.isParryActive()) {
            shapeRenderer.setColor(Color.PINK);
            Rectangle h = player.getParryHitbox();
            shapeRenderer.rect(h.x, h.y, h.width, h.height);
        }

// Render enemy bullets
        for (Bullet bullet : bulletManager.getActiveEnemyBullets()) {
            if (bullet.isDestroyed()) continue;

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(
                bullet.getX(),
                bullet.getY(),
                bullet.getWidth(),
                bullet.getHeight()
            );
        }

// Render player bullets
        for (Bullet bullet : bulletManager.getActivePlayerBullets()) {
            if (bullet.isDestroyed()) continue;

            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(
                bullet.getX(),
                bullet.getY(),
                bullet.getWidth(),
                bullet.getHeight()
            );
        }


        shapeRenderer.end();

        hud.render(batch, shapeRenderer, player, parrySystem, timeManager, levelManager.getCurrentType().name());
        freezeOverlay.render(shapeRenderer, timeManager.isFrozen(), timeManager.getRemainingTime());
    }

    private void handleDebugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) loadLevel(LevelManager.LevelType.TEST);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) loadLevel(LevelManager.LevelType.RUN_GUN);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) loadLevel(LevelManager.LevelType.BOSS);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) respawnPlayer();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        updateCamera();
    }

    @Override public void pause()  { levelManager.getCurrentLevel().pause(); }
    @Override public void resume() { levelManager.getCurrentLevel().resume(); }
    @Override public void hide()   { }

    @Override
    public void dispose() {
        levelManager.dispose();
        hud.dispose();
        freezeOverlay.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        AssetManager.getInstance().dispose();
    }
}
