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
    String sport,
    BasicClubDTO club,
    List<BasicUserDTO> players
) {
    
}
