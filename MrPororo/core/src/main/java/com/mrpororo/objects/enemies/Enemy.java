package com.mrpororo.objects.enemies;

import com.mrpororo.objects.GameObject;

public abstract class Enemy extends GameObject {

    protected int health;
    protected int damage;
    protected float movementSpeed;

    public Enemy(
        float x,
        float y,
        float width,
        float height,
        int health,
        int damage,
        float movementSpeed,
        Color color
    ) {
        super(x, y, width, height, movementSpeed, color);

        this.health = health;
        this.damage = damage;
    }

    public abstract void attack();

    public abstract void update(float delta);

    public void takeDamage(int damage) {
        // TODO: Reduce enemy health
    }

    public boolean isDead() {
        // TODO: Check enemy health
        return false;
    }
}
