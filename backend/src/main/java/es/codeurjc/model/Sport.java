package es.codeurjc.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ElementCollection
    private List<Mode> modes; // Variations of play (i.e. singles, doubles, ...)

    public Sport() {
        // Used by JPA
    }

    public Sport(String name, List<Mode> modes) {
        this.name = name;
        this.modes = modes;
    }

    public String getName() {
        return name;
    }

    public List<Mode> getModes() {
        return modes;
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
    
    @Override
    public String toString() {
        return name + " - " + modes;
    }

    public Long getId() {
        return id;
    }

    
}
