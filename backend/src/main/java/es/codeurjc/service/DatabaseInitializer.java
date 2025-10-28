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

import es.codeurjc.domain.Mode;
import es.codeurjc.domain.Sport;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.User;
import es.codeurjc.utils.ImageUtils;
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
    ImageUtils imageUtils;

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        
        Mode mode1 = new Mode("Singles",2);
        Mode mode2 = new Mode("Doubles",4);
        Mode mode3 = new Mode("7v7",14);
        Mode mode4 = new Mode("11v11",22);
        
        List<Mode> modesOfTennis = new ArrayList<>();
        modesOfTennis.add(mode1);
        modesOfTennis.add(mode2);

        List<Mode> modesOfFootball = new ArrayList<>();
        modesOfFootball.add(mode3);
        modesOfFootball.add(mode4);

        List<Mode> modesOfPaddle = new ArrayList<>();
        modesOfPaddle.add(mode1);
        modesOfPaddle.add(mode2);
        
        LocalDateTime date1 = LocalDateTime.of(2025, 9, 30, 12, 30);
        LocalDateTime date2 = LocalDateTime.of(2025, 10, 1, 11, 00);
        LocalDateTime date3 = LocalDateTime.of(2025, 10, 3, 10, 30);
        LocalDateTime date4 = LocalDateTime.of(2025, 10, 5, 19, 00);

        Sport sport1 = new Sport("Tenis", modesOfTennis);
        Sport sport2 = new Sport("Padel", modesOfPaddle);
        Sport sport3 = new Sport("Futbol", modesOfFootball);

        List<Sport> sports1 = new ArrayList<>();
        sports1.add(sport1);
        List<Integer> numberOfCourts = new ArrayList<>();
        numberOfCourts.add(12);

        List<Sport> sports3 = new ArrayList<>();
        sports3.add(sport1);
        sports3.add(sport2);
        List<Integer> numberOfCourts2 = new ArrayList<>();
        numberOfCourts.add(8);
        numberOfCourts.add(15);

        List<Sport> sports4 = new ArrayList<>();
        sports1.add(sport3);
        List<Integer> numberOfPitches = new ArrayList<>();
        numberOfCourts.add(8);


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

        Club club1 = new Club("Tennis Club Elite", "Madrid", "Plaza de Rafael Nadal, 22", "684274290","tennisclubelite@outlook.com","www.tennisclubelite.com",sports1,numberOfCourts);
        clubService.save(club1); 
        Club club2 = new Club("Padel Pro Center", "Valencia", "Avenida de las Estrellas, 5", "684274292","padelprocenter@emeal.com","www.padelprocenter.com",List.of(sport2),List.of(10));
        clubService.save(club2);
        Club club3 = new Club("Tennis & Padel Hub", "Sevilla", "Calle de los Campeones, 8", "684274293","tennis&padelhub@emeal.com","www.tennisandpadelhub.com",sports3,numberOfCourts2);
        clubService.save(club3);
        Club club4 = new Club("Football Arena", "Barcelona", "Carrer de Messi, 10", "684274291","footballarean@outlook.com","www.footballarena.com",sports4,numberOfPitches);
        clubService.save(club4);

        
        Match match1 = new Match(date1,true,false,true,user1,3.49f,sport1.getName(),club1);
        Match match2 = new Match(date2,false,true,true,user2,8.99f, sport2.getName(),club2);
        Match match3 = new Match(date3,true,false,true,user3,6.49f,sport1.getName(),club3);
        Match match4 = new Match(date4,true,false,true,user4,4.5f,sport3.getName(),club4);

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
    public void setUserImage(User user, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}
}
