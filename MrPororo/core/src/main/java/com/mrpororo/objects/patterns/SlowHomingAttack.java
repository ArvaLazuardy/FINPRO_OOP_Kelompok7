package com.mrpororo.objects.patterns;

import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;

public class SlowHomingAttack extends BossAttackPattern {
    private static final float WINDUP = 0.8f;
    private static final float SPEED = 130f;
    private static final float TURN_RATE = 1.8f;  // rad/s
    private static final float LIFETIME = 6f;
    private static final int DAMAGE = 1;

    private boolean fired;

    public SlowHomingAttack() { super(1.5f); }

    @Override
    protected void onStart(SnowGolem boss) { fired = false; }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        if (!fired && timer >= WINDUP) {
            float y = boss.getGroundY() + 150f;
            // NOTICE ME!!!! THIS HOMING BULLET IS ALWAYS PARRYABLE!!!
            Bullet orb = spawnBullet(boss, boss.getShootX(), y, -SPEED, 0f, DAMAGE, true, 30f, 30f);
            orb.setMovementPattern(new HomingMovement(boss.getTarget(), SPEED, TURN_RATE, LIFETIME));
            fired = true;
        }
        if (fired && timer >= WINDUP + 1.5f) finished = true; // orb bbkl tetap hidup sampai LIFETIME


    }

    //DEBUG
    @Override
    public String getName() {
        return "Pattern 3 - Slow Homing";
    }
}
