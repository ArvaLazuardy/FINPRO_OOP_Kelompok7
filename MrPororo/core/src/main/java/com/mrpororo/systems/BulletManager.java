package com.mrpororo.systems;

import com.mrpororo.objects.bullets.Bullet;
import com.mrpororo.objects.bullets.BulletType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class BulletManager {
    private final Deque<Bullet> playerPool = new ArrayDeque<>();
    private final Deque<Bullet> enemyPool = new ArrayDeque<>();
    private final List<Bullet> activePlayerBullets = new ArrayList<>();
    private final List<Bullet> activeEnemyBullets = new ArrayList<>();

    private Bullet obtain(boolean playerOwned) {
        Deque<Bullet> pool = playerOwned ? playerPool : enemyPool;
        return pool.isEmpty() ? EntityFactory.createBullet() : pool.pop();
    }

    public Bullet spawnPlayerBullet(float x, float y, float vx, float vy, int damage) {
        Bullet bullet = obtain(true);
        bullet.init(x, y, vx, vy, damage, BulletType.PLAYER);
        activePlayerBullets.add(bullet);
        return bullet;
    }

    public Bullet spawnEnemyBullet(float x, float y, float vx, float vy, int damage, boolean parryable) {
        Bullet bullet = obtain(false);
        bullet.init(x, y, vx, vy, damage,
            parryable ? BulletType.ENEMY_PARRYABLE : BulletType.ENEMY_NORMAL);
        activeEnemyBullets.add(bullet);
        return bullet;
    }

    // differentiate between enemy and player deltas
    public void update(float playerDelta, float enemyDelta, float worldWidth, float worldHeight) {
        for (Bullet b : activePlayerBullets) b.update(playerDelta);
        for (Bullet b : activeEnemyBullets) b.update(enemyDelta);
        cleanup(worldWidth, worldHeight);
    }

    public void cleanup(float worldWidth, float worldHeight) {
        recycleDead(activePlayerBullets, playerPool, worldWidth, worldHeight);
        recycleDead(activeEnemyBullets, enemyPool, worldWidth, worldHeight);
    }

    private void recycleDead(List<Bullet> active, Deque<Bullet> pool, float w, float h) {
        for (int i = active.size() - 1; i >= 0; i--) {
            Bullet b = active.get(i);
            if (b.isDestroyed() || b.getX() < -40f || b.getX() > w + 40f || b.getY() < -40f || b.getY() > h + 40f) {
                b.destroy();
                active.remove(i);
                pool.push(b);
            }
        }
    }

    public List<Bullet> getActivePlayerBullets() {
        return Collections.unmodifiableList(activePlayerBullets);
    }
    public List<Bullet> getActiveEnemyBullets() {
        return Collections.unmodifiableList(activeEnemyBullets);
    }
    public int getPlayerPoolSize() { return playerPool.size(); }
    public int getEnemyPoolSize() { return enemyPool.size(); }

    public void clear() {
        for (Bullet b : activePlayerBullets) { b.destroy(); playerPool.push(b); }
        for (Bullet b : activeEnemyBullets) { b.destroy(); enemyPool.push(b); }
        activePlayerBullets.clear();
        activeEnemyBullets.clear();
    }
}
