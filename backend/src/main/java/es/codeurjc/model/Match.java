package es.codeurjc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	private int duration; // Duration in minutes 

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
	
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();

	public Match(LocalDateTime date, boolean type, boolean isPrivate, boolean state, int modeSelected, int duration, User organizer, double price, Sport sport, Club club) {
		this.date = date;
		this.type = type;
		this.isPrivate = isPrivate;
		this.state = state;
		this.modeSelected = modeSelected;
		this.duration = duration;
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

	public void addMessage(ChatMessage message) {
		this.chatMessages.add(message);
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

	public boolean isFull() {
		int playersPerGame = sport.getModes().get(modeSelected).getPlayersPerGame();
		int totalPlayers = (team1Players != null ? team1Players.size() : 0) + (team2Players != null ? team2Players.size() : 0);
		return totalPlayers >= playersPerGame;
	}

	public void validateResult(MatchResult result) {
		if (result == null) {
			throw new IllegalArgumentException("El resultado no puede ser nulo");
		}

		if (sport.getScoringType() == ScoringType.SCORE) {
			validateScoreResult(result);
		} else if (sport.getScoringType() == ScoringType.SETS) {
			validateSetsResult(result);
		}
	}

	private void validateScoreResult(MatchResult result) {

		if (result.getTeam1Score() == null || result.getTeam2Score() == null) {
			throw new IllegalArgumentException("Los goles no pueden ser nulos");
		}

		if (result.getTeam1Score() < 0 || result.getTeam2Score() < 0) {
			throw new IllegalArgumentException("Los goles no pueden ser negativos");
		}
	}

	private void validateSetsResult(MatchResult result) {

		String sportName = this.getSport().getName().toLowerCase();

		if (sportName.equals("voleibol")) {
			validateVolleyball(result);
		} else if (sportName.equals("tenis") || sportName.equals("padel")) {
			validateRacketSports(result);
		} else {
			throw new IllegalArgumentException("Deporte no soportado: " + sportName);
		}
	}

	private void validateRacketSports(MatchResult result) {

		List<Integer> team1Sets = result.getTeam1GamesPerSet();
		List<Integer> team2Sets = result.getTeam2GamesPerSet();

		validateCommon(team1Sets, team2Sets);

		int setsWon1 = 0;
    	int setsWon2 = 0;

		for (int i = 0; i < team1Sets.size(); i++) {

			int s1 = team1Sets.get(i);
			int s2 = team2Sets.get(i);

			if (s1 < 0 || s2 < 0) {
				throw new IllegalArgumentException("Los juegos no pueden ser negativos");
			}

			if (s1 > 7 || s2 > 7) {
				throw new IllegalArgumentException("Un set no puede superar 7 juegos");
			}

			if (s1 == s2) {
				throw new IllegalArgumentException("No puede haber empate en un set");
			}

			if (s1 > s2) {
				setsWon1++;
			} else {
				setsWon2++;
			}

			boolean normalSet = (Math.max(s1, s2) == 6 && Math.abs(s1 - s2) >= 2);
			boolean tieBreak = (Math.max(s1, s2) == 7 && (Math.min(s1, s2) >= 5));

			if (!normalSet && !tieBreak) {
				throw new IllegalArgumentException("Resultado inválido para tenis/pádel");
			}
		}

		if (setsWon1 < 2 && setsWon2 < 2) {
			throw new IllegalArgumentException("El partido debe ser al mejor de 3 sets y no hay empate");
		}
	}

	private void validateVolleyball(MatchResult result) {

		List<Integer> team1Sets = result.getTeam1GamesPerSet();
		List<Integer> team2Sets = result.getTeam2GamesPerSet();

		validateCommon(team1Sets, team2Sets);

		int setsWon1 = 0;
    	int setsWon2 = 0;

		for (int i = 0; i < team1Sets.size(); i++) {

			int s1 = team1Sets.get(i);
			int s2 = team2Sets.get(i);

			if (s1 < 0 || s2 < 0) {
				throw new IllegalArgumentException("Los puntos no pueden ser negativos");
			}

			int target = 25;

			if (s1 < target && s2 < target) {
				throw new IllegalArgumentException("El set no llega al mínimo de puntos");
			}

			if (Math.abs(s1 - s2) < 2) {
				throw new IllegalArgumentException("Debe haber diferencia mínima de 2 puntos");
			}

			if (s1 == s2) {
				throw new IllegalArgumentException("No puede haber empate en un set");
			}

			if (s1 > s2) {
				setsWon1++;
			} else {
				setsWon2++;
			}
		}

		if (setsWon1 < 2 && setsWon2 < 2) {
			throw new IllegalArgumentException("El partido debe ser al mejor de 3 sets y no hay empate");
		}
	}

	private void validateCommon(List<Integer> t1, List<Integer> t2) {

		if (t1 == null || t2 == null) {
			throw new IllegalArgumentException("Los sets no pueden ser nulos");
		}

		if (t1.size() != t2.size()) {
			throw new IllegalArgumentException("El número de sets debe coincidir");
		}

		if (t1.isEmpty()) {
			throw new IllegalArgumentException("Debe haber al menos un set");
		}
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


	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}


	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Match [id=" + id + ", date=" + date + ", type=" + type + ", isPrivate=" + isPrivate + ", state=" + state
				+ ", organizer=" + organizer + ", sport=" + sport + ", club=" + club + ", team1=" + team1Players + "team2="+ 
				team2Players + "]";
	}

}