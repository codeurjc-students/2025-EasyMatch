package es.codeurjc.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sports")
public class Sport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ElementCollection
    private List<Mode> modes; // Variations of play (i.e. singles, doubles, ...)

    @Enumerated(EnumType.STRING)
    private ScoringType scoringType;

    public Sport() {
        // Used by JPA
    }

    public Sport(String name, List<Mode> modes, ScoringType scoringType) {
        this.name = name;
        this.modes = modes;
        this.scoringType = scoringType;
    }

    public String getName() {
        return name;
    }

    public List<Mode> getModes() {
        return modes;
    }

     public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }

    public ScoringType getScoringType() { 
        return scoringType; 
    }

    public void setScoringType(ScoringType scoringType) { 
        this.scoringType = scoringType; 
    }
    
    @Override
    public String toString() {
        return name + " - " + modes;
    }

   
}
