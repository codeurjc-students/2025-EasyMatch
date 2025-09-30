package es.codeurjc.model;

import java.time.LocalDateTime;
/* import java.util.List;*/
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/* import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne; */




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

	private String organizer;
	/* private User organizer; */

	private String sport;
	/* private Sport sport; */

	/* @ManyToOne
	@JoinColumn(name = "club_id") 
	private Club club; */

	/* @ManyToMany
	@JoinColumn(name = "player_id") 
	private List<User> players; */
	

	public Match(LocalDateTime date, boolean type, boolean isPrivate, boolean state,String organizer, String sport) {
		this.date = date;
		this.type = type;
		this.isPrivate = isPrivate;
		this.state = state;
		this.organizer = organizer;
		this.sport = sport;
	}



	/* public Match(LocalDateTime date, boolean type, boolean isPrivate, boolean state, User organizer, Sport sport,
			Club club) {
		this.date = date;
		this.type = type;
		this.isPrivate = isPrivate;
		this.state = state;
		this.organizer = organizer;
		this.sport = sport;
		this.club = club;
	} */



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



	public boolean isType() {
		return type;
	}



	public void setType(boolean type) {
		this.type = type;
	}



	public boolean isPrivate() {
		return isPrivate;
	}



	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}



	public boolean isState() {
		return state;
	}



	public void setState(boolean state) {
		this.state = state;
	}



	/* public Club getClub() {
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
 */


	public String getSport() {
		return sport;
	}



	public void setSport(String sport) {
		this.sport = sport;
	}



	public String getOrganizer() {
		return organizer;
	}



	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	@Override
	public String toString() {
		return "Match [id=" + id + ", date=" + date + ", type=" + type + ", isPrivate=" + isPrivate + ", state=" + state
				+ ", organizer=" + organizer + ", sport=" + sport + /* ", club=" + club + ", players=" + players + */ "]";
	}
	
	
}