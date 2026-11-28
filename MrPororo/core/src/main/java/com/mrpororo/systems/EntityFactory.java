package com.mrpororo.systems;

import com.mrpororo.objects.Player;
import com.mrpororo.objects.Platform;
import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.bullets.BulletType;
import com.mrpororo.objects.enemies.SnowGolem;

public final class EntityFactory {
    private EntityFactory() {}

    public static Player createPlayer(float x, float y) {
        return new Player(x, y); // no player sprite for now
    }

    public static SnowGolem createBoss(float x, float y) {
        return new SnowGolem(x, y);
    }

    public static Platform createPlatform(float x, float y, float width, float height, String spriteKey) {
        return new Platform(x, y, width, height,
            spriteKey == null ? null : AssetManager.getInstance().getRegion(spriteKey),
            AssetManager.SNOW_LEDGE.equals(spriteKey));
    }

    public static Bullet createBullet() {
        return new Bullet(); // called only when an object pool has no free bullets
    }

    public static Bullet createBullet(float x, float y, float vx, float vy, int damage, BulletType type) {
        Bullet bullet = createBullet();
        bullet.init(x, y, vx, vy, damage, type);
        return bullet;
    }
}
