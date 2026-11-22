package com.mrpororo.systems;

import com.badlogic.gdx.math.Rectangle;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;
import java.util.List;

public class CollisionReferee {
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

    // add enemy hit later
}
