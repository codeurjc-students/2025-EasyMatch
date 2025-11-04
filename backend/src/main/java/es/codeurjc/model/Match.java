package es.codeurjc.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
//import jakarta.persistence.JoinTable;
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
	/*@JoinTable(
        name = "match_players",
        joinColumns = @JoinColumn(name = "match_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )*/
	private List<User> players; 
	

	public Match(LocalDateTime date, boolean type, boolean isPrivate, boolean state,User organizer, double price, Sport sport, Club club) {
		this.date = date;
		this.type = type;
		this.isPrivate = isPrivate;
		this.state = state;
		this.organizer = organizer;
		this.price = price;
		this.sport = sport;
		this.club = club;
	}



	public Match() {
		// Used by JPA
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



	public List<User> getPlayers() {
		return players;
	}



	public void setPlayers(List<User> players) {
		this.players = players;
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
	


	@Override
	public String toString() {
		return "Match [id=" + id + ", date=" + date + ", type=" + type + ", isPrivate=" + isPrivate + ", state=" + state
				+ ", organizer=" + organizer + ", sport=" + sport + ", club=" + club + ", players=" + players + "]";
	}

	
}