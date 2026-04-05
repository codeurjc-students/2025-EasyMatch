package es.codeurjc.model;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private String realname;
    private String username;
    private String email;
    private String password;
    private LocalDateTime birthDate;
    private boolean gender; // 1 male, 0 female
    private String description;


    @ManyToMany(mappedBy = "team1Players")
    private List<Match> matchesAsTeam1Player;

    @ManyToMany(mappedBy = "team2Players")
    private List<Match> matchesAsTeam2Player;

    @OneToMany (mappedBy = "organizer")
    private List<Match> organizedMatches;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_level_history", joinColumns = @JoinColumn(name = "user_id"))
    private List<LevelHistory> levelHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSportProfile> sportProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> sentMessages = new ArrayList<>();

    @Lob
    private Blob image;
    
    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    public User() {
        // Used by JPA
    }

    public User(String realname, String username, String email, String password, LocalDateTime birthDate,
            boolean gender, String description, String... roles) {
        this.realname = realname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.description = description;
        this.roles = List.of(roles);
    }
    

    public UserSportProfile getProfileForSport(Sport sport) {
        return sportProfiles.stream()
                .filter(sl -> sl.getSport().equals(sport))
                .findFirst()
                .orElse(null);
    }

    public void addSport(Sport sport, float initialLevel) {
        UserSportProfile sl = new UserSportProfile(this, sport, initialLevel);
        sportProfiles.add(sl);
    }

    public void applyMatchResult(Sport sport, boolean won, LocalDateTime date,
                                float teamAvg, float opponentAvg) {

        UserSportProfile sl = getProfileForSport(sport);

        if (sl == null) {
            throw new RuntimeException("User does not have this sport");
        }

        sl.applyMatchResult(won, false, date, teamAvg, opponentAvg);
    }

    public List<Match> getMatchHistory() {
        List<Match> history = new ArrayList<>();
        if (matchesAsTeam1Player != null) history.addAll(matchesAsTeam1Player);
        if (matchesAsTeam2Player != null) history.addAll(matchesAsTeam2Player);
        return history;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
    public String getUsername() {
        return username;
    }   
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Match> getMatchesAsTeam1Player() {
        return matchesAsTeam1Player;
    }

    public void setMatchesAsTeam1Player(List<Match> matchesAsTeam1Player) {
        this.matchesAsTeam1Player = matchesAsTeam1Player;
    }

    public List<Match> getMatchesAsTeam2Player() {
        return matchesAsTeam2Player;
    }

    public void setMatchesAsTeam2Player(List<Match> matchesAsTeam2Player) {
        this.matchesAsTeam2Player = matchesAsTeam2Player;
    }

    public List<Match> getOrganizedMatches() {
        return organizedMatches;
    }

    public void setOrganizedMatches(List<Match> organizedMatches) {
        this.organizedMatches = organizedMatches;
    }

    public List<LevelHistory> getLevelHistory() {
        return levelHistory;
    }

    public void setLevelHistory(List<LevelHistory> levelHistory) {
        this.levelHistory = levelHistory;
    }

    public List<ChatMessage> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<ChatMessage> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public List<UserSportProfile> getSportProfiles() {
        return sportProfiles;
    }

    public void setSportProfiles(List<UserSportProfile> sportProfiles) {
        this.sportProfiles = sportProfiles;
    }
    
}
