package es.codeurjc.domain;

import java.util.List;

public class Sport {
    private String name;
    private List<Mode> modes; // Variations of play (i.e. singles, doubles, ...)

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

    @Override
    public String toString() {
        return "Sport{name='" + name + "', modes=" + modes + "}";
    }
}
