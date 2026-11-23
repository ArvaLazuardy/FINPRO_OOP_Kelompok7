package com.mrpororo.objects.patterns;

import com.badlogic.gdx.math.MathUtils;
import com.mrpororo.objects.Player;
import com.mrpororo.objects.enemies.SnowGolem;

// Ring of bullets with a gap so player can doge + 1 parryable bullet
public class RingShotAttack extends BossAttackPattern {
    private static final int RINGS = 3;
    private static final int BULLETS_PER_RING = 40;
    private static final int GAP_BULLETS = 0;          // jumlah peluru yang dihapus untuk gap
    private static final float GAP_AIM_JITTER_DEG = 1f; // gap ~ to  player +- jitter
    private static final float WINDUP = 0.9f;
    private static final float INTERVAL = 1.1f;
    private static final float SPEED = 230f;
    private static final int DAMAGE = 10;
    private static final float SIZE = 25f;

    private int ringsFired;

    public RingShotAttack() { super(1.6f); }

    @Override
    protected void onStart(SnowGolem boss) { ringsFired = 0; }

    @Override
    protected void onUpdate(SnowGolem boss, float delta) {
        if (ringsFired < RINGS && timer >= WINDUP + ringsFired * INTERVAL) {
            fireRing(boss);
            ringsFired++;
        }
        if (ringsFired >= RINGS && timer >= WINDUP + (RINGS - 1) * INTERVAL + 1.2f) finished = true;
    }

    private void fireRing(SnowGolem boss) {
        float cx = boss.getCenterX(), cy = boss.getCenterY();
        float step = MathUtils.PI2 / BULLETS_PER_RING;
        float startOffset = MathUtils.random(step);

        // arah gap: kira-kira ke player, digeser acak -> tiap ring gap-nya "bergeser"
        float gapCenter = MathUtils.PI;
        Player p = boss.getTarget();
        if (p != null) {
            float dx = p.getX() + p.getWidth() / 2f - cx;
            float dy = p.getY() + p.getHeight() / 2f - cy;
            gapCenter = MathUtils.atan2(dy, dx);
        }
        gapCenter += MathUtils.random(-GAP_AIM_JITTER_DEG, GAP_AIM_JITTER_DEG) * MathUtils.degreesToRadians;
        float gapHalf = GAP_BULLETS * step / 2f;

        // pilih 1 peluru parryable dari yang tidak kena gap
        boolean[] inGap = new boolean[BULLETS_PER_RING];
        int open = 0;
        for (int i = 0; i < BULLETS_PER_RING; i++) {
            float a = startOffset + i * step;
            inGap[i] = Math.abs(angleDiff(a, gapCenter)) < gapHalf;
            if (!inGap[i]) open++;
        }
        int parryPick = MathUtils.random(open - 1); // NOTICE ME (PARRY)! cuma 1 per ring
        int openIdx = 0;

        for (int i = 0; i < BULLETS_PER_RING; i++) {
            if (inGap[i]) continue;
            float a = startOffset + i * step;
            boolean parry = (openIdx == parryPick);
            openIdx++;
            spawnBullet(boss, cx - SIZE / 2f, cy - SIZE / 2f,
                SPEED * MathUtils.cos(a), SPEED * MathUtils.sin(a), DAMAGE, parry, SIZE, SIZE);
        }
    }

    /** Selisih sudut ter-normalisasi ke [-PI, PI]. */
    private static float angleDiff(float a, float b) {
        float d = a - b;
        while (d > MathUtils.PI) d -= MathUtils.PI2;
        while (d < -MathUtils.PI) d += MathUtils.PI2;
        return d;
    }

    //DEBUG
    @Override public String getName() { return "Pattern 3 - Ring Shot"; }
}
