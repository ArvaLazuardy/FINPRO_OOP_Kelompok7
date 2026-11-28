package com.mrpororo.objects.bullets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mrpororo.systems.AssetManager;

import com.mrpororo.objects.GameObject;
import com.mrpororo.objects.Collidable;
import com.mrpororo.objects.patterns.BulletMovementPattern;

public class Bullet extends GameObject {

    private BulletType bulletType;
    private int damage;
    private float vx;
    private float vy;
    private boolean playerBullet;
    private boolean parryable;
    private BulletMovementPattern movementPattern;

    // THESE LINES ARE FOR PHASE 2
    private boolean harmless;



    /** Constructor kosong untuk object pool (EntityFactory.createBullet()). */
    public Bullet() {
        super(0f, 0f, 16f, 16f, 0f, Color.WHITE);
        this.active = false;
    }

    public Bullet(float x, float y, float vx, float vy, int damage,
                  BulletType bulletType, boolean playerBullet) {
        super(x, y, 16f, 16f, (float) Math.hypot(vx, vy), Color.WHITE);

        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.bulletType = bulletType;
        this.playerBullet = playerBullet;
        this.parryable = false;
    }

    @Override
    public void update(float delta) {
        if (!active) return;

        super.update(delta);

        if (movementPattern != null) {
            movementPattern.move(this, delta);
        } else {
            x += vx * delta;
            y += vy * delta;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!active || batch == null) return;
        if (!playerBullet) {
            super.render(batch);
            return;
        }
        TextureRegion frame = AssetManager.getInstance().getRegion(AssetManager.PLAYER_BULLET);
        if (frame == null) return;
        float drawHeight = height;
        float drawWidth = drawHeight * frame.getRegionWidth() / frame.getRegionHeight();
        float drawX = x + (width - drawWidth) / 2f;
        if (vx >= 0f) batch.draw(frame, drawX, y, drawWidth, drawHeight);
        else batch.draw(frame, drawX + drawWidth, y, -drawWidth, drawHeight);
    }

    @Override
    public void onCollision(Collidable other) {
        // TODO: Handle bullet collision.
    }

    public void init(float x, float y, float vx, float vy, int damage,
                     BulletType bulletType, boolean playerBullet) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.speed = (float) Math.hypot(vx, vy);
        this.damage = damage;
        this.bulletType = bulletType;
        this.playerBullet = playerBullet;

        // A pooled enemy spike may have changed these values previously.
        this.width = 16f;
        this.height = 16f;
        this.color = Color.WHITE;
        this.sprite = null;
        this.animation = null;
        this.active = true;
        this.stateTime = 0f;
        this.movementPattern = null;
        this.parryable = false;
        this.harmless = false;
    }

    /** Overload yang dipakai BulletManager / EntityFactory. */
    public void init(float x, float y, float vx, float vy, int damage, BulletType type) {
        init(x, y, vx, vy, damage, type, type == BulletType.PLAYER);
        this.parryable = (type == BulletType.ENEMY_PARRYABLE);
    }

    public int getDamage() { return damage; }
    public BulletType getBulletType() { return bulletType; }

    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }

    public boolean isPlayerBullet() { return playerBullet; }

    public boolean isParryable() { return parryable; }
    public void setParryable(boolean parryable) { this.parryable = parryable; }

    public BulletMovementPattern getMovementPattern() { return movementPattern; }
    public void setMovementPattern(BulletMovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    // PHASE 2 Harmless GETTER & SETTER
    public boolean isHarmless() { return harmless; }
    public void setHarmless(boolean harmless) { this.harmless = harmless; }
}
