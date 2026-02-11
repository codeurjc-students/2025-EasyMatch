package es.codeurjc.dto;

import java.time.LocalDateTime;

public record LevelHistoryDTO(
    LocalDateTime date,
    float levelBefore,
    float levelAfter,
    boolean won
) {}