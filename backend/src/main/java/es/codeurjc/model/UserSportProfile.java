package es.codeurjc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserSportProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Sport sport;

    private float level;

    @Embedded
    private PlayerStats stats = new PlayerStats();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_sport_level_history",
        joinColumns = @JoinColumn(name = "user_sport_level_id")
    )
    private List<LevelHistory> levelHistory = new ArrayList<>();

    public UserSportProfile() {}

    public UserSportProfile(User user, Sport sport, float level) {
        this.user = user;
        this.sport = sport;
        this.level = level;
    }

    public void applyMatchResult(Long id, boolean won, boolean draw, LocalDateTime matchDate, float teamAvgLevel, float opponentAvgLevel) {

        float previousLevel = this.level;

        float delta = calculateDeltaLevel(won, teamAvgLevel, opponentAvgLevel);
        this.level = clampLevel(this.level + delta);

        this.stats.updateStats(won, draw);

        this.levelHistory.add(
            new LevelHistory(id,matchDate, previousLevel, this.level, won)
        );
    }

    private float calculateDeltaLevel(boolean won, float teamAvgLevel, float opponentAvgLevel) {
        double expectedResult = 1 / (1 + Math.pow(10, (opponentAvgLevel - teamAvgLevel) / 1.25));
        float kFactor = 0.2f;
        float actualResult = won ? 1.0f : 0.0f;
        return kFactor * (actualResult - (float) expectedResult);
    }

    private float clampLevel(float level){
        if (level < 1.0f) return 1.0f;
        if (level > 7.0f) return 7.0f;
        return level;
    }

    
    public void resetToInitial() {
        if(levelHistory.size() > 0){
            this.level = levelHistory.getFirst().getLevelBefore();
        }  
        this.stats = new PlayerStats();   
        this.levelHistory.clear();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public List<LevelHistory> getLevelHistory() {
        return levelHistory;
    }

    public void setLevelHistory(List<LevelHistory> levelHistory) {
        this.levelHistory = levelHistory;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }

}
