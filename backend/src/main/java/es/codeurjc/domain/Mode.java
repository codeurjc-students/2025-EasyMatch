package es.codeurjc.domain;

public class Mode {
    
    private String name; 
    private int playersPerGame;
    
    public Mode(String name, int playersPerGame) {
        this.name = name;
        this.playersPerGame = playersPerGame;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPlayersPerGame() {
        return playersPerGame;
    }
    public void setPlayersPerGame(int playersPerGame) {
        this.playersPerGame = playersPerGame;
    } 
    
    @Override
    public String toString() {  
        return "Mode{name='" + name + "', playersPerGame=" + playersPerGame + "}";
    }
}
