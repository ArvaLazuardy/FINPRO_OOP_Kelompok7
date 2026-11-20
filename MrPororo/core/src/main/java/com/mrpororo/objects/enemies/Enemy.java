package com.mrpororo.objects.enemies;

import com.badlogic.gdx.graphics.Color;
import com.mrpororo.objects.GameObject;

public abstract class Enemy extends GameObject {

    protected int health;
    protected int maxHealth;
    protected int damage;
    protected float movementSpeed;

    public Enemy(
        float x, float y, float width, float height,
        int health, int damage, float movementSpeed, Color color
    ) {
        super(x, y, width, height, movementSpeed, color);

        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.movementSpeed = movementSpeed;
    }

    public abstract void attack();

    public void takeDamage(int amount) {
        if (amount <= 0 || !active) return;

        health -= amount;
        if (health <= 0) {
            health = 0;
            destroy();
        }
    }

    public boolean isDead() { return health <= 0; }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
}
