package es.codeurjc.dto;

public record PlayerStatsDTO(
    int totalMatches,
    int wins,
    int draws,
    int losses,
    double winRate
) {}