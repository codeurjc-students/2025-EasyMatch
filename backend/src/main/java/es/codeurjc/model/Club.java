package es.codeurjc.model;

import java.sql.Blob;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Club {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private String name;
    private String city;
    private String address;
    private String phone;
    private String email;
    private String web;

    @Embedded
    private Schedule schedule;

    @Embedded
    private PriceRange priceRange;


    @OneToMany(mappedBy = "club")
    private List<Match> matchRecord;

    @ManyToMany
    @JoinTable(
        name = "club_sports",
        joinColumns = @JoinColumn(name = "club_id"),
        inverseJoinColumns = @JoinColumn(name = "sport_id")
    )
    private List<Sport> sports;

    @ElementCollection
    private List<Integer> numberOfCourts;

    @Lob
    private Blob image;

    public Club(String name, String city, String address, String phone, String email, String web) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.web = web;
    }

    public Club() {
        // Used by JPA
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }
    public Blob getImage() {
        return image;   
    }
    public void setImage(Blob image) {
		this.image = image;
	}

    public List<Sport> getSports() {
        return sports;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    public List<Integer> getNumberOfCourts() {
        return numberOfCourts;
    }

    public void setNumberOfCourts(List<Integer> numberOfCourts) {
        this.numberOfCourts = numberOfCourts;
    }



    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    public List<Match> getMatchRecord() {
        return matchRecord;
    }

    public void setMatchRecord(List<Match> matchRecord) {
        this.matchRecord = matchRecord;
    }
}
