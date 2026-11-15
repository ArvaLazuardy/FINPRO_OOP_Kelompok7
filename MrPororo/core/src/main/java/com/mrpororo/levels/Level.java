
package com.mrpororo.levels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Level {

    private float elapsedTime;
    private boolean active;
    private boolean completed;

    public Level() {
        elapsedTime = 0f;
        active = false;
        completed = false;
    }

    public void start() {
        elapsedTime = 0f;
        active = true;
        completed = false;
        onStart();
    }

    public void update(float delta) {
        if (!active || completed) {
            return;
        }

        elapsedTime += delta;
        updateLevel(delta);
    }

    protected abstract void onStart();

    protected abstract void updateLevel(float delta);

    public abstract void render(ShapeRenderer renderer);

    public void complete() {
        completed = true;
        active = false;
    }

    public void dispose() {
        active = false;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCompleted() {
        return completed;
    }
}
