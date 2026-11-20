package com.mrpororo.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.objects.Platform;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.EntityFactory;

// just the place for now
public class BossLevel extends Level {
    private int bossHealth = 100;

    @Override
    public void load() {
        resetLevel();
        bossHealth = 100;
        worldWidth = 960f;
        spawnX = 85f;
        spawnY = 145f;
        for (int x = 0; x < 960; x += 240) {
            platforms.add(EntityFactory.createPlatform(x, 0f, 240f, 94f, AssetManager.SNOW_GROUND));
        }
        platforms.add(EntityFactory.createPlatform(245f, 185f, 160f, 24f, AssetManager.SNOW_LEDGE));
        platforms.add(EntityFactory.createPlatform(535f, 185f, 160f, 24f, AssetManager.SNOW_LEDGE));
    }

    @Override
    public Color getBackgroundColor() { return new Color(0.24f, 0.38f, 0.59f, 1f); }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer renderer) {
        batch.begin();
        if (AssetManager.getInstance().getRegion(AssetManager.SNOW_TREE_RIGHT) != null) {
            batch.draw(AssetManager.getInstance().getRegion(AssetManager.SNOW_TREE_RIGHT),
                715f, 86f, 200f, 275f);
        }
        for (Platform p : platforms) p.render(batch);
        batch.end();
    }

    @Override
    protected void updateLevel(float delta) {
        if (bossHealth <= 0) complete();
    }

    public void setBossHealth(int value) { bossHealth = value; }
    public int getBossHealth() { return bossHealth; }
}
