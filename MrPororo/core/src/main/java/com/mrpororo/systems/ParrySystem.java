package com.mrpororo.systems;

public class ParrySystem {
    private final float maxEnergy;
    private final float energyPerParry;
    private final float parryDuration;
    private final float parryCooldown;

    private float energy;
    private float parryWindowTimer;
    private float cooldownTimer;
    private boolean rewardedThisWindow;

    public ParrySystem() {
        this(100f, 25f, 0.20f, 0.50f);
    }

    public ParrySystem(float maxEnergy, float energyPerParry, float parryDuration, float parryCooldown) {
        this.maxEnergy = maxEnergy;
        this.energyPerParry = energyPerParry;
        this.parryDuration = parryDuration;
        this.parryCooldown = parryCooldown;
    }

    public void update(float delta) {
        float dt = Math.max(0f, delta);
        parryWindowTimer = Math.max(0f, parryWindowTimer - dt);
        cooldownTimer = Math.max(0f, cooldownTimer - dt);
    }

    public boolean beginParry() {
        if (cooldownTimer > 0f || parryWindowTimer > 0f) {
            return false;
        }

        parryWindowTimer = parryDuration;
        cooldownTimer = parryCooldown;
        rewardedThisWindow = false;
        return true;
    }

    public boolean isParryActive() {
        return parryWindowTimer > 0f && !rewardedThisWindow;
    }

    public boolean registerSuccess() {
        if (!isParryActive()) {
            return false;
        }

        rewardedThisWindow = true;
        parryWindowTimer = 0f;

        energy = Math.min(maxEnergy, energy + energyPerParry);

        return true;
    }

    public boolean isMeterFull() {
        return energy >= maxEnergy;
    }

    public boolean consumeFullMeter() {
        if (!isMeterFull()) {
            return false;
        }

        energy = 0f;
        return true;
    }

    public float getEnergy() { return energy; }
    public float getMaxEnergy() { return maxEnergy; }
    public float getEnergyPercentage() { return energy / maxEnergy; }
    public float getRemainingParryWindow() { return parryWindowTimer; }

    public void reset() {
        energy = 0f;
        parryWindowTimer = 0f;
        cooldownTimer = 0f;
        rewardedThisWindow = false;
    }
}
