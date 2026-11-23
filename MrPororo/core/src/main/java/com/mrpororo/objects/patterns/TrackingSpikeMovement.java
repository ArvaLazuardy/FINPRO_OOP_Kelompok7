package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;

/**
 * Spike jatuh ttapi   homing x dari player:
 * hover fase 1 -> x mengikuti player,
 * hover fase 2 (lockTime terakhir) -> x terkunci (kesempatan menghindar),
 * fall lurus k bawah.
 */
public class TrackingSpikeMovement implements BulletMovementPattern {

    private final Player target;
    private final float hoverTime, lockTime, trackSpeed, fallSpeed, groundY, worldWidth;

    public TrackingSpikeMovement(Player target, float hoverTime, float lockTime, float trackSpeed,
                                 float fallSpeed, float groundY, float worldWidth) {
        this.target = target;
        this.hoverTime = hoverTime;
        this.lockTime = lockTime;
        this.trackSpeed = trackSpeed;
        this.fallSpeed = fallSpeed;
        this.groundY = groundY;
        this.worldWidth = worldWidth;
    }

    @Override
    public void move(Bullet b, float delta) {
        float t = b.getStateTime();

        if (t < hoverTime - lockTime) {            // TRACKING
            if (target != null && !target.isDestroyed()) {
                float goalX = target.getX() + target.getWidth() / 2f - b.getWidth() / 2f;
                float step = trackSpeed * delta;
                float nx = b.getX() + MathUtils.clamp(goalX - b.getX(), -step, step);
                b.setX(MathUtils.clamp(nx, 0f, worldWidth - b.getWidth()));
            }
            return;
        }
        if (t < hoverTime) return;                 // LOCKED (warning)

        b.setY(b.getY() - fallSpeed * delta);      // FALL
        if (b.getY() <= groundY) b.destroy();
    }
}
