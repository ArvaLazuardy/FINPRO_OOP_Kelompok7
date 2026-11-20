package com.mrpororo.levels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Level {

    protected boolean isCompleted;
    protected boolean isPaused;
    protected float elapsedTime;

    public abstract void load();

    public void update(float delta) {
        if (isPaused || isCompleted) {
            return;
        }

        elapsedTime += delta;
        updateLevel(delta);
    }

    protected abstract void updateLevel(float delta);

    public abstract void render(ShapeRenderer renderer);

    protected void onStart() {
    }

    protected void complete() {
        isCompleted = true;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void dispose() {
    }
}
