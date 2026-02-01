package es.codeurjc.dto;

import java.util.List;

public record MatchResultDTO(
    String team1Name,
    String team2Name,
    Integer team1Score,
    Integer team2Score,
    List<Integer> team1GamesPerSet,
    List<Integer> team2GamesPerSet
){
    
}