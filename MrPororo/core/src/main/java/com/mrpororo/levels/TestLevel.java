package com.mrpororo.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.EntityFactory;
import com.mrpororo.objects.Platform;

public class TestLevel extends Level {
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
    }

    @Override
    public Color getBackgroundColor() { return Color.BLACK; }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer renderer) {
        batch.begin();
        for (Platform p : platforms) p.render(batch);
        batch.end();
    }
}
