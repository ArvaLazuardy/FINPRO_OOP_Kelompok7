package com.mrpororo.backend.controller;

import com.mrpororo.backend.dto.LeaderboardRequest;
import com.mrpororo.backend.model.LeaderboardEntry;
import com.mrpororo.backend.service.LeaderboardService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    private final LeaderboardService service;

    public LeaderboardController(LeaderboardService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "online");
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        return service.getLeaderboard();
    }

    @PostMapping("/leaderboard")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaderboardEntry submitRecord(@Valid @RequestBody LeaderboardRequest request) {
        return service.submitRecord(request);
    }
}
