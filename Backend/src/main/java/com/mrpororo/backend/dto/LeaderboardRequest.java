package com.mrpororo.backend.dto;

import jakarta.validation.constraints.*;

public record LeaderboardRequest(

        @NotBlank
        @Size(max = 24)
        String playerName,

        @NotNull
        @Positive
        Long completionTimeMs,

        @NotNull
        @Positive
        Long realTimeMs,

        @NotNull
        @PositiveOrZero
        Integer successfulParries,

        @NotNull
        @PositiveOrZero
        Integer freezeCount

) {}
