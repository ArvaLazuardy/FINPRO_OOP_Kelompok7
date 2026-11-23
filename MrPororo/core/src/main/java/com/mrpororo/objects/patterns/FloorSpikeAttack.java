package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;


public class FloorSpikeAttack extends BossAttackPattern {
    private static final float WINDUP = 0.8f;     // boss ganti pose
    private static final float WARN = 1.0f;       // stub kuning bergetar
    private static final float RISE = 0.15f;
    private static final float HOLD = 0.6f;
    private static final float RETRACT = 0.35f;

    private static final int COLUMNS = 10;
    private static final float SPIKE_GAP = 8f;
    private static final float STUB_HEIGHT = 14f;
    // max height MUST BE <  platform-height (BossLevel: ledge y=185, ground top=94 -> 94+90=184).
    private static final float SPIKE_HEIGHT = 90f;
    private static final int DAMAGE = 30;         // MEGA DAMAGE (player maxHealth = 100)

    // NOTICE ME! THIS IS THE PLACEHOLDER POSE:
    // ART REGION CAN BE REGISTERED WITH THIS KEY IN Assetmanager
    private static final String POSE_KEY = "boss_phase2_grab";
    private static final Color POSE_COLOR = new Color(0.1f, 0.15f, 0.7f, 1f);

    private boolean spawned;

    public FloorSpikeAttack() { super(1.8f); }

    @Override
    protected void onStart(SnowGolem boss) {
        spawned = false;
        boss.setPoseVisual(POSE_KEY, POSE_COLOR);
    }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        if (!spawned && timer >= WINDUP) {
            spawnAllSpikes(boss);
            spawned = true;
        }
        if (spawned && timer >= WINDUP + WARN + RISE + HOLD + RETRACT + 0.1f) finished = true;
    }

    private void spawnAllSpikes(SnowGolem boss) {
        float colW = boss.getWorldWidth() / COLUMNS;
        float w = colW - SPIKE_GAP;
        float g = boss.getGroundY();

        for (int i = 0; i < COLUMNS; i++) {
            float x = i * colW + SPIKE_GAP / 2f;
            // NOTICE ME (PARRY)! Floor spike = hazard lingkungan, SELALU non-parryable.
            Bullet b = spawnBullet(boss, x, g, 0f, 0f, DAMAGE, false, w, STUB_HEIGHT);
            b.setHarmless(true);
            b.setMovementPattern(new RisingSpikeMovement(x, WARN, RISE, HOLD, RETRACT, STUB_HEIGHT, SPIKE_HEIGHT));
        }
    }

    @Override
    protected void onFinish(SnowGolem boss) { boss.resetPose(); }

    //DEBUG
    @Override public String getName() { return "Pattern 1 - Floor Spike"; }
}
