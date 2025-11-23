package es.codeurjc.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MatchDTO (
    Long id,
    LocalDateTime date,
    Boolean type,
    Boolean isPrivate,
    Boolean state,
    BasicUserDTO organizer,
    Float price,
    SportDTO sport,
    BasicClubDTO club,
    List<UserDTO> team1Players,
    List<UserDTO> team2Players,
    MatchResultDTO result
) {
    public MatchDTO(Long id, LocalDateTime date,
        Boolean type,
        Boolean isPrivate,
        Boolean state,
        BasicUserDTO organizer,
        Float price,
        SportDTO sport,
        BasicClubDTO club,
        List<UserDTO> team1Players,
        List<UserDTO> team2Players) {
            this(id, date, type, isPrivate, state, organizer, price, sport, club, team1Players,team2Players, null);
    }
}
