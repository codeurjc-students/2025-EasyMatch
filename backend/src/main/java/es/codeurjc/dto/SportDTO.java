package es.codeurjc.dto;
import java.util.List;

public record SportDTO (
    Long id,
    String name,
    List<ModeDTO> modes
) {
    
}


