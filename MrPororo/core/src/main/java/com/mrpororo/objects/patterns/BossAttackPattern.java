package com.mrpororo.objects.patterns;

import com.mrpororo.objects.enemies.SnowGolem;

/** Strategy Pattern: satu kelas = satu pola serangan boss. */
public abstract class BossAttackPattern {

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
    }

    protected void onStart(SnowGolem boss) { }
    protected abstract void onUpdate(SnowGolem boss, float delta);

    public boolean isFinished() { return finished; }
    public float getCooldownAfter() { return cooldownAfter; }
}
