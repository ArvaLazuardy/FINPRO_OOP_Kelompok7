package com.mrpororo.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.objects.Platform;
import com.mrpororo.objects.enemies.SnowGolem;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.EntityFactory;

/** Level sandbox: arena kecil + boss placeholder di kanan. */
public class TestLevel extends Level {

    private static final float GROUND_TOP = 85f;

    private SnowGolem boss;

    @Override
    public void load() {
        resetLevel();
        worldWidth = 960f;
        spawnX = 70f;
        spawnY = 130f;
        // Snow tiles are only visual. Collision is exactly the Platform rectangle.
        for (int x = 0; x < 960; x += 240) {
            platforms.add(EntityFactory.createPlatform(x, 0f, 240f, 85f, AssetManager.SNOW_GROUND));
        }
        platforms.add(EntityFactory.createPlatform(260f, 185f, 170f, 24f, AssetManager.SNOW_LEDGE));
        platforms.add(EntityFactory.createPlatform(550f, 265f, 165f, 24f, AssetManager.SNOW_LEDGE));

        boss = EntityFactory.createBoss(720f, GROUND_TOP); // diam di kanan, berdiri di tanah
    }

    @Override
    public Color getBackgroundColor() { return Color.BLACK; }

    @Override
    protected void updateLevel(float delta) {
        if (boss == null) return;
        if (enemyDelta > 0f) boss.update(enemyDelta);

        // debug: H = boss kena 10 damage (Phase 2 saat HP <= 50%)
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) boss.takeDamage(100);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer renderer) {
        // platform bersprite (kalau tileset ada)
        batch.begin();
        for (Platform p : platforms) p.render(batch);
        batch.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // fallback: platform tanpa sprite (tileset belum ada) digambar sebagai kotak abu-abu
        for (Platform p : platforms) {
            if (p.getSprite() == null) p.render(renderer);
        }

        if (boss != null) {
            boss.render(renderer);

            float ratio = (float) boss.getHealth() / boss.getMaxHealth();
            float barX = worldWidth - 20f - 400f, barY = worldHeight - 40f;
            renderer.setColor(0.15f, 0.15f, 0.15f, 1f);
            renderer.rect(barX - 2f, barY - 2f, 404f, 20f);
            renderer.setColor(0.8f, 0.15f, 0.15f, 1f);
            renderer.rect(barX, barY, 400f * ratio, 16f);
        }
        renderer.end();
    }

    public SnowGolem getBoss() { return boss; }
}
