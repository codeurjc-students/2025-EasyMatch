package es.codeurjc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public class LevelHistory {

    private Long matchId;
    private LocalDateTime date;
    private float levelBefore;
    private float levelAfter;
    private boolean won;

    protected LevelHistory() {
        // JPA
    }

    public LevelHistory(Long matchId, LocalDateTime date, float levelBefore, float levelAfter, boolean won) {
        this.matchId = matchId;
        this.date = date;
        this.levelBefore = levelBefore;
        this.levelAfter = levelAfter;
        this.won = won;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public float getLevelBefore() {
        return levelBefore;
    }

    public float getLevelAfter() {
        return levelAfter;
    }

    public boolean isWon() {
        return won;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLevelBefore(float levelBefore) {
        this.levelBefore = levelBefore;
    }

    public void setLevelAfter(float levelAfter) {
        this.levelAfter = levelAfter;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
    
}
