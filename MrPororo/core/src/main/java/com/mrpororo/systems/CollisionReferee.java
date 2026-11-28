package com.mrpororo.systems;

import com.badlogic.gdx.math.Rectangle;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.enemies.Enemy;

import java.util.List;

public class CollisionReferee {
    private static final float CONTACT_COOLDOWN = 0.75f;
    private float contactCooldownRemaining;

    public void update(float delta) {
        contactCooldownRemaining = Math.max(0f, contactCooldownRemaining - Math.max(0f, delta));
    }

    public void reset() {
        contactCooldownRemaining = 0f;
    }

    public boolean resolveParries(Rectangle parryHitbox, List<Bullet> enemyBullets, ParrySystem parrySystem) {
        if (parryHitbox == null || enemyBullets == null || parrySystem == null || !parrySystem.isParryActive()) return false;

        for (Bullet bullet : enemyBullets) {
            if (bullet.isDestroyed() || !bullet.isParryable()) continue;
            if (parryHitbox.overlaps(bullet.getCoreHitbox()) && parrySystem.registerSuccess()) {
                bullet.destroy();
                return true;
            }
        }
        return false;
    }

    public boolean resolvePlayerHits(Player player, List<Bullet> enemyBullets) {
        if (player == null || player.isDestroyed()) return false;
        Rectangle playerBounds = player.getCoreHitbox();
        boolean hit = false;
        for (Bullet bullet : enemyBullets) {
            if (bullet.isDestroyed() || bullet.isHarmless() || !playerBounds.overlaps(bullet.getCoreHitbox())) continue;
            player.takeDamage(bullet.getDamage());
            bullet.destroy();
            hit = true;
        }
        return hit;
    }

    public int resolveEnemyHits(
        List<Bullet> playerBullets,
        List<? extends Enemy> enemies) {

        if (playerBullets == null || enemies == null) return 0;

        int totalDamage = 0;

        for (Bullet bullet : playerBullets) {
            if (bullet.isDestroyed() || !bullet.isPlayerBullet() || bullet.getDamage() <= 0) continue;

            for (Enemy enemy : enemies) {
                if (enemy == null || enemy.isDestroyed() || enemy.isDead()) continue;
                if (!bullet.getCoreHitbox().overlaps(enemy.getCoreHitbox())) continue;

                int previousHealth = enemy.getHealth();
                enemy.takeDamage(bullet.getDamage());
                totalDamage += Math.max(0, previousHealth - enemy.getHealth());
                bullet.destroy();
                break;
            }
        }

        return totalDamage;
    }

    public boolean resolveEnemyContact(Player player, List<? extends Enemy> enemies) {
        if (player == null || player.isDestroyed() || enemies == null || contactCooldownRemaining > 0f) return false;
        Rectangle playerBounds = player.getCoreHitbox();
        for (Enemy enemy : enemies) {
            if (enemy == null || enemy.isDestroyed() || enemy.isDead() || enemy.getDamage() <= 0) continue;
            if (playerBounds.overlaps(enemy.getCoreHitbox())) {
                player.takeDamage(enemy.getDamage());
                contactCooldownRemaining = CONTACT_COOLDOWN;
                return true;
            }
        }
        return false;
    }

}
