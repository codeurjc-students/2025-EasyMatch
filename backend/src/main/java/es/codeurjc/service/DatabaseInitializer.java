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
import es.codeurjc.model.MatchResult;
import es.codeurjc.model.Mode;
import es.codeurjc.model.PriceRange;
import es.codeurjc.model.Schedule;
import es.codeurjc.model.ScoringType;
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
        Mode indoor = new Mode("Indoor",12);
        Mode beach = new Mode("Beach",4);
        
        List<Mode> modesOfTennis = new ArrayList<>();
        modesOfTennis.add(singles);
        modesOfTennis.add(doubles);

        List<Mode> modesOfFootball = new ArrayList<>();
        modesOfFootball.add(m7v7);
        modesOfFootball.add(m11v11);

        List<Mode> modesOfPaddle = new ArrayList<>();
        modesOfPaddle.add(singles);
        modesOfPaddle.add(doubles);

        List<Mode> modesOfVolley = new ArrayList<>();
        modesOfVolley.add(beach);
        modesOfVolley.add(indoor);
        
        LocalDateTime date1 = LocalDateTime.of(2025, 12, 5, 12, 30);
        LocalDateTime date2 = LocalDateTime.of(2025, 11, 25, 11, 00);
        LocalDateTime date3 = LocalDateTime.of(2025, 12, 3, 10, 30);
        LocalDateTime date4 = LocalDateTime.of(2025, 11, 28, 19, 00);
        LocalDateTime date5 = LocalDateTime.of(2025, 11, 15, 10, 30);

        Sport sport1 = new Sport("Tenis", modesOfTennis,ScoringType.SETS);
        Sport tennis = sportService.save(sport1);
        Sport sport2 = new Sport("Padel", modesOfPaddle,ScoringType.SETS);
        Sport paddle = sportService.save(sport2);
        Sport sport3 = new Sport("Futbol", modesOfFootball,ScoringType.SCORE);
        Sport football = sportService.save(sport3);
        Sport sport4 = new Sport("Voleibol", modesOfVolley,ScoringType.SETS);
        Sport volley = sportService.save(sport4);

        User pedro = new User("Pedro Garcia","pedro123","pedro@emeal.com","pedroga4",LocalDateTime.of(1990,5,20,0,0),true,"Apasionado del tenis",5.12f,"USER");
        setUserImage(pedro,"/images/pedro.jpg");
        userService.save(pedro);

        User maria = new User("Maria Lopez","maria456","maria@emeal.com","marialo3",LocalDateTime.of(1992,8,15,0,0),false,"Me encantan los partidos amistosos",4.57f,"USER");
        setUserImage(maria,"/images/maria.jpg");
        userService.save(maria);

        User juan = new User("Juan Martinez","juan789","juan@emeal.com","juanma1",LocalDateTime.of(1988,3,10,0,0),true,"Apasionado del deporte rey y competitivo",6.03f,"USER");
        setUserImage(juan,"/images/juan.jpg");
        userService.save(juan);

        User luis = new User("Luis Sanchez","luis321","luis@emeal.com","saluis2",LocalDateTime.of(1995,12,5,0,0),true,"Disfruto de partidos casuales",3.51f,"USER");
        setUserImage(luis,"/images/luis.jpg");
        userService.save(luis);

        User silvia = new User("Silvia Gonzalez","silvia66","silvia@emeal.com","silvia5",LocalDateTime.of(2003,8,7,0,0),false,"Me encanta jugar mientras haya un ambiente sano",5.37f,"USER");
        setUserImage(silvia,"/images/silvia.jpg");
        userService.save(silvia);


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

        Club club5 = new Club(
            "Volleyball Pro Center", 
            "Murcia", 
            "Calle de Carlos Alcaraz, 6", 
            "684274292",
            "volleyballcenter@outlook.com",
            "www.volleyballcenter.com"
        );
        club5.setSports(List.of(volley));
        club5.setNumberOfCourts(List.of(3));
        club5.setSchedule(new Schedule("10:00", "22:00"));
        club5.setPriceRange(new PriceRange(15, 30, "€/hora"));
        setClubImage(club5, "/images/volleyball_center.jpg");
        clubService.save(club5);

        
        Match match1 = new Match(date1,true,false,true,pedro,3.49f,tennis,club1);
        Match match2 = new Match(date2,false,true,true,maria,8.99f, paddle,club2);
        Match match3 = new Match(date3,true,false,true,luis,6.49f,tennis,club3);
        Match match4 = new Match(date4,true,false,true,juan,4.49f,football,club4);
        Match match5 = new Match(date5,true,false,true,silvia,3.25f,volley,club5);

        match1.setTeam1Players(new ArrayList<>());
        match1.setTeam2Players(new ArrayList<>());
        match1.getTeam1Players().add(match1.getOrganizer());
        match2.setTeam1Players(new ArrayList<>());
        match2.setTeam2Players(new ArrayList<>());
        match2.getTeam1Players().add(match2.getOrganizer());
        match3.setTeam1Players(new ArrayList<>());
        match3.setTeam2Players(new ArrayList<>());
        match3.getTeam1Players().add(match3.getOrganizer());
        match4.setTeam1Players(new ArrayList<>());
        match4.setTeam2Players(new ArrayList<>());
        match4.getTeam1Players().add(match4.getOrganizer());
        match5.setTeam1Players(List.of(silvia));
        match5.setTeam2Players(new ArrayList<>());
        

        matchService.save(match1);
        matchService.save(match2);
        matchService.save(match3);
        matchService.save(match4);
        matchService.save(match5);

        Match tennisMatch1 = new Match(
            LocalDateTime.of(2025, 9, 10, 10, 30),
            true, 
            false,
            false, 
            pedro, 
            4.50,
            tennis,
            club1
        );

        Match tennisMatch2 = new Match(
            LocalDateTime.of(2025, 9, 18, 12, 0),
            true,
            false,
            false,
            pedro,
            4.50,
            tennis,
            club3
        );

        Match tennisMatch3 = new Match(
            LocalDateTime.of(2025, 10, 1, 9, 30),
            true,
            false,
            false,
            maria,
            4.50,
            tennis,
            club3
        );

        tennisMatch1.setTeam1Players(List.of(pedro));
        tennisMatch1.setTeam2Players(List.of(maria));

        tennisMatch2.setTeam1Players(List.of(pedro));
        tennisMatch2.setTeam2Players(List.of(luis));

        tennisMatch3.setTeam1Players(List.of(maria));
        tennisMatch3.setTeam2Players(List.of(pedro));

        tennisMatch1.setResult(new MatchResult("Pedro Garcia", "Maria Lopez", 2, 1,List.of(6,4,6),List.of(3,6,2))); // Pedro wins
        tennisMatch2.setResult(new MatchResult("Pedro Garcia", "Luis Sanchez", 0, 2,List.of(3,4), List.of(6,6))); // Pedro loses
        tennisMatch3.setResult(new MatchResult("Maria Lopez", "Pedro Garcia", 2, 0,List.of(6,6), List.of(2,1))); // Pedro loses


        matchService.save(tennisMatch1);
        matchService.save(tennisMatch2);
        matchService.save(tennisMatch3);

        pedro.updateStats(tennisMatch1.didPlayerWin(pedro), false); // wins
        pedro.updateStats(tennisMatch2.didPlayerWin(pedro), false); // loses
        pedro.updateStats(tennisMatch3.didPlayerWin(pedro), false); // loses
        userService.update(pedro);
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
