package com.mrpororo.levels;

public class BossLevel extends Level {

    private int bossHealth;
    private boolean bossDefeated;

    @Override
    public void load() {
        bossHealth = 100;
        bossDefeated = false;
        isCompleted = false;
    }

    @Override
    public void update(float delta) {
        if (isPaused || bossDefeated) {
            return;
        }

        checkBossDefeated();
    }

    @Override
    public void render() {
        // Render Boss Level
    }

    public boolean checkBossDefeated() {
        if (bossHealth <= 0) {
            bossDefeated = true;
            isCompleted = true;
        }

        return bossDefeated;
    }

    @Override
    public void dispose() {
    }
}
