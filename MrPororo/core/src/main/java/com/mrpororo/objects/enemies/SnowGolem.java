package com.mrpororo.objects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.patterns.BossState;
import com.mrpororo.objects.patterns.PhaseOneState;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.BulletManager;

/**
 * Ice Golem boss. Diam di tempat (seperti Cuphead);
 * yang berganti hanya sprite + daftar serangan lewat BossState.
 */
public class SnowGolem extends Enemy {

    private BossState currentState;
    private String spriteKey;

    private BulletManager bulletManager;
    private Player target;
    private float worldWidth = 960f;
    private float worldHeight = 540f;

    public SnowGolem(float x, float y) {
        super(x, y, 200f, 300f, 100, 20, 0f, Color.CYAN);
        changeState(new PhaseOneState());
    }

    public void bind(BulletManager bulletManager, Player target, float worldWidth, float worldHeight) {
        this.bulletManager = bulletManager;
        this.target = target;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void update(float delta) {
        super.update(delta); // stateTime
        if (!active || currentState == null || bulletManager == null) return;

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
        // Attack oleh BossAttackPattern di dalem BossState
    }

    public BossState getCurrentState() { return currentState; }
    public String getSpriteKey() { return spriteKey; }

    public BulletManager getBulletManager() { return bulletManager; }
    public Player getTarget() { return target; }
    public float getWorldWidth() { return worldWidth; }
    public float getWorldHeight() { return worldHeight; }
    /** Boss span dengan y = perm. tanah,
     *  y boss = tinggi tanah. */
    public float getGroundY() { return y; }
    /** Titik keluar proyektil (sisi kiri badan boss). */
    public float getShootX() { return x - 10f; }

}
