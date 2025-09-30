package es.codeurjc.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.domain.Mode;
import es.codeurjc.domain.Sport;
import es.codeurjc.model.Match;
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

        List<Sport> sports = new ArrayList<>();
        sports.add(sport1);
        List<Integer> numberOfCourts = new ArrayList<>();
        numberOfCourts.add(12);

        /* Club club1 = new Club("Tennis Club Elite", "Madrid", "Plaza de Rafael Nadal, 22", "684274290","tennisclubelite@outlook.com","www.tennisclubelite.com",sports,numberOfCourts);
        clubService.save(club1); */
        
        Match match1 = new Match(date1,true,false,true,"Pedro",sport1.getName());
        Match match2 = new Match(date2,false,true,true,"Maria",sport2.getName());
        Match match3 = new Match(date3,true,false,true,"Juan",sport1.getName());
        Match match4 = new Match(date4,true,false,true,"Luis",sport3.getName());

        matchService.save(match1);
        matchService.save(match2);
        matchService.save(match3);
        matchService.save(match4);
    }
}
