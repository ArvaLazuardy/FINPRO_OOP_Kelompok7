package com.mrpororo.objects.patterns;

import com.mrpororo.objects.bullets.Bullet;

public class FallingSpikeMovement implements BulletMovementPattern {


    private final float hoverTime;
    private final float fallSpeed;
    private final float groundY;

    public FallingSpikeMovement(float hoverTime, float fallSpeed, float groundY) {
        this.hoverTime = hoverTime;
        this.fallSpeed = fallSpeed;
        this.groundY = groundY;
    }

    @Override
    public void move(Bullet bullet, float delta) {
        if (bullet.getStateTime() < hoverTime) return; // THIS' THE WARNING PHASE!

        bullet.setY(bullet.getY() - fallSpeed * delta);
        if (bullet.getY() <= groundY) {
            bullet.destroy();
        }
    }

}
