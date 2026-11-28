package com.mrpororo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.mrpororo.ui.LeaderboardPanel;
import com.mrpororo.objects.enemies.SnowGolem;

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

    private final RunTimer runTimer = new RunTimer();
    private final LeaderboardPanel leaderboardPanel = new LeaderboardPanel();
    private boolean rankedRun;
    private boolean paused;

    private final GameHUD hud;
    private final FreezeOverlayRenderer freezeOverlay;

    private float dummyShootTimer;
    private float playerShootTimer;

    private String parryStartKey = AssetManager.PARRY_START;
    private String parrySuccessKey = AssetManager.PARRY_SUCCESS_V2;
    private float parryAnimationTime = 0f;
    private float parrySuccessTime = 0f;
    private boolean parrySuccessPlaying = false;

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
        // Keep practice as the development default; use startRankedRun() here for release.
        loadPracticeLevel(LevelManager.LevelType.TEST);
    }

    private void startRankedRun() {
        leaderboardPanel.close();
        loadLevel(LevelManager.LevelType.RUN_GUN);
        runTimer.start();
        rankedRun = true;
    }

    private void loadPracticeLevel(LevelManager.LevelType type) {
        runTimer.stop();
        rankedRun = false;
        loadLevel(type);
    }

    private void loadLevel(LevelManager.LevelType type) {
        levelManager.load(type);
        resetLevelState();
    }

    private void resetLevelState() {
        stopAbilityEffects();
        parrySystem.reset();
        bulletManager.clear();
        collisionReferee.reset();
        parryAnimationTime = 0f;
        parrySuccessTime = 0f;
        parrySuccessPlaying = false;
        respawnPlayer();
    }

    private void stopAbilityEffects() {
        SoundManager.getInstance().stopParry();
        if (timeManager.isFrozen()) SoundManager.getInstance().stopFreezeAudio();
        timeManager.reset();
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
        if (paused) return;
        if (leaderboardPanel.isOpen()) {
            if (leaderboardPanel.canLeave() && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                startRankedRun();
                renderWorld();
                return;
            } else {
                leaderboardPanel.render();
                return;
            }
        }
        float dt = Math.max(0f, Math.min(delta, 1f / 30f));
        handleDebugKeys();
        if (leaderboardPanel.isOpen()) {
            leaderboardPanel.render();
            return;
        }
        Level level = levelManager.getCurrentLevel();

        if (player.isDestroyed() || level.isCompleted()) {
            renderWorld();
            return;
        }

        boolean wasParrying = parrySystem.isParryActive();
        parrySystem.update(dt);
        collisionReferee.update(dt);

        if (parrySystem.isParryActive()) parryAnimationTime += dt;
        if (parrySuccessPlaying) {
            parrySuccessTime += dt;
            Animation<TextureRegion> successAnim = AssetManager.getInstance().getAnimation(parrySuccessKey);
            if (successAnim == null || successAnim.isAnimationFinished(parrySuccessTime)) {
                parrySuccessPlaying = false;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && parrySystem.beginParry()) {
            parryAnimationTime = 0f;
            parrySuccessPlaying = false;
            SoundManager.getInstance().startParry();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && parrySystem.isMeterFull() && timeManager.activateFreeze()) {
            parrySystem.consumeFullMeter();
            runTimer.registerFreeze();
            SoundManager.getInstance().startFreezeAudio();
        }

        boolean frozenThisFrame = timeManager.isFrozen();
        // Count real frame time, excluding at most the remaining freeze duration.
        runTimer.update(delta, timeManager.getRemainingTime());
        float enemyDelta = timeManager.getEnemyDelta(dt);

        // NOTICE ME SENPAI!
        // DUMMY INI GW NONAKTIFIN DULU YH BUAT TEST ATTACK PATTERN.
        // TEMPORARY BULLET TESTING
        /*
        if (levelManager.getCurrentType() == LevelManager.LevelType.TEST) {
            dummyShootTimer -= enemyDelta;
            if (dummyShootTimer <= 0f) {
                bulletManager.spawnEnemyBullet(850f, 117f, -190f, 0f, 10, true);
                dummyShootTimer = 2f;
            }
        }
        */


        //  UPDATE!
        SnowGolem boss = level.getBoss();
        if (boss != null) boss.bind(bulletManager, player, level.getWorldWidth(), level.getWorldHeight());
        level.setEnemyDelta(Math.min(enemyDelta, 1f / 30f));
        level.update(dt);
        player.update(dt, level.getPlatforms());
        bulletManager.update(dt, enemyDelta, level.getWorldWidth(), level.getWorldHeight());
        if (collisionReferee.resolveParries(player.getParryHitbox(), bulletManager.getActiveEnemyBullets(), parrySystem)) {
            runTimer.registerParry();
            parryAnimationTime = 0f;
            parrySuccessTime = 0f;
            parrySuccessPlaying = true;
            SoundManager.getInstance().successfulParry();
        }

        collisionReferee.resolvePlayerHits(player, bulletManager.getActiveEnemyBullets());
        if (!player.isDestroyed()) {
            // Includes SnowGolem and, later, every minion returned by Level.getEnemies().
            collisionReferee.resolveEnemyHits(bulletManager.getActivePlayerBullets(), level.getEnemies());
            collisionReferee.resolveEnemyContact(player, level.getEnemies());
        }
        bulletManager.cleanup(level.getWorldWidth(), level.getWorldHeight());

        timeManager.update(Math.max(0f, delta));
        if (frozenThisFrame && !timeManager.isFrozen()) SoundManager.getInstance().stopFreezeAudio();

        if (player.isDestroyed()) {
            runTimer.stop();
            stopAbilityEffects();
            parrySuccessPlaying = false;
            renderWorld();
            return;
        }

        if (player.getY() < -200f) respawnPlayer(); collisionReferee.reset();// jatuh keluar dunia

        if (level instanceof RunGunLevel) {
            ((RunGunLevel) level).checkFinish(player.getX());
        }
        // Detect a lethal hit this frame rather than waiting for another update.
        if (levelManager.getCurrentType() == LevelManager.LevelType.BOSS
            && boss != null && boss.isDead()) level.complete();

        if (level.isCompleted()) {
            if (levelManager.getCurrentType() == LevelManager.LevelType.BOSS) {
                runTimer.stop();
                stopAbilityEffects();
                leaderboardPanel.open(rankedRun ? runTimer : null);
                leaderboardPanel.render();
                return;
            }
            LevelManager.LevelType before = levelManager.getCurrentType();
            levelManager.nextLevel();
            if (levelManager.getCurrentType() != before) resetLevelState();
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

        for (Bullet bullet : bulletManager.getActiveEnemyBullets()) {
            if (bullet.isDestroyed()) continue;
            shapeRenderer.setColor(bullet.getColor());
            shapeRenderer.rect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
        }

        for (Bullet bullet : bulletManager.getActivePlayerBullets()) {
            if (!bullet.isDestroyed()) bullet.render(shapeRenderer);
        }

        shapeRenderer.end();
        renderParrySprite();

        hud.render(batch, shapeRenderer, player, parrySystem, timeManager, levelManager.getCurrentType().name());
        freezeOverlay.render(shapeRenderer, timeManager.isFrozen(), timeManager.getRemainingTime());
        if (rankedRun) leaderboardPanel.renderTimer(runTimer, timeManager.isFrozen());
    }

    private void renderParrySprite() {
        boolean startActive = parrySystem.isParryActive();
        if (!parrySuccessPlaying && !startActive) return;

        Rectangle hitbox = player.getParryHitbox();
        String key = parrySuccessPlaying ? parrySuccessKey : parryStartKey;
        float elapsed = parrySuccessPlaying ? parrySuccessTime : parryAnimationTime;
        Animation<TextureRegion> anim = AssetManager.getInstance().getAnimation(key);
        if (anim == null) return;

        TextureRegion frame = anim.getKeyFrame(elapsed, false);
        // Center the visual around the full body. Collision remains unchanged.
        float visualSize = Math.max(hitbox.width, hitbox.height) * 1.15f;
        float scale = visualSize / Math.max(frame.getRegionWidth(), frame.getRegionHeight());
        float drawWidth = frame.getRegionWidth() * scale;
        float drawHeight = frame.getRegionHeight() * scale;
        float drawX = hitbox.x + (hitbox.width - drawWidth) / 2f;
        float drawY = hitbox.y + (hitbox.height - drawHeight) / 2f;

        batch.begin();
        batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
        batch.end();
    }

    private void handleDebugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) { startRankedRun(); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) { loadPracticeLevel(LevelManager.LevelType.TEST); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) { loadPracticeLevel(LevelManager.LevelType.RUN_GUN); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) { loadPracticeLevel(LevelManager.LevelType.BOSS); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (rankedRun) startRankedRun();
            else loadPracticeLevel(levelManager.getCurrentType());
            return;
        }
        if (!rankedRun && Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            stopAbilityEffects();
            leaderboardPanel.open(null);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        leaderboardPanel.resize(width, height);
        updateCamera();
    }

    @Override public void pause()  { paused = true; levelManager.getCurrentLevel().pause(); }
    @Override public void resume() { paused = false; levelManager.getCurrentLevel().resume(); }
    @Override public void hide()   { }

    @Override
    public void dispose() {
        stopAbilityEffects();
        levelManager.dispose();
        leaderboardPanel.dispose();
        hud.dispose();
        freezeOverlay.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        AssetManager.getInstance().dispose();
    }
}
