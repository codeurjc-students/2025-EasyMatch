package es.codeurjc.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "matches") 
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private LocalDateTime date;
    private Boolean type; // competitive 1, friendly 0
    private Boolean isPrivate; // private 1, public 0
    private Boolean state; // open 1, closed 0
	private int modeSelected; 

	@ManyToOne
	@JoinColumn(name = "organizer_id")
	private User organizer;
	private double price;

	@ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

	@ManyToOne
	@JoinColumn(name = "club_id")
	private Club club; 

	@ManyToMany
	@JoinTable(
		name = "match_team1_players",
		joinColumns = @JoinColumn(name = "match_id"),
		inverseJoinColumns = @JoinColumn(name = "player_id")
	)
    private Set<User> team1Players;
	
	
	@ManyToMany
	@JoinTable(
		name = "match_team2_players",
		joinColumns = @JoinColumn(name = "match_id"),
		inverseJoinColumns = @JoinColumn(name = "player_id")
	)
    private Set<User> team2Players;

	@Embedded
    private MatchResult result;
	

	public Match(LocalDateTime date, boolean type, boolean isPrivate, boolean state, int modeSelected, User organizer, double price, Sport sport, Club club) {
		this.date = date;
		this.type = type;
		this.isPrivate = isPrivate;
		this.state = state;
		this.modeSelected = modeSelected;
		this.organizer = organizer;
		this.price = price;
		this.sport = sport;
		this.club = club;
	}



	public Match() {
		// Used by JPA
	}


	public void addPlayerToTeam1(User player) {
        this.team1Players.add(player);
    }

    public void addPlayerToTeam2(User player) {
        this.team2Players.add(player);
    }

	public boolean containsPlayer(User player) {
        return (team1Players != null && team1Players.contains(player))
            || (team2Players != null && team2Players.contains(player));
    }

	public boolean didPlayerWin(User player) {
        if (result == null || sport == null || sport.getScoringType() == null) return false;
        String winner = result.getWinner(sport.getScoringType());
        if (winner == null || winner.equals("Empate") || winner.equals("Sin resultado")) return false;

        if (team1Players != null && team1Players.contains(player)) {
            return winner.equals(result.getTeam1Name());
        } else if (team2Players != null && team2Players.contains(player)) {
            return winner.equals(result.getTeam2Name());
        }
        return false;
    }

	public boolean didPlayerLose(User player) {
        if (!containsPlayer(player)) return false;
        return !didPlayerWin(player);
    }

	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}


	public LocalDateTime getDate() {
		return date;
	}



	public void setDate(LocalDateTime date) {
		this.date = date;
	}


	public Boolean getType() {
		return type;
	}



	public void setType(Boolean type) {
		this.type = type;
	}



	 public Club getClub() {
		return club;
	}



	public void setClub(Club club) {
		this.club = club;
	}


 


	public Sport getSport() {
		return sport;
	}



	public void setSport(Sport sport) {
		this.sport = sport;
	}



	public User getOrganizer() {
		return organizer;
	}



	public void setOrganizer(User organizer) {
		this.organizer = organizer;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}



	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Boolean getState() {
		return state;
	}



	public void setState(Boolean state) {
		this.state = state;
	}

	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	
	

	public MatchResult getResult() {
		return result;
	}


	public void setResult(MatchResult result) {
		this.result = result;
	}
	
	public Set<User> getTeam1Players() {
		return team1Players;
	}


	public void setTeam1Players(Set<User> team1Players) {
		this.team1Players = team1Players;
	}



	public Set<User> getTeam2Players() {
		return team2Players;
	}

	public void setTeam2Players(Set<User> team2Players) {
		this.team2Players = team2Players;
	}

	
	public int getModeSelected() {
		return modeSelected;
	}



	public void setModeSelected(int modeSelected) {
		this.modeSelected = modeSelected;
	}


	@Override
	public String toString() {
		return "Match [id=" + id + ", date=" + date + ", type=" + type + ", isPrivate=" + isPrivate + ", state=" + state
				+ ", organizer=" + organizer + ", sport=" + sport + ", club=" + club + ", team1=" + team1Players + "team2="+ 
				team2Players + "]";
	}

}