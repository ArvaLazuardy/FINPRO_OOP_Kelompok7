package com.mrpororo.levels;

public abstract class Level {

    protected boolean isCompleted;
    protected boolean isPaused;

    public abstract void load();

    public abstract void update(float delta);

    public abstract void render();

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void dispose() {
    }
}
