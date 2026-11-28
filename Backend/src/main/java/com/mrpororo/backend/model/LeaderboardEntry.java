package com.mrpororo.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard_entries")
public class LeaderboardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 24)
    private String playerName;

    @Column(nullable = false)
    private long completionTimeMs;

    @Column(nullable = false)
    private long realTimeMs;

    private int successfulParries;
    private int freezeCount;

    private LocalDateTime createdAt;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String playerName, long completionTimeMs, long realTimeMs, int successfulParries, int freezeCount) {
        this.playerName = playerName;
        this.completionTimeMs = completionTimeMs;
        this.realTimeMs = realTimeMs;
        this.successfulParries = successfulParries;
        this.freezeCount = freezeCount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getCompletionTimeMs() {
        return completionTimeMs;
    }

    public long getRealTimeMs() {
        return realTimeMs;
    }

    public int getSuccessfulParries() {
        return successfulParries;
    }

    public int getFreezeCount() {
        return freezeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
