package com.mrpororo.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.objects.Platform;
import com.mrpororo.objects.enemies.SnowGolem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Level {
    protected final List<Platform> platforms = new ArrayList<>();
    protected boolean isCompleted;
    protected boolean isPaused;
    protected float elapsedTime;
    protected float worldWidth = 960f;
    protected float worldHeight = 540f;
    protected float spawnX = 65f;
    protected float spawnY = 140f;

    protected float enemyDelta;

    public abstract void load();
    protected void resetLevel() {
        platforms.clear();
        elapsedTime = 0f;
        isCompleted = false;
        isPaused = false;
    }

    public void update(float delta) {
        if (!isPaused && !isCompleted) {
            elapsedTime += delta;
            updateLevel(delta);
        }
    }
    protected void updateLevel(float delta) {}

    public abstract Color getBackgroundColor();
    public abstract void render(SpriteBatch batch, ShapeRenderer renderer);

    public List<Platform> getPlatforms() { return Collections.unmodifiableList(platforms); }
    public float getWorldWidth() { return worldWidth; }
    public float getWorldHeight() { return worldHeight; }
    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
    public float getElapsedTime() { return elapsedTime; }
    public boolean isCompleted() { return isCompleted; }
    public void complete() { isCompleted = true; }
    public void pause() { isPaused = true; }
    public void resume() { isPaused = false; }
    public void dispose() {}

    public void setEnemyDelta(float enemyDelta) { this.enemyDelta = enemyDelta; }
    public SnowGolem getBoss() { return null; } // di-override TestLevel & BossLevel
}
