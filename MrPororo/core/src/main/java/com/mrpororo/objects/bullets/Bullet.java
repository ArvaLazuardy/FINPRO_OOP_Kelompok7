package com.mrpororo.objects.bullets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import com.mrpororo.objects.GameObject;
import com.mrpororo.objects.Collidable;
import com.mrpororo.patterns.BulletMovementPattern;

public class Bullet extends GameObject {

    private BulletType bulletType;

    private int damage;

    private float vx;
    private float vy;

    private boolean playerBullet;

    private boolean parryable;

    private BulletMovementPattern movementPattern;

    public Bullet(
        float x,
        float y,
        float vx,
        float vy,
        int damage,
        BulletType bulletType,
        boolean playerBullet
    ) {
        super(x, y, 16f, 16f,
            (float) Math.hypot(vx, vy), Color.WHITE);

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
        super.render(batch);
    }

    @Override
    public void onCollision(Collidable other) {
        // TODO: Handle bullet collision.
    }

    public void init(
        float x, float y,
        float vx, float vy,
        int damage,
        BulletType bulletType,
        boolean playerBullet
    ) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.bulletType = bulletType;
        this.playerBullet = playerBullet;

        this.active = true;
        this.stateTime = 0f;
        this.movementPattern = null;
        this.parryable = false;
    }

    public int getDamage() { return damage; }

    public BulletType getBulletType() {
        return bulletType;
    }

    public float getVx() { return vx; }
    public float getVy() { return vy; }

    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }

    public boolean isPlayerBullet() {
        return playerBullet;
    }

    public boolean isParryable() {
        return parryable;
    }

    public void setParryable(boolean parryable) {
        this.parryable = parryable;
    }

    public BulletMovementPattern getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(
        BulletMovementPattern movementPattern
    ) {
        this.movementPattern = movementPattern;
    }
}
