package com.mrpororo.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mrpororo.systems.AssetManager;
import com.mrpororo.systems.BulletManager;
import com.mrpororo.systems.SoundManager;

import java.util.List;

public class Player extends GameObject {

    private static final float LEGACY_FLOOR_Y = 88f; // dipakai hanya jika tidak ada daftar platform

    private float health;
    private float maxHealth;

    private float velocityY;
    private float gravity;
    private float jumpForce;
    private float worldWidth = 960f;

    private boolean isGrounded;
    private boolean isJumping;

    private static final float SHOOT_COOLDOWN = 0.3f;
    private static final float BULLET_SPEED = 450f;
    private static final int BULLET_DAMAGE = 10;
    private static final float SHOOT_ANIMATION_DURATION = 0.16f;
    private static final float WALK_SOUND_INTERVAL = 0.20f;
    private float walkSoundTimer;
    private float shootTimer;
    private float shootAnimationRemaining;
    private float jumpAnimationTime;
    private float movementAnimationTime;
    private float dyingTime;
    private boolean facingRight = true;
    private boolean moving;

    public Player(float x, float y) {
        super(x, y, 48f, 64f, 250f, Color.BLUE);

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
        update(delta, null, null);
    }

    /** Update dengan collision terhadap platform (mendarat dari atas). */
    public void update(float delta, List<Platform> platforms) {
        update(delta, platforms, null);
    }

    public void update(float delta, List<Platform> platforms, BulletManager bulletManager) {
        super.update(delta);
        if (!active) return;

        shootTimer = Math.max(0f, shootTimer - delta);
        shootAnimationRemaining = Math.max(0f, shootAnimationRemaining - delta);

        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean wasMoving = moving;
        moving = left != right;
        if (moving) {
            facingRight = right;
            x += (facingRight ? speed : -speed) * delta;
        }
        movementAnimationTime = wasMoving == moving ? movementAnimationTime + delta : 0f;

        // Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isGrounded) {
            velocityY = jumpForce;
            isGrounded = false;
            isJumping = true;
            jumpAnimationTime = 0f;
            SoundManager.getInstance().playPlayerJump();
        }

        // Gravity
        float prevY = y;
        velocityY -= gravity * delta;
        y += velocityY * delta;

        // World boundaries
        if (x < 0) x = 0;
        if (x + width > worldWidth) x = worldWidth - width;

        // Ground / platform collision
        boolean wasGrounded = isGrounded;
        isGrounded = false;
        if (platforms == null) {
            if (y <= LEGACY_FLOOR_Y) land(LEGACY_FLOOR_Y);
        } else if (velocityY <= 0f) {
            for (Platform p : platforms) {
                Rectangle r = p.getCoreHitbox();
                float top = r.y + r.height;
                boolean overlapX = x + width > r.x && x < r.x + r.width;
                if (overlapX && prevY >= top - 0.01f && y <= top) {
                    land(top);
                    break;
                }
            }
        }
        if (!isGrounded) {
            if (wasGrounded) jumpAnimationTime = 0f;
            else jumpAnimationTime += delta;
        } else {
            jumpAnimationTime = 0f;
        }

        // Footsteps only appear when actually moving on the ground, not while midair.
        if (isGrounded && moving) {
            walkSoundTimer -= delta;
            if (walkSoundTimer <= 0f) {
                SoundManager.getInstance().playPlayerWalk();
                walkSoundTimer = WALK_SOUND_INTERVAL;
            }
        } else {
            walkSoundTimer = 0f;
        }

        // Use the existing pooled BulletManager; holding J repeats at the cooldown.
        if (bulletManager != null && Gdx.input.isKeyPressed(Input.Keys.J) && shootTimer <= 0f) {
            float bulletX = facingRight ? x + width : x - 16f;
            float bulletY = y + height * 0.55f - 8f;
            bulletManager.spawnPlayerBullet(bulletX, bulletY,
                facingRight ? BULLET_SPEED : -BULLET_SPEED, 0f, BULLET_DAMAGE);
            SoundManager.getInstance().playPlayerShoot();
            shootTimer = SHOOT_COOLDOWN;
            shootAnimationRemaining = SHOOT_ANIMATION_DURATION;
        }
    }

    private TextureRegion getCurrentFrame() {
        AssetManager assets = AssetManager.getInstance();
        Animation<TextureRegion> anim;
        if (!active) {
            anim = assets.getAnimation(AssetManager.PLAYER_DYING);
            return anim == null ? null : anim.getKeyFrame(dyingTime, false);
        }
        if (shootAnimationRemaining > 0f) {
            anim = assets.getAnimation(AssetManager.PLAYER_SHOOT);
            if (anim != null) return anim.getKeyFrame(SHOOT_ANIMATION_DURATION - shootAnimationRemaining, false);
        }
        if (!isGrounded) {
            anim = assets.getAnimation(AssetManager.PLAYER_JUMP);
            if (anim != null) return anim.getKeyFrame(jumpAnimationTime, false);
        }
        anim = assets.getAnimation(moving ? AssetManager.PLAYER_WALK : AssetManager.PLAYER_IDLE);
        if (anim == null) anim = assets.getAnimation(AssetManager.PLAYER_IDLE);
        return anim == null ? null : anim.getKeyFrame(movementAnimationTime, true);
    }

    public void updateDeathAnimation(float delta) {
        if (!active) dyingTime += Math.max(0f, delta);
    }

    public boolean hasVisual() {
        return getCurrentFrame() != null;
    }

    public boolean isFacingRight() { return facingRight; }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getCurrentFrame();
        if (batch == null || frame == null) return;

        // Original art is 1254x1254 with transparent padding. Ground the
        // visible feet (~1034px from the top) at the existing 48x64 hitbox.
        float drawHeight = height * (1254f / 819f);
        float drawWidth = drawHeight * frame.getRegionWidth() / frame.getRegionHeight();
        float drawX = x + (width - drawWidth) / 2f;
        float drawY = y - drawHeight * (220f / 1254f);

        // The TextureRegion overload handles vertical UVs. Mirror only X.
        if (facingRight) batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
        else batch.draw(frame, drawX + drawWidth, drawY, -drawWidth, drawHeight);
    }

    public Rectangle getParryHitbox() {
        float reach = 15f;

        return new Rectangle(
            x - reach,
            y - reach,
            width + (reach * 2),
            height + (reach * 2)
        );
    }

    private void land(float surfaceY) {
        y = surfaceY;
        velocityY = 0f;
        isGrounded = true;
        isJumping = false;
    }

    public void takeDamage(float damage) {
        if (damage <= 0 || !active) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            destroy();
        }

        SoundManager.getInstance().playPlayerHit();
    }

    public void heal(float amount) {
        if (amount <= 0 || !active) return;
        health = Math.min(health + amount, maxHealth);
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public boolean isGrounded() { return isGrounded; }
    public boolean isJumping() { return isJumping; }

    public void setWorldWidth(float worldWidth) {
        if (worldWidth > 0) this.worldWidth = worldWidth;
    }

    public void setMaxHealth(float maxHealth) {
        if (maxHealth > 0) {
            this.maxHealth = maxHealth;
            health = Math.min(health, maxHealth);
        }
    }

    public void setJumpForce(float jumpForce) {
        if (jumpForce > 0) this.jumpForce = jumpForce;
    }

    public void setGravity(float gravity) {
        if (gravity > 0) this.gravity = gravity;
    }
}
