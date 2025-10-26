package es.codeurjc.dto;

import java.time.LocalDateTime;

public record MatchDTO (
    Long id,
    LocalDateTime date,
    Boolean type,
    Boolean isPrivate,
    Boolean state,
    BasicUserDTO organizer,
    String sport
) {
    
}
