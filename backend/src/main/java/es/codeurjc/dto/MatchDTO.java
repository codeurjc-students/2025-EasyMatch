package es.codeurjc.dto;

import java.time.LocalDateTime;
import java.util.List;

import es.codeurjc.model.Sport;

public record MatchDTO (
    Long id,
    LocalDateTime date,
    Boolean type,
    Boolean isPrivate,
    Boolean state,
    BasicUserDTO organizer,
    Float price,
    Sport sport,
    BasicClubDTO club,
    List<BasicUserDTO> players
) {
    
}
