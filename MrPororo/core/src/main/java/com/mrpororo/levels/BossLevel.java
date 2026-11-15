
package com.mrpororo.levels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BossLevel extends Level {

    private static final float WORLD_WIDTH = 960f;
    private static final float GROUND_HEIGHT = 88f;

    private float bossHealth;
    private float maxBossHealth;

    private float bossAttackTimer;
    private boolean bossDefeated;

    public BossLevel() {
        super();

        maxBossHealth = 1000f;
        bossHealth = maxBossHealth;

        bossAttackTimer = 0f;
        bossDefeated = false;
    }

    @Override
    protected void onStart() {
        bossHealth = maxBossHealth;
        bossAttackTimer = 0f;
        bossDefeated = false;
    }

    @Override
    protected void updateLevel(float delta) {
        bossAttackTimer += delta;

        if (bossHealth <= 0f) {
            bossHealth = 0f;
            bossDefeated = true;
            complete();
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Boss arena background
        renderer.setColor(0.35f, 0.55f, 0.70f, 1f);
        renderer.rect(0, 0, WORLD_WIDTH, 540);

        // Arena ground
        renderer.setColor(0.80f, 0.92f, 0.98f, 1f);
        renderer.rect(0, 0, WORLD_WIDTH, GROUND_HEIGHT);

        // Boss placeholder
        renderer.setColor(0.25f, 0.38f, 0.52f, 1f);
        renderer.rect(700, GROUND_HEIGHT, 100, 120);

        renderer.end();
    }

    public void takeBossDamage(float damage) {
        if (damage <= 0 || bossDefeated) {
            return;
        }

        bossHealth -= damage;

        if (bossHealth <= 0) {
            bossHealth = 0;
            bossDefeated = true;
            complete();
        }
    }

    public float getBossHealth() {
        return bossHealth;
    }

    public float getMaxBossHealth() {
        return maxBossHealth;
    }

    public float getBossHealthPercentage() {
        return bossHealth / maxBossHealth;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    public float getBossAttackTimer() {
        return bossAttackTimer;
    }
}
