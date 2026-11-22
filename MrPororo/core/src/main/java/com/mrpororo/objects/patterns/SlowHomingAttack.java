package com.mrpororo.objects.patterns;

import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;

public class SlowHomingAttack extends BossAttackPattern {
    private static final float WINDUP = 0.8f;
    private static final float SPEED = 130f;
    private static final float TURN_RATE = 1.8f;  // rad/s
    private static final float LIFETIME = 6f;
    private static final int DAMAGE = 1;

    private static final float SECOND_DELAY = 1.5f;
    private boolean firedFirst, firedSecond;

    private boolean fired;

    public SlowHomingAttack() { super(1.5f); }

    @Override
    protected void onStart(SnowGolem boss) {
        firedFirst = false;
        firedSecond = false;
    }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        float g = boss.getGroundY();

        // FIRST HOMING BULLET SPAWN
        if (!firedFirst && timer >= WINDUP) {
            // NOTICE ME!!!! THIS HOMING BULLET IS ALWAYS PARRYABLE!!!
            spawnHoming(boss, g + 100f); // ADJUSTABLE y
            spawnHoming(boss, g + 220f);
            firedFirst = true;
        }

        // SECOND waave HOMING BULLET SPAWN
        if (!firedSecond && timer >= WINDUP + SECOND_DELAY) {
            spawnHoming(boss, g + 150f);
            firedSecond = true;
        }

        if (firedSecond && timer >= WINDUP + SECOND_DELAY + 1.5f) finished = true; // orb bbkl tetap hidup sampai LIFETIME


    }

    private void spawnHoming(SnowGolem boss, float y) {
        // NOTEICE ME! (PARRY) HOMING ALWAYS PARRYABLE! NO ROLL
        Bullet orb = spawnBullet(boss, boss.getShootX(), y, -SPEED, 0f, DAMAGE, true, 30f, 30f);
        orb.setMovementPattern(new HomingMovement(boss.getTarget(), SPEED, TURN_RATE, LIFETIME));
    }

    //DEBUG
    @Override
    public String getName() {
        return "Pattern 3 - Slow Homing";
    }
}
