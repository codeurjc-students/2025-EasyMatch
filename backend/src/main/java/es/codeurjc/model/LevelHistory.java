package es.codeurjc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public class LevelHistory {

    private LocalDateTime date;
    private float levelBefore;
    private float levelAfter;
    private boolean won;

    protected LevelHistory() {
        // JPA
    }

    public LevelHistory(LocalDateTime date, float levelBefore, float levelAfter, boolean won) {
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
}
