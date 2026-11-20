package com.mrpororo.objects.bullets;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.GameObject;

public class Bullet extends GameObject {
    private float vx, vy;
    private float damage;
    private BulletType type;

    public Bullet() {
        super(0, 0, 12, 12, 0, Color.WHITE);
        active = false;
        type = BulletType.PLAYER;
    }

    public void init(float x, float y, float vx, float vy, float damage, BulletType type) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.type = type;
        this.width = 12;
        this.height = 12;
        this.color = type == BulletType.PLAYER ? Color.YELLOW
            : type == BulletType.ENEMY_PARRYABLE ? Color.PINK : Color.RED;
        active = true;
        stateTime = 0f;
    }

    @Override
    public void update(float delta) {
        if (!active) return;
        super.update(delta);
        x += vx * delta;
        y += vy * delta;
    }

    public boolean isPlayerBullet() { return type == BulletType.PLAYER; }
    public boolean isParryable() { return type == BulletType.ENEMY_PARRYABLE; }
    public float getDamage() { return damage; }
    public BulletType getType() { return type; }
}
