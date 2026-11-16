package com.mrpororo.systems;

import com.badlogic.gdx.math.Rectangle;
import com.mrpororo.objects.bullets.Bullet;
import java.util.List;

public class CollisionReferee {

    public boolean resolveParries(Rectangle parryHitbox, List<Bullet> enemyBullets, ParrySystem parrySystem) {
        if (parryHitbox == null || enemyBullets == null || parrySystem == null || !parrySystem.isParryActive()) {
            return false;
        }

        for (Bullet bullet : enemyBullets) {
            if (bullet.isDestroyed() || !bullet.isParryable()) {
                continue;
            }

            if (parryHitbox.overlaps(bullet.getCoreHitbox()) && parrySystem.registerSuccess()) {
                bullet.destroy();
                return true;
            }
        }

        return false;
    }
}
