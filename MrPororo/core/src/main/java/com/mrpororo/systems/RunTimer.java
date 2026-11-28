package com.mrpororo.systems;

/** One timer for entire run */
public class RunTimer {
    private double realSeconds;
    private double activeSeconds;
    private int successfulParries;
    private int freezeCount;
    private boolean running;

    public void start() {
        realSeconds = activeSeconds = 0;
        successfulParries = freezeCount = 0;
        running = true;
    }

    public void update(float delta, float freezeRemaining) {
        if (!running || !Float.isFinite(delta) || delta <= 0f) return;
        double frozenSeconds = Math.min(delta, Math.max(0f, freezeRemaining));
        realSeconds += delta;
        activeSeconds += delta - frozenSeconds;
    }

    public void registerParry() { if (running) successfulParries++; }
    public void registerFreeze() { if (running) freezeCount++; }
    public void stop() { running = false; }
    public long getRealTimeMs() { return Math.round(realSeconds * 1000); }
    public long getCompletionTimeMs() { return Math.round(activeSeconds * 1000); }
    public int getSuccessfulParries() { return successfulParries; }
    public int getFreezeCount() { return freezeCount; }

    public static String formatTime(long ms) {
        long minutes = ms / 60000;
        long seconds = (ms / 1000) % 60;
        long hundredths = (ms / 10) % 100;
        return twoDigits(minutes) + ":" + twoDigits(seconds) + "." + twoDigits(hundredths);
    }

    private static String twoDigits(long n) { return n < 10 ? "0" + n : Long.toString(n); }
}
