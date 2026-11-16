package com.mrpororo.systems;

public class TimeManager {
    private final float freezeDuration;
    private float freezeRemaining;

    public TimeManager() {
        this(3f);
    }

    public TimeManager(float freezeDuration) {
        if (freezeDuration <= 0f) {
            throw new IllegalArgumentException("freezeDuration must be positive");
        }
        this.freezeDuration = freezeDuration;
    }

    public boolean activateFreeze() {
        if (isFrozen()) return false;
        freezeRemaining = freezeDuration;
        return true;
    }

    public void update(float realDelta) {
        freezeRemaining = Math.max(0f, freezeRemaining - Math.max(0f, realDelta));
    }

    public boolean isFrozen() { return freezeRemaining > 0f; }
    public float getEnemyDelta(float realDelta) {
        return isFrozen() ? 0f : Math.max(0f, realDelta);
    }
    public float getRemainingTime() { return freezeRemaining; }
    public float getFreezeDuration() { return freezeDuration; }
    public void reset() { freezeRemaining = 0f; }
}
