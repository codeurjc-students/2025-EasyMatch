package es.codeurjc.model;

import jakarta.persistence.*;
import java.util.List;

@Embeddable
public class MatchResult {

    private String team1Name;
    private String team2Name;

    private Integer team1Score;
    private Integer team2Score;

    private Integer team1Sets;
    private Integer team2Sets;

    @ElementCollection
    private List<Integer> team1GamesPerSet;

    @ElementCollection
    private List<Integer> team2GamesPerSet;

    public MatchResult() {}

    public MatchResult(String team1Name, String team2Name, Integer team1Score, Integer team2Score) {
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    public MatchResult(String team1Name, String team2Name, Integer team1Sets, Integer team2Sets,List<Integer> team1GamesPerSetIntegers,
     List<Integer> team2GamesPerSet) {
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1Sets = team1Sets;
        this.team2Sets = team2Sets;
        this.team1GamesPerSet = team1GamesPerSetIntegers;
        this.team2GamesPerSet = team2GamesPerSet;
    }
    

    public String getWinner(ScoringType scoringType) {
        if (scoringType == null) return "Tipo de puntuación no definido";

        return switch (scoringType) {
            case SCORE -> compare(team1Score, team2Score);
            case SETS -> compare(team1Sets, team2Sets);
        };
    }

    private String compare(Integer v1, Integer v2) {
        if (v1 == null || v2 == null) return "Sin resultado";
        if (v1 > v2) return team1Name;
        if (v2 > v1) return team2Name;
        return "Empate";
    }

    public String getResultSummary(ScoringType scoringType) {
        return switch (scoringType) {
            case SCORE -> String.format("%s %d - %d %s",
                    team1Name, team1Score, team2Score, team2Name);
            case SETS -> getSetsSummary();
        };
    }

    private String getSetsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %d sets - %d sets %s",
                team1Name, team1Sets, team2Sets, team2Name));

        if (team1GamesPerSet != null && team2GamesPerSet != null
                && team1GamesPerSet.size() == team2GamesPerSet.size()) {
            sb.append(" [");
            for (int i = 0; i < team1GamesPerSet.size(); i++) {
                sb.append(team1GamesPerSet.get(i))
                  .append("-")
                  .append(team2GamesPerSet.get(i));
                if (i < team1GamesPerSet.size() - 1) sb.append(", ");
            }
            sb.append("]");
        }
        return sb.toString();
    }


    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }

    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }

    public Integer getTeam1Sets() { return team1Sets; }
    public void setTeam1Sets(Integer team1Sets) { this.team1Sets = team1Sets; }

    public Integer getTeam2Sets() { return team2Sets; }
    public void setTeam2Sets(Integer team2Sets) { this.team2Sets = team2Sets; }

    public List<Integer> getTeam1GamesPerSet() { return team1GamesPerSet; }
    public void setTeam1GamesPerSet(List<Integer> team1GamesPerSet) { this.team1GamesPerSet = team1GamesPerSet; }

    public List<Integer> getTeam2GamesPerSet() { return team2GamesPerSet; }
    public void setTeam2GamesPerSet(List<Integer> team2GamesPerSet) { this.team2GamesPerSet = team2GamesPerSet; }

    @Override
    public String toString() {
        return "MatchResult{" +
                "team1Name='" + team1Name + '\'' +
                ", team2Name='" + team2Name + '\'' +
                ", team1Score=" + team1Score +
                ", team2Score=" + team2Score +
                ", team1Sets=" + team1Sets +
                ", team2Sets=" + team2Sets +
                ", team1GamesPerSet=" + team1GamesPerSet +
                ", team2GamesPerSet=" + team2GamesPerSet +
                '}';
    }
}
