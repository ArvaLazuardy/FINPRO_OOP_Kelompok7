package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.SnowGolem;

// Strategy Pattern: satu class = satu Attack pattern .
public abstract class BossAttackPattern {

    // ARVA NOTICE ME!!! THIS THEPELUANG PARRYABLE
    protected static final float PARRY_CHANCE = 0.25f;

    private static final Color ICE_COLOR = new Color(0.55f, 0.9f, 1f, 1f);

    //ARVA NOTICE ME!! (PARRY) INI WARNA PROJECTILE PARRY
    // BOLEH GANTI PARRYABLE ATTACK DI SINI. ATAU BIKIN SENDIRI BISA. HHE
    private static final Color PARRY_COLOR = Color.PINK;

    protected float timer;
    protected boolean finished;
    private final float cooldownAfter;

    protected BossAttackPattern(float cooldownAfter) {
        this.cooldownAfter = cooldownAfter;
    }

    public void start(SnowGolem boss) {
        timer = 0f;
        finished = false;
        onStart(boss);
    }

    public void update(SnowGolem boss, float delta) {
        if (finished) return;
        timer += delta;
        onUpdate(boss, delta);
        if (finished) onFinish(boss);

    }

    protected void onStart(SnowGolem boss) { }
    protected abstract void onUpdate(SnowGolem boss, float delta);

    // NOTICE ME (PARRY)! METHOD INI NENTUIN PARRYABLE BUAT BULLET.
    // see BulletManager.spawnEvemyBullet() for more info tentang parameter "parryable"
    protected Bullet spawnBullet(SnowGolem boss, float x, float y, float vx, float vy, int damage,
                                boolean parryable, float w,  float h) {
        Bullet b  = boss.getBulletManager().spawnEnemyBullet(x, y, vx, vy, damage, parryable);
        b.setWidth(w);
        b.setHeight(h);
        b.setColor(parryable ? PARRY_COLOR : ICE_COLOR);
        return b;
    }

    // ARVA NOTICE ME (PARRY)! Dipanggil di tiaps attack untuk mengacak "UNDIAN" parryable.
    public boolean rollParryable() {
        return MathUtils.randomBoolean(PARRY_CHANCE);
    }

    public boolean isFinished() { return finished; }
    public float getCooldownAfter() { return cooldownAfter; }

    public abstract String getName();

    protected void onFinish(SnowGolem boss) { }
}
