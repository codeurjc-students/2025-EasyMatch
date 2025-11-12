package es.codeurjc.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class PlayerStats {

    private int totalMatches;
    private int wins;
    private int draws;
    private int losses;
    private double winRate;

    public PlayerStats() {}

    public PlayerStats(int totalMatches, int wins, int draws, int losses) {
        this.totalMatches = totalMatches;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.winRate = calculateWinRate();
    }

    public void updateStats(boolean won, boolean draw) {
        totalMatches++;
        if (draw) draws++;
        else if (won) wins++;
        else losses++;
        winRate = calculateWinRate();
    }

    private double calculateWinRate() {
        return totalMatches == 0 ? 0.0 : (wins * 100.0 / totalMatches);
    }

    public int getTotalMatches() { 
        return totalMatches; 
    }

    public int getWins() { 
        return wins; 
    }
    public int getDraws() { 
        return draws; 
    }
    public int getLosses() { 
        return losses; 
    }

    public double getWinRate() { 
        return winRate; 
    }

    public void setTotalMatches(int totalMatches) { 
        this.totalMatches = totalMatches; 
    }

    public void setWins(int wins) { 
        this.wins = wins; 
    }
    public void setDraws(int draws) { 
        this.draws = draws; 
    }
    public void setLosses(int losses) { 
        this.losses = losses; 
    }
    public void setWinRate(double winRate) { 
        this.winRate = winRate; 
    }

    @Override
    public String toString() {
        return "Stats [matches=" + totalMatches + ", wins=" + wins +
               ", draws=" + draws + ", losses=" + losses + ", winRate=" + winRate + "]";
    }
}
