package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.enemies.SnowGolem;

import java.util.ArrayList;
import java.util.List;

/**
 * State Pattern: satu BossState = satu fase boss (sprite + kumpulan serangan).
 * Boss diam seperti Cuphead; yang berubah hanya sprite dan daftar serangan.
 */
public abstract class BossState {

    private static final float ENTER_DELAY = 1.0f;

    private final List<BossAttackPattern> attacks = new ArrayList<>();
    private BossAttackPattern current;
    private int lastIndex = -1;
    private float idleTimer;

    protected void addAttack(BossAttackPattern attack) {
        attacks.add(attack);
    }

    public void enter(SnowGolem boss) {
        current = null;
        lastIndex = -1;
        idleTimer = ENTER_DELAY;
        boss.setPhaseVisual(getSpriteKey(), getPlaceholderColor());
    }

    public void update(SnowGolem boss, float delta) {
        if (current != null) {
            current.update(boss, delta);
            if (current.isFinished()) {
                idleTimer = current.getCooldownAfter();
                current = null;
            }
            return;
        }

        idleTimer -= delta;
        if (idleTimer <= 0f && !attacks.isEmpty()) {
            current = pickNextAttack();
            System.out.println("[BOSS " + getName() + "] ATTACKING USING " + current.getName());
            current.start(boss);
        }
    }

    public void exit(SnowGolem boss) {
        current = null;
    }

    /** Return state berikutnya jika syarat transisi terpenuhi, null jika tetap. */
    public BossState getNextState(SnowGolem boss) {
        return null;
    }

    public BossAttackPattern getCurrentAttack() { return current; }

    public abstract String getName();
    public abstract String getSpriteKey();
    public abstract Color getPlaceholderColor();

    /** ATTACK PATTERN RANDOMIZED tapi GA  mgulang ATTACK yang sama dua kali berturut2. */
    private BossAttackPattern pickNextAttack() {
        int index;
        do {
            index = MathUtils.random(attacks.size() - 1);
        } while (attacks.size() > 1 && index == lastIndex);
        lastIndex = index;
        return attacks.get(index);
    }
}
