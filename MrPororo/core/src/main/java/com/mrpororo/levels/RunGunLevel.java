package com.mrpororo.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.EntityFactory;
import com.mrpororo.objects.Platform;

// temporary place
public class RunGunLevel extends Level {
    private static final float FINISH_X = 2420f;

    @Override
    public void load() {
        resetLevel();
        worldWidth = 2600f;
        spawnX = 70f;
        spawnY = 145f;
        for (int x = 0; x < 2600; x += 260) {
            platforms.add(EntityFactory.createPlatform(x, 0f, 260f, 92f, AssetManager.SNOW_GROUND));
        }
        addLedge(265, 190, 200);
        addLedge(590, 260, 185);
        addLedge(950, 180, 210);
        addLedge(1300, 265, 200);
        addLedge(1710, 195, 205);
        addLedge(2070, 260, 180);
    }
    private void addLedge(float x, float y, float width) {
        platforms.add(EntityFactory.createPlatform(x, y, width, 25f, AssetManager.SNOW_LEDGE));
    }

    @Override
    public Color getBackgroundColor() { return new Color(0.54f, 0.77f, 0.94f, 1f); }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer renderer) {
        AssetManager assets = AssetManager.getInstance();
        batch.begin();

        for (int i = 0; i < 7; i++) {
            String key = i % 2 == 0 ? AssetManager.SNOW_TREE_LEFT : AssetManager.SNOW_TREE_MIDDLE;
            if (assets.getRegion(key) != null) {
                batch.setColor(1f, 1f, 1f, 0.75f);
                batch.draw(assets.getRegion(key), i * 420f + 40f, 80f, 270f, 310f);
                batch.setColor(Color.WHITE);
            }
        }
        for (Platform p : platforms) p.render(batch);
        batch.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GOLD);
        renderer.rect(FINISH_X, 92f, 8f, 175f);
        renderer.end();
    }

    public void checkFinish(float playerX) {
        if (playerX >= FINISH_X) complete();
    }
}
