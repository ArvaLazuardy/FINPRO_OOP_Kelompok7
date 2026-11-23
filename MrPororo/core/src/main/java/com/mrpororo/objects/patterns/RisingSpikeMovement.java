package com.mrpororo.objects.patterns;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.bullets.Bullet;

/**
 * FLOOR SPIKE :
 * WARNING
 * -> RISE  -> HOLD -> RETRACT  -> destroyy
 * Bullet.y tettap di bawah, yang berubah hanya height.
 */
public class RisingSpikeMovement implements BulletMovementPattern {

    private static final Color WARN_COLOR = new Color(1f, 0.85f, 0.25f, 1f);
    private static final Color DANGER_COLOR = new Color(0.75f, 0.95f, 1f, 1f);

    private final float baseX;
    private final float warnTime, riseTime, holdTime, retractTime;
    private final float stubHeight, maxHeight;

    public RisingSpikeMovement(float baseX, float warnTime, float riseTime, float holdTime,
                               float retractTime, float stubHeight, float maxHeight) {
        this.baseX = baseX;
        this.warnTime = warnTime;
        this.riseTime = riseTime;
        this.holdTime = holdTime;
        this.retractTime = retractTime;
        this.stubHeight = stubHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public void move(Bullet b, float delta) {
        float t = b.getStateTime();
        float riseEnd = warnTime + riseTime;
        float holdEnd = riseEnd + holdTime;
        float end = holdEnd + retractTime;

        if (t < warnTime) {                       // WARNING:
            b.setHarmless(true);
            b.setColor(WARN_COLOR);
            b.setX(baseX + MathUtils.sin(t * 70f) * 3f);
            b.setHeight(stubHeight);
        } else if (t < riseEnd) {                 // RISE: DANGER
            b.setHarmless(false);
            b.setColor(DANGER_COLOR);
            b.setX(baseX);
            float p = (t - warnTime) / riseTime;
            b.setHeight(stubHeight + (maxHeight - stubHeight) * p);
        } else if (t < holdEnd) {                 // HOLD
            b.setHeight(maxHeight);
        } else if (t < end) {                     // RETRACT: SAFE!
            b.setHarmless(true);
            float p = (t - holdEnd) / retractTime;
            b.setHeight(Math.max(1f, maxHeight * (1f - p)));
        } else {
            b.destroy();
        }
    }
}
