package es.codeurjc.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Schedule {
    private String openingTime;
    private String closingTime;
    
    public Schedule(String openingTime, String closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Schedule() {
    }
    
    public String getOpeningTime() {
        return openingTime;
    }
    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }
    public String getClosingTime() {
        return closingTime;
    }
    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public String toString() {
        return openingTime + " - " + closingTime;
    }
    
}
