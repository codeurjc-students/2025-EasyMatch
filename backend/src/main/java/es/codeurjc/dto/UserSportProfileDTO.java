package es.codeurjc.dto;

public record UserSportProfileDTO(
        String sportName,
        float level,
        PlayerStatsDTO stats
) {}