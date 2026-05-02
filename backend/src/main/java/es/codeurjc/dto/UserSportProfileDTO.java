package es.codeurjc.dto;

public record UserSportProfileDTO(
        Long id,
        String sportName,
        float level,
        PlayerStatsDTO stats
) {}