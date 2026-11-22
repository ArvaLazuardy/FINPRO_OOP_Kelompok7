package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;

// Homing version of SpikeRainattack
public class HomingSpikeRainAttack extends BossAttackPattern {
    private static final int SPIKES = 7;
    private static final float WINDUP = 0.8f;
    private static final float INTERVAL = 0.45f;
    private static final float HOVER = 1.1f;        // Total Hover (kek menggantung)
    private static final float LOCK = 0.35f;        // bagian akhir HOVER dengan locked x.
    private static final float TRACK_SPEED = 240f;  // sedikit < speed player (250) biar player bisa   kabur
    private static final float FALL_SPEED = 480f;
    private static final float SPAWN_JITTER = 60f;
    private static final int DAMAGE = 15;

    private int spawned;

    public HomingSpikeRainAttack() { super(1.5f); }

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
        Player p = boss.getTarget();
        float cx = (p != null) ? p.getX() + p.getWidth() / 2f + MathUtils.random(-SPAWN_JITTER, SPAWN_JITTER)
            : MathUtils.random(0f, boss.getWorldWidth());
        float x = MathUtils.clamp(cx - w / 2f, 0f, boss.getWorldWidth() - w);
        float y = boss.getWorldHeight() - h - 20f;

        // NOTICE ME (PARRY)! parryable di-roll per spike, sama seperti Spike Rain phase 1.
        Bullet b = spawnBullet(boss, x, y, 0f, 0f, DAMAGE, rollParryable(), w, h);
        b.setMovementPattern(new TrackingSpikeMovement(
            p, HOVER, LOCK, TRACK_SPEED, FALL_SPEED, boss.getGroundY(), boss.getWorldWidth()));
    }

    //DEBUG
    @Override public String getName() { return "Pattern 2 - Homing Spike Rain"; }
}
