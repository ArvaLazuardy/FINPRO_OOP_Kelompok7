package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;

public class IceSpikeRainAttack extends BossAttackPattern {
    private static final int SPIKES = 9;
    private static final float WINDUP = 0.8f;      // boss "meniup" ke atas
    private static final float INTERVAL = 0.35f;
    private static final float HOVER = 0.7f;       // spike diam dulu = peringatan
    private static final float FALL_SPEED = 430f;
    private static final int DAMAGE = 15;

    private int spawned;

    public IceSpikeRainAttack() { super(1.5f); }

    @Override
    protected void onStart(SnowGolem boss) { spawned = 0; }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        if (spawned < SPIKES && timer >= WINDUP + spawned * INTERVAL) {
            spawnSpike(boss);
            spawned++;
        }
        if (spawned >= SPIKES && timer >= WINDUP + (SPIKES - 1) * INTERVAL + HOVER + 1.2f) finished = true;
    }

    private void spawnSpike(SnowGolem boss) {
        float w = 26f, h = 56f;
        float x = MathUtils.random(0f, boss.getWorldWidth() - w);
        float y = boss.getWorldHeight() - h - 20f;
        // NOTICE ME (PARRY)! this SPIKE PARRYABLE STATE IS  ROLLED!
        Bullet b = spawnBullet(boss, x, y, 0f, 0f, DAMAGE, rollParryable(), w, h);
        b.setMovementPattern(new FallingSpikeMovement(HOVER, FALL_SPEED, boss.getGroundY()));
    }

    //DEBUG
    @Override public String getName() { return "Pattern 2 - Ice Spike Rain"; }
}
