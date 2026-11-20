package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.enemies.SnowGolem;
import com.mrpororo.objects.patterns.BossState;

public class PhaseOneState extends BossState {

    public PhaseOneState() {
        // TODO: addAttack(new IceShardAttack());
        // TODO: addAttack(new IceBreathSpikeRainAttack());
        // TODO: addAttack(new SlowHomingAttack());
    }

    @Override
    public BossState getNextState(SnowGolem boss) {
        // pindah ke fase 2 saat HP <= 50%
        return boss.getHealth() <= boss.getMaxHealth() * 0.5f ? new PhaseTwoState() : null;
    }

    @Override public String getName() { return "PHASE 1"; }
    @Override public String getSpriteKey() { return "boss_phase1"; }
    @Override public Color getPlaceholderColor() { return new Color(0.45f, 0.85f, 1f, 1f); }
}
