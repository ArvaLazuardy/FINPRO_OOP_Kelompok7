package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.enemies.SnowGolem;

public class IceShardAttack extends BossAttackPattern {
    private static final int SHOTS = 6;
    private static final float WINDUP = 0.7f;
    private static final float INTERVAL = 0.65f;
    private static final float SPEED = 380f;
    private static final int DAMAGE = 1;

    private int fired;

    public IceShardAttack() { super(1.2f); }

    @Override
    protected void onStart(SnowGolem boss) { fired = 0; }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        if (fired < SHOTS && timer >= WINDUP + fired * INTERVAL) {
            float g = boss.getGroundY();
            float y = MathUtils.random(g + 8f, g + 260f); // y acak, masih terjangkau lompat
            spawnBullet(boss, boss.getShootX(), y, -SPEED, 0f, DAMAGE, rollParryable(), 44f, 14f);
            fired++;
        }
        if (fired >= SHOTS && timer >= WINDUP + (SHOTS - 1) * INTERVAL + 0.4f) finished = true;
    }


    //DEBUG
    @Override
    public String getName() { return "Pattern 1 - Ice Shard"; }
}
