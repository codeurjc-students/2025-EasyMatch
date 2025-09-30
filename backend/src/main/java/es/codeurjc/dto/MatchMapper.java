package es.codeurjc.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.model.Match;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    MatchDTO toDTO(Match match);

    List<MatchDTO> toDTOs(Collection<Match> products);

    Match toDomain(MatchDTO matchDTO);
} 
