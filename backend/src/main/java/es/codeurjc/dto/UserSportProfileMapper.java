package es.codeurjc.dto;

import org.mapstruct.Mapper;

import es.codeurjc.model.PlayerStats;
import es.codeurjc.model.UserSportProfile;

@Mapper(componentModel = "spring")
public class UserSportProfileMapper {

    public static UserSportProfileDTO toDTO(UserSportProfile profile) {

        PlayerStats stats = profile.getStats();

        PlayerStatsDTO statsDTO = new PlayerStatsDTO(
                stats.getTotalMatches(),
                stats.getWins(),
                stats.getDraws(),
                stats.getLosses(),
                stats.getWinRate()
        );

        return new UserSportProfileDTO(
                profile.getId(),
                profile.getSport().getName(),
                profile.getLevel(),
                statsDTO
        );
    }
}