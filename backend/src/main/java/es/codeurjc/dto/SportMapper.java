package es.codeurjc.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.model.Sport;

@Mapper(componentModel = "spring")
public interface SportMapper {
    
    SportDTO toDTO(Sport sport);

    List<SportDTO> toDTOs(Collection<Sport> sports);

    Sport toDomain(SportDTO sportDTO);
    
}
