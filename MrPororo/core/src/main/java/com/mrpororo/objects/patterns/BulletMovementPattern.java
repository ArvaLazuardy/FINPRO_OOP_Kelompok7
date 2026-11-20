package com.mrpororo.patterns;

import com.mrpororo.objects.bullets.Bullet;

public interface BulletMovementPattern {

    void move(Bullet bullet, float delta);
}
