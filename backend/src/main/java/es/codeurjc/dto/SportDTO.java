package es.codeurjc.dto;
import java.util.List;

import es.codeurjc.model.ScoringType;

public record SportDTO (
    Long id,
    String name,
    List<ModeDTO> modes,
    ScoringType scoringType
) {
    
}


