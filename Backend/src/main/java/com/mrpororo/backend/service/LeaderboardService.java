package com.mrpororo.backend.service;

import com.mrpororo.backend.dto.LeaderboardRequest;
import com.mrpororo.backend.model.LeaderboardEntry;
import com.mrpororo.backend.repository.LeaderboardRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LeaderboardService {
    private final LeaderboardRepository repository;

    public LeaderboardService(LeaderboardRepository repository) {
        this.repository = repository;
    }

    public LeaderboardEntry submitRecord(LeaderboardRequest request) {

        if (request.completionTimeMs() > request.realTimeMs()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Completion time cannot exceed real time"
            );
        }

        LeaderboardEntry entry = new LeaderboardEntry(
                request.playerName().trim(),
                request.completionTimeMs(),
                request.realTimeMs(),
                request.successfulParries(),
                request.freezeCount()
        );

        return repository.save(entry);
    }

    public List<LeaderboardEntry> getLeaderboard() {

        return repository.findTop100ByOrderByCompletionTimeMsAscRealTimeMsAscIdAsc();
    }
}
