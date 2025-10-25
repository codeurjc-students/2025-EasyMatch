package es.codeurjc.model;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
public class User {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private String realName;
    private String username;
    private String email;
    private String password;
    private LocalDateTime birthDate;
    private boolean gender; // 1 male, 0 female
    private String description;
    private float level; // from 1 to 7

    @ManyToMany (mappedBy = "players", cascade = CascadeType.REMOVE)
    private List<Match> matchRecord;

    @OneToMany (mappedBy = "organizer", cascade = CascadeType.REMOVE)
    private List<Match> organizedMatches;

    @Lob
    private Blob image;
    
    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    public User() {
        // Used by JPA
    }

    public User(String realName, String username, String email, String password, LocalDateTime birthDate,
            boolean gender, String description, float level, String... roles) {
        this.realName = realName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.description = description;
        this.level = level;
        this.roles = List.of(roles);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
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

    
    
}
