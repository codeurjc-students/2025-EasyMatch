package es.codeurjc.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.Mode;
import es.codeurjc.model.PriceRange;
import es.codeurjc.model.Schedule;
import es.codeurjc.model.Sport;
import es.codeurjc.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;


@Component
public class DatabaseInitializer {
    @Autowired
    MatchService matchService;

    @Autowired
    UserService userService;

    @Autowired
    ClubService clubService;

    @Autowired
    SportService sportService;

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        
        Mode singles = new Mode("Singles",2);
        Mode doubles = new Mode("Doubles",4);
        Mode m7v7 = new Mode("7v7",14);
        Mode m11v11 = new Mode("11v11",22);
        
        List<Mode> modesOfTennis = new ArrayList<>();
        modesOfTennis.add(singles);
        modesOfTennis.add(doubles);

        List<Mode> modesOfFootball = new ArrayList<>();
        modesOfFootball.add(m7v7);
        modesOfFootball.add(m11v11);

        List<Mode> modesOfPaddle = new ArrayList<>();
        modesOfPaddle.add(singles);
        modesOfPaddle.add(doubles);
        
        LocalDateTime date1 = LocalDateTime.of(2025, 9, 30, 12, 30);
        LocalDateTime date2 = LocalDateTime.of(2025, 10, 1, 11, 00);
        LocalDateTime date3 = LocalDateTime.of(2025, 10, 3, 10, 30);
        LocalDateTime date4 = LocalDateTime.of(2025, 10, 5, 19, 00);

        Sport sport1 = new Sport("Tenis", modesOfTennis);
        Sport tennis = sportService.save(sport1);
        Sport sport2 = new Sport("Padel", modesOfPaddle);
        Sport paddle = sportService.save(sport2);
        Sport sport3 = new Sport("Futbol", modesOfFootball);
        Sport football = sportService.save(sport3);


        User user1 = new User("Pedro Garcia","pedro123","pedro@emeal.com","pedroga4",LocalDateTime.of(1990,5,20,0,0),true,"Avid tennis player",5.0f,"USER");
        setUserImage(user1,"/images/pedro.jpg");
        userService.save(user1);

        User user2 = new User("Maria Lopez","maria456","maria@emeal.com","marialo3",LocalDateTime.of(1992,8,15,0,0),false,"Loves friendly matches",4.5f,"USER");
        setUserImage(user2,"/images/maria.jpg");
        userService.save(user2);

        User user3 = new User("Juan Martinez","juan789","juan@emeal.com","juanma1",LocalDateTime.of(1988,3,10,0,0),true,"Competitive footballer",6.0f,"USER");
        setUserImage(user3,"/images/juan.jpg");
        userService.save(user3);

        User user4 = new User("Luis Sanchez","luis321","luis@eameal.com","saluis2",LocalDateTime.of(1995,12,5,0,0),true,"Enjoys casual games",3.5f,"USER");
        setUserImage(user4,"/images/luis.jpg");
        userService.save(user4);

        Club club1 = new Club("Tennis Club Elite", "Madrid", "Plaza de Rafael Nadal, 22", "684274290","tennisclubelite@outlook.com","www.tennisclubelite.com");
        club1.setSports(List.of(paddle, tennis));
        club1.setNumberOfCourts(List.of(12,10));
        club1.setSchedule(new Schedule("08:00", "22:00"));
        club1.setPriceRange(new PriceRange(30, 35, "€/hora"));
        setClubImage(club1,"/images/tennis_club_elite.jpg");
        clubService.save(club1); 

        Club club2 = new Club("Padel Pro Center", "Valencia", "Avenida de las Estrellas, 5", "684274292","padelprocenter@emeal.com","www.padelprocenter.com");
        club2.setSports(List.of(paddle));
        club2.setNumberOfCourts(List.of(18));
        club2.setSchedule(new Schedule("07:00", "23:00"));
        club2.setPriceRange(new PriceRange(25, 30, "€/hora"));
        setClubImage(club2,"/images/padel_pro_center.jpeg");
        clubService.save(club2);

        Club club3 = new Club("Tennis & Padel Hub", "Sevilla", "Calle de los Campeones, 8", "684274293","tennis&padelhub@emeal.com","www.tennisandpadelhub.com");
        club3.setSports(List.of(paddle,tennis));
        club3.setNumberOfCourts(List.of(8,15));
        club3.setSchedule(new Schedule("08:00", "22:30"));
        club3.setPriceRange(new PriceRange(35, 45, "€/hora"));
        setClubImage(club3,"/images/tennis_padel_hub.jpg");
        clubService.save(club3);

        Club club4 = new Club("Football Arena", "Barcelona", "Carrer de Messi, 10", "684274291","footballarean@outlook.com","www.footballarena.com");
        club4.setSports(List.of(football));
        club4.setNumberOfCourts(List.of(5));
        club4.setSchedule(new Schedule("09:30", "21:30"));
        club4.setPriceRange(new PriceRange(10, 20, "€/hora"));
        setClubImage(club4,"/images/football_arena.jpg");
        clubService.save(club4);

        
        Match match1 = new Match(date1,true,false,true,user1,3.49f,tennis,club1);
        Match match2 = new Match(date2,false,true,true,user2,8.99f, paddle,club2);
        Match match3 = new Match(date3,true,false,true,user3,6.49f,tennis,club3);
        Match match4 = new Match(date4,true,false,true,user4,4.5f,football,club4);

        match1.setPlayers(new ArrayList<>());
        match1.getPlayers().add(match1.getOrganizer());
        match2.setPlayers(new ArrayList<>());
        match2.getPlayers().add(match2.getOrganizer());
        match3.setPlayers(new ArrayList<>());
        match3.getPlayers().add(match3.getOrganizer());
        match4.setPlayers(new ArrayList<>());
        match4.getPlayers().add(match4.getOrganizer());

        matchService.save(match1);
        matchService.save(match2);
        matchService.save(match3);
        matchService.save(match4);
    }
    private void setUserImage(User user, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}

    private void setClubImage(Club club, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    club.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}
}
