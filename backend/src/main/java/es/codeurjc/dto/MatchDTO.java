package es.codeurjc.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record MatchDTO (
    Long id,
    LocalDateTime date,
    Boolean type,
    Boolean isPrivate,
    Boolean state,
    Integer modeSelected,
    BasicUserDTO organizer,
    Float price,
    SportDTO sport,
    BasicClubDTO club,
    Set<UserDTO> team1Players,
    Set<UserDTO> team2Players,
    MatchResultDTO result
) {
    public MatchDTO(Long id, LocalDateTime date,
        Boolean type,
        Boolean isPrivate,
        Boolean state,
        Integer modeSelected,
        BasicUserDTO organizer,
        Float price,
        SportDTO sport,
        BasicClubDTO club,
        Set<UserDTO> team1Players,
        Set<UserDTO> team2Players) {
            this(id, date, type, isPrivate, state,modeSelected, organizer, price, sport, club, team1Players,team2Players, null);
    }
}
