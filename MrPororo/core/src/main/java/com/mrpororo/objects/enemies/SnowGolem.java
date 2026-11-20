package com.mrpororo.objects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.patterns.BossState;
import com.mrpororo.objects.patterns.PhaseOneState;
import com.mrpororo.systems.AssetManager;

/**
 * Ice Golem boss. Diam di tempat (seperti Cuphead);
 * yang berganti hanya sprite + daftar serangan lewat BossState.
 */
public class SnowGolem extends Enemy {

    private BossState currentState;
    private String spriteKey;

    public SnowGolem(float x, float y) {
        super(x, y, 200f, 300f, 100, 20, 0f, Color.CYAN);
        changeState(new PhaseOneState());
    }

    @Override
    public void update(float delta) {
        super.update(delta); // stateTime
        if (!active || currentState == null) return;

        currentState.update(this, delta);

        BossState next = currentState.getNextState(this);
        if (next != null) {
            changeState(next);
        }
    }

    public void changeState(BossState next) {
        if (currentState != null) currentState.exit(this);
        currentState = next;
        currentState.enter(this);
    }

    /** Dipanggil BossState.enter(): ganti sprite (placeholder: warna rectangle). */
    public void setPhaseVisual(String spriteKey, Color placeholderColor) {
        this.spriteKey = spriteKey;
        this.color = placeholderColor;
        this.sprite = AssetManager.getInstance().getRegion(spriteKey); // null sampai sprite didaftarkan
    }

    @Override
    public void attack() {
        // serangan ditangani BossAttackPattern di dalam BossState
    }

    public BossState getCurrentState() { return currentState; }
    public String getSpriteKey() { return spriteKey; }
}
