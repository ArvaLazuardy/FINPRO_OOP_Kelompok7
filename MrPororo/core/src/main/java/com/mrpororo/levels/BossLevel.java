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

public class BossLevel extends Level {

    private static final float GROUND_TOP = 94f;

    private SnowGolem boss;

    @Override
    public void load() {
        resetLevel();
        worldWidth = 960f;
        spawnX = 85f;
        spawnY = 145f;

        for (int x = 0; x < 960; x += 240) {
            platforms.add(EntityFactory.createPlatform(x, 0f, 240f, 94f, AssetManager.SNOW_GROUND));
        }
        platforms.add(EntityFactory.createPlatform(245f, 185f, 160f, 24f, AssetManager.SNOW_LEDGE));
        platforms.add(EntityFactory.createPlatform(535f, 185f, 160f, 24f, AssetManager.SNOW_LEDGE));

        // Boss placeholder: diam di kanan, berdiri di atas tanah
        boss = EntityFactory.createBoss(720f, GROUND_TOP);
    }

    @Override
    public Color getBackgroundColor() { return new Color(0.24f, 0.38f, 0.59f, 1f); }

    @Override
    protected void updateLevel(float delta) {
        if (boss == null) return;

        if (enemyDelta > 0f) boss.update(enemyDelta);

        // ---- debug (hapus nanti) ----
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) boss.takeDamage(100); // 50% HP -> Phase 2
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) boss.takeDamage(500); // langsung Phase 2
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) load();              // restart level

        if (boss.isDead()) complete();
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer renderer) {
        batch.begin();
        for (Platform p : platforms) p.render(batch);
        batch.end();

        if (boss == null) return;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        boss.render(renderer); // placeholder rectangle (skip otomatis kalau sudah mati)

        // boss health bar (kanan atas)
        float ratio = (float) boss.getHealth() / boss.getMaxHealth();
        float barX = worldWidth - 20f - 400f, barY = worldHeight - 40f;
        renderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        renderer.rect(barX - 2f, barY - 2f, 404f, 20f);
        renderer.setColor(0.8f, 0.15f, 0.15f, 1f);
        renderer.rect(barX, barY, 400f * ratio, 16f);
        renderer.end();
    }

    public SnowGolem getBoss() { return boss; }

    // kompatibel dengan versi lama
    public int getBossHealth() { return boss == null ? 0 : boss.getHealth(); }
    public void setBossHealth(int value) {
        if (boss != null && value < boss.getHealth()) boss.takeDamage(boss.getHealth() - value);
    }
}
