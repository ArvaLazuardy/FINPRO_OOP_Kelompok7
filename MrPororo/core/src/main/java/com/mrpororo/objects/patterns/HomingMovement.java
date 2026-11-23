package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.bullets.Bullet;

// TODO: PELAJARIN!!
public class HomingMovement implements BulletMovementPattern {
    private final Player target;
    private final float speed;
    private final float turnRate;   // radian/Sec
    private final float lifetime;   // in sec

    public HomingMovement(Player target, float speed, float turnRate, float lifetime) {
        this.target = target;
        this.speed = speed;
        this.turnRate = turnRate;
        this.lifetime = lifetime;
    }

    @Override
    public void move(Bullet b, float delta) {
        if (b.getStateTime() >= lifetime) {
            b.destroy();
            return;
        }
        float angle = MathUtils.atan2(b.getVy(), b.getVx());

        if (target != null && !target.isDestroyed()) {
            float dx = (target.getX() + target.getWidth() / 2f) - (b.getX() + b.getWidth() / 2f);
            float dy = (target.getY() + target.getHeight() / 2f) - (b.getY() + b.getHeight() / 2f);
            float diff = MathUtils.atan2(dy, dx) - angle;
            while (diff > MathUtils.PI) diff -= MathUtils.PI2;
            while (diff < -MathUtils.PI) diff += MathUtils.PI2;
            float maxTurn = turnRate * delta;
            angle += MathUtils.clamp(diff, -maxTurn, maxTurn);
        }

        float vx = speed * MathUtils.cos(angle);
        float vy = speed * MathUtils.sin(angle);

        b.setVx(vx);
        b.setVy(vy);
        b.setX(b.getX() + vx * delta);
        b.setY(b.getY() + vy * delta);
    }
}
