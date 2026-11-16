package com.mrpororo.levels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BossLevel extends Level {

    private int bossHealth;
    private boolean bossDefeated;

    @Override
    public void load() {
        bossHealth = 100;
        bossDefeated = false;

        elapsedTime = 0;
        isCompleted = false;
        isPaused = false;

        onStart();
    }

    @Override
    protected void updateLevel(float delta) {
        checkBossDefeated();
    }

    @Override
    public void render(ShapeRenderer renderer) {
        // Render Boss Level
    }

    public boolean checkBossDefeated() {
        if (bossHealth <= 0) {
            bossDefeated = true;
            complete();
        }

        return bossDefeated;
    }

    public void setBossHealth(int bossHealth) {
        this.bossHealth = bossHealth;
    }

    public int getBossHealth() {
        return bossHealth;
    }

    @Override
    public void dispose() {
    }
}
