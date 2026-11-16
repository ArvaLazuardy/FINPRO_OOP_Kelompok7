package com.mrpororo.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Player extends GameObject {

    private float health;
    private float maxHealth;

    private float velocityY;
    private float gravity;
    private float jumpForce;

    private boolean isGrounded;
    private boolean isJumping;

    public Player(float x, float y) {
        super(
            x,
            y,
            48f,
            64f,
            250f,
            Color.BLUE
        );

        this.maxHealth = 100f;
        this.health = maxHealth;

        this.velocityY = 0f;
        this.gravity = 900f;
        this.jumpForce = 450f;

        this.isGrounded = false;
        this.isJumping = false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (!active) return;

        // Horizontal movement
        if (Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * delta;
        }

        // Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            && isGrounded) {
            velocityY = jumpForce;
            isGrounded = false;
            isJumping = true;
        }

        // Gravity
        velocityY -= gravity * delta;
        y += velocityY * delta;

        // World boundaries
        if (x < 0) {
            x = 0;
        }

        if (x + width > 960f) {
            x = 960f - width;
        }

        // Ground collision
        if (y <= 88f) {
            y = 88f;
            velocityY = 0f;
            isGrounded = true;
            isJumping = false;
        } else {
            isGrounded = false;
        }
    }

    public void takeDamage(float damage) {
        if (damage <= 0 || !active) return;

        health -= damage;

        if (health <= 0) {
            health = 0;
            destroy();
        }
    }

    public void heal(float amount) {
        if (amount <= 0 || !active) return;

        health = Math.min(health + amount, maxHealth);
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setMaxHealth(float maxHealth) {
        if (maxHealth > 0) {
            this.maxHealth = maxHealth;
            health = Math.min(health, maxHealth);
        }
    }

    public void setJumpForce(float jumpForce) {
        if (jumpForce > 0) {
            this.jumpForce = jumpForce;
        }
    }

    public void setGravity(float gravity) {
        if (gravity > 0) {
            this.gravity = gravity;
        }
    }
}
