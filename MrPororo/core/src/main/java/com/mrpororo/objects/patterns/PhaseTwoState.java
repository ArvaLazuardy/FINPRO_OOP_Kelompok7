package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;


public class PhaseTwoState extends BossState {

    public PhaseTwoState() {
        // TODO: addAttack(new FloorSpikeAttack());
        // TODO: addAttack(new HomingIceBreathAttack());
        // TODO: attack ketiga (menyusul)
    }

    @Override public String getName() { return "PHASE 2"; }
    @Override public String getSpriteKey() { return "boss_phase2"; }
    @Override public Color getPlaceholderColor() { return new Color(0.25f, 0.35f, 0.95f, 1f); }
}
