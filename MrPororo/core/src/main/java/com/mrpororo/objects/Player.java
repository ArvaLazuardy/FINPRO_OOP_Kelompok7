
package com.mrpororo.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class Player extends GameObject {
    // HEALTH SYSTEM
    private float health = 100f;
    private float maxHealth = 100f;

    // MOVEMENT SYSTEM
    private float velocityY;
    private float gravity = 980f;
    private float jumpForce = 535f;
    private float moveDirection;

    private boolean grounded;
    private boolean facingRight = true;

    // DAMAGE SYSTEM
    private float invulnerableRemaining;

    // PARRY SYSTEM
    private float parryEnergy = 0f;
    private float maxParryEnergy = 100f;

    private float parryCooldown = 0f;
    private float parryDuration = 0f;

    private static final float PARRY_COOLDOWN = 0.35f;
    private static final float PARRY_DURATION = 0.18f;
    private static final float PARRY_ENERGY_GAIN = 25f;

    // DASH SYSTEM
    private boolean dashing = false;
    private float dashTimer = 0f;
    private float dashCooldown = 0f;
    private float dashDirection = 1f;

    private static final float DASH_SPEED = 650f;
    private static final float DASH_DURATION = 0.2f;
    private static final float DASH_COOLDOWN = 1f;

    // CONSTRUCTOR
    public Player(float x, float y) {
        super(x, y, 38f, 54f, 275f, Color.CYAN);
    }

    // MOVEMENT
    public void move(float direction) {
        moveDirection = MathUtils.clamp(direction, -1f, 1f);

        if (moveDirection != 0f) {
            facingRight = moveDirection > 0f;
        }
    }

    public boolean jump() {
        if (!grounded || !active || dashing) {
            return false;
        }

        velocityY = jumpForce;
        grounded = false;

        return true;
    }

    // DASH SYSTEM
    public boolean dash() {

        if (!active || dashing || dashCooldown > 0f) {
            return false;
        }

        dashDirection = moveDirection != 0f
            ? Math.signum(moveDirection)
            : (facingRight ? 1f : -1f);

        dashing = true;
        dashTimer = DASH_DURATION;
        dashCooldown = DASH_COOLDOWN;

        return true;
    }

    public boolean isDashing() {
        return dashing;
    }

    public float getDashCooldown() {
        return dashCooldown;
    }

    public boolean parry() {

        if (!active || parryCooldown > 0f || dashing) {
            return false;
        }

        parryDuration = PARRY_DURATION;
        parryCooldown = PARRY_COOLDOWN;

        return true;
    }

    public boolean isParrying() {
        return parryDuration > 0f;
    }

    public void successfulParry() {

        if (!isParrying()) {
            return;
        }

        parryEnergy = Math.min(
            maxParryEnergy,
            parryEnergy + PARRY_ENERGY_GAIN
        );
    }

    public Rectangle getParryHitbox() {

        float reach = 34f;

        return new Rectangle(
            facingRight ? x + width - 6f : x - reach + 6f,
            y + 8f,
            reach,
            height - 16f
        );
    }

    public float getParryEnergy() {
        return parryEnergy;
    }

    public float getMaxParryEnergy() {
        return maxParryEnergy;
    }

    public boolean isParryEnergyFull() {
        return parryEnergy >= maxParryEnergy;
    }

    public boolean consumeParryEnergy() {

        if (!isParryEnergyFull()) {
            return false;
        }

        parryEnergy = 0f;

        return true;
    }

    @Override
    public void update(float delta) {
        update(delta, null, 960f);
    }

    public void update(
        float delta,
        List<Platform> platforms,
        float worldWidth
    ) {

        if (!active) {
            return;
        }

        float dt = MathUtils.clamp(delta, 0f, 1f / 30f);

        super.update(dt);

        invulnerableRemaining = Math.max(
            0f, invulnerableRemaining - dt
        );

        parryCooldown = Math.max(
            0f, parryCooldown - dt
        );

        parryDuration = Math.max(
            0f, parryDuration - dt
        );

        dashCooldown = Math.max(
            0f, dashCooldown - dt
        );

        float oldX = x;

        boolean dashActiveThisFrame = dashing;

        float horizontalSpeed = dashActiveThisFrame
            ? dashDirection * DASH_SPEED
            : moveDirection * speed;

        x += horizontalSpeed * dt;

        x = MathUtils.clamp(
            x,
            0f,
            Math.max(0f, worldWidth - width)
        );

        if (dashActiveThisFrame) {

            dashTimer -= dt;

            if (dashTimer <= 0f) {
                dashing = false;
                dashTimer = 0f;
            }
        }

        if (platforms != null) {

            for (Platform platform : platforms) {

                Rectangle r = platform.getCoreHitbox();

                if (platform.isOneWay()
                    || y >= r.y + r.height - 1f
                    || y + height <= r.y + 1f) {
                    continue;
                }

                if (x + width > r.x && x < r.x + r.width) {

                    if (horizontalSpeed > 0
                        && oldX + width <= r.x + 1f) {

                        x = r.x - width;
                    }

                    if (horizontalSpeed < 0
                        && oldX >= r.x + r.width - 1f) {

                        x = r.x + r.width;
                    }
                }
            }
        }

        float oldY = y;

        velocityY -= gravity * dt;
        y += velocityY * dt;

        grounded = false;

        if (platforms != null) {

            for (Platform platform : platforms) {

                Rectangle r = platform.getCoreHitbox();

                if (x + width <= r.x || x >= r.x + r.width) {
                    continue;
                }

                float platformTop = r.y + r.height;

                // Landing on platform
                if (velocityY <= 0f
                    && oldY >= platformTop - 1f
                    && y <= platformTop) {

                    y = platformTop;
                    velocityY = 0f;
                    grounded = true;
                }

                // Head collision
                else if (!platform.isOneWay()
                    && velocityY > 0f
                    && oldY + height <= r.y + 1f
                    && y + height >= r.y) {

                    y = r.y - height;
                    velocityY = 0f;
                }
            }
        }

        // Prevent falling forever
        if (y < -250f) {
            takeDamage(999f);
        }
    }

    public void takeDamage(float damage) {

        if (damage <= 0f
            || !active
            || invulnerableRemaining > 0f) {
            return;
        }

        health = Math.max(0f, health - damage);

        invulnerableRemaining = 0.8f;

        if (health <= 0f) {
            destroy();
        }
    }

    public boolean isInvulnerable() {
        return invulnerableRemaining > 0f;
    }

    public float getInvulnerableRemaining() {
        return invulnerableRemaining;
    }

    public void heal(float amount) {

        health = Math.min(
            maxHealth,
            health + Math.max(0f, amount)
        );
    }

    public void reset(float spawnX, float spawnY) {

        x = spawnX;
        y = spawnY;

        velocityY = 0f;
        moveDirection = 0f;

        health = maxHealth;

        grounded = false;
        facingRight = true;

        active = true;

        invulnerableRemaining = 0f;

        parryEnergy = 0f;
        parryCooldown = 0f;
        parryDuration = 0f;

        dashing = false;
        dashTimer = 0f;
        dashCooldown = 0f;
        dashDirection = 1f;
    }

    // GETTERS
    public boolean isGrounded() {
        return grounded;
    }

    public boolean isJumping() {
        return !grounded;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getVelocityY() {
        return velocityY;
    }

    // SETTERS
    public void setJumpForce(float value) {
        if (value > 0f) {
            jumpForce = value;
        }
    }

    public void setGravity(float value) {
        if (value > 0f) {
            gravity = value;
        }
    }

    public void setMaxHealth(float value) {

        if (value > 0f) {
            maxHealth = value;
            health = Math.min(health, maxHealth);
        }
    }

    public void setMaxParryEnergy(float value) {

        if (value > 0f) {
            maxParryEnergy = value;
            parryEnergy = Math.min(parryEnergy, maxParryEnergy);
        }
    }
}
