package es.codeurjc.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.MatchResult;
import es.codeurjc.model.MessageType;
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
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private SportService sportService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserSportProfileService userSportProfileService;

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
        
        LocalDateTime date1 = LocalDateTime.of(2026, 6, 16, 12, 30);
        LocalDateTime date2 = LocalDateTime.of(2026, 6, 18, 11, 0);
        LocalDateTime date3 = LocalDateTime.of(2026, 6, 20, 10, 30);
        LocalDateTime date4 = LocalDateTime.of(2026, 6, 21, 19, 0);
        LocalDateTime date5 = LocalDateTime.of(2026, 6, 22, 10, 30);

        LocalDateTime date6 = LocalDateTime.of(2026, 6, 24, 18, 0);
        LocalDateTime date7 = LocalDateTime.of(2026, 6, 25, 20, 0);
        LocalDateTime date8 = LocalDateTime.of(2026, 6, 27, 17, 30);
        LocalDateTime date9 = LocalDateTime.of(2026, 6, 28, 21, 0);
        LocalDateTime date10 = LocalDateTime.of(2026, 6, 29, 9, 30);

        LocalDateTime date11 = LocalDateTime.of(2026, 7, 1, 11, 0);
        LocalDateTime date12 = LocalDateTime.of(2026, 7, 3, 19, 30);

        Sport sport1 = new Sport("Tenis", modesOfTennis,ScoringType.SETS);
        Sport tennis = sportService.save(sport1);
        Sport sport2 = new Sport("Padel", modesOfPaddle,ScoringType.SETS);
        Sport paddle = sportService.save(sport2);
        Sport sport3 = new Sport("Futbol", modesOfFootball,ScoringType.SCORE);
        Sport football = sportService.save(sport3);
        Sport sport4 = new Sport("Voleibol", modesOfVolley,ScoringType.SETS);
        Sport volley = sportService.save(sport4);

        User admin = new User(
            "Admin",
            "admin",
            "admin@emeal.com",
            "admin",
            LocalDateTime.of(1985,1,1,0,0),
            true,
            "Administrador del sistema",
            "ADMIN","USER"
        );
        setUserImage(admin,"/images/default-avatar.jpg");
        userService.save(admin);

        User pedro = new User(
            "Pedro Garcia",
            "pedro123",
            "pedro@emeal.com",
            "pedroga4",
            LocalDateTime.of(1990,5,20,0,0),
            true,
            "Apasionado del tenis", 
            "USER"
        );
        setUserImage(pedro,"/images/pedro.jpg");
        userService.save(pedro);

        User maria = new User(
            "Maria Lopez",
            "maria456",
            "maria@emeal.com",
            "marialo3",
            LocalDateTime.of(1992,8,15,0,0),
            false,
            "Me encantan los partidos amistosos",
            "USER"
        );
        setUserImage(maria,"/images/maria.jpg");
        userService.save(maria);

        User juan = new User(
            "Juan Martinez",
            "juan789",
            "juan@emeal.com",
            "juanma1",
            LocalDateTime.of(1988,3,10,0,0),
            true,
            "Apasionado del deporte rey y competitivo",
            "USER"
        );
        setUserImage(juan,"/images/juan.jpg");
        userService.save(juan);

        User luis = new User(
            "Luis Sanchez",
            "luis321",
            "luis@emeal.com",
            "saluis2",
            LocalDateTime.of(1995,12,5,0,0),
            true,
            "Disfruto de partidos casuales",
            "USER"
        );
        setUserImage(luis,"/images/luis.jpg");
        userService.save(luis);

        User silvia = new User(
            "Silvia Gonzalez",
            "silvia66",
            "silvia@emeal.com",
            "silvia5",
            LocalDateTime.of(2003,8,7,0,0),
            false,
            "Me encanta jugar mientras haya un ambiente sano",
            "USER"
        );
        setUserImage(silvia,"/images/silvia.jpg");
        userService.save(silvia);

        User carlos = new User(
            "Carlos Ruiz",
            "carlos10",
            "carlos@emeal.com",
            "carlrui7",
            LocalDateTime.of(1993, 4, 18, 0, 0),
            true,
            "Apasionado del fútbol y los partidos competitivos",
            "USER"
        );
        setUserImage(carlos, "/images/carlos.jpg");
        userService.save(carlos);

        User fernando = new User(
                "Fernando Torres",
                "fernando22",
                "fernando@emeal.com",
                "fertor4",
                LocalDateTime.of(1991, 9, 12, 0, 0),
                true,
                "Jugador de voleibol y amante del trabajo en equipo",
                "USER"
        );
        setUserImage(fernando, "/images/fernando.jpg");
        userService.save(fernando);

        User jorge = new User(
                "Jorge Medina",
                "jorge88",
                "jorge@emeal.com",
                "jormed9",
                LocalDateTime.of(1996, 2, 25, 0, 0),
                false,
                "Disfruto del voleibol competitivo y los partidos amistosos",
                "USER"
        );
        setUserImage(jorge, "/images/jorge.jpg");
        userService.save(jorge);

        User ana = new User(
                "Ana Romero",
                "ana77",
                "ana@emeal.com",
                "anarom6",
                LocalDateTime.of(1998, 11, 3, 0, 0),
                true,
                "El voleibol es mi deporte favorito y siempre busco mejorar",
                "USER"
        );
        setUserImage(ana, "/images/ana.jpg");
        userService.save(ana);

        Club club1 = new Club(
            "Tennis Club Elite", 
            "Madrid", 
            "Plaza de Rafael Nadal, 22", 
            "684274290",
            "tennisclubelite@outlook.com",
            "www.tennisclubelite.com"
        );
        club1.setSports(List.of(paddle, tennis));
        club1.setNumberOfCourts(List.of(12,10));
        club1.setSchedule(new Schedule("08:00", "22:00"));
        club1.setPriceRange(new PriceRange(30, 35, "€/hora"));
        setClubImage(club1,"/images/tennis_club_elite.jpg");
        clubService.save(club1); 

        Club club2 = new Club(
            "Padel Pro Center", 
            "Valencia", 
            "Avenida de las Estrellas, 5", 
            "684274292",
            "padelprocenter@hotmail.com",
            "www.padelprocenter.com"
        );
        club2.setSports(List.of(paddle));
        club2.setNumberOfCourts(List.of(18));
        club2.setSchedule(new Schedule("07:00", "23:00"));
        club2.setPriceRange(new PriceRange(25, 30, "€/hora"));
        setClubImage(club2,"/images/padel_pro_center.jpg");
        clubService.save(club2);

       Club club3 = new Club(
            "Club Tenis y Pádel Bético",
            "Sevilla",
            "Avenida de Heliópolis, 14",
            "684274293",
            "ctpbetico@hotmail.com",
            "www.ctpbetico.com"
        );

        club3.setSports(List.of(paddle, tennis));
        club3.setNumberOfCourts(List.of(6, 8));
        club3.setSchedule(new Schedule("08:00", "22:30"));
        club3.setPriceRange(new PriceRange(28, 40, "€/hora"));
        setClubImage(club3, "/images/ctp_betico.jpg");
        clubService.save(club3);

        Club club4 = new Club(
            "Football Arena", 
            "Barcelona", 
            "Carrer de Messi, 10", 
            "684274291",
            "footballarean@outlook.com",
            "www.footballarena.com"
        );
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

        Club club6 = new Club(
            "Costa del Sol Sports Club",
            "Málaga",
            "Paseo Marítimo Antonio Banderas, 18",
            "684274301",
            "costadelsol@outlook.com",
            "www.costadelsolsports.com"
        );
        club6.setSports(List.of(football, volley));
        club6.setNumberOfCourts(List.of(2, 2));
        club6.setSchedule(new Schedule("09:00", "22:00"));
        club6.setPriceRange(new PriceRange(12, 22, "€/hora"));
        setClubImage(club6, "/images/costadelsol.jpg");
        clubService.save(club6);

        Club club7 = new Club(
            "Club Deportivo El Roble",
            "Toledo",
            "Calle del Roble, 14",
            "684274302",
            "elroble@outlook.com",
            "www.elrobleclub.com"
        );
        club7.setSports(List.of(football, tennis));
        club7.setNumberOfCourts(List.of(2,2));
        club7.setSchedule(new Schedule("10:00", "21:30"));
        club7.setPriceRange(new PriceRange(14, 20, "€/hora"));
        setClubImage(club7, "/images/elroble.jpg");
        clubService.save(club7);

        Club club8 = new Club(
            "La Sierra Club",
            "Granada",
            "Avenida Sierra Nevada, 25",
            "684274303",
            "lasierra@outlook.com",
            "www.lasierraclub.com"
        );
        club8.setSports(List.of(volley, tennis));
        club8.setNumberOfCourts(List.of(2, 3));
        club8.setSchedule(new Schedule("08:30", "22:30"));
        club8.setPriceRange(new PriceRange(18, 28, "€/hora"));
        setClubImage(club8, "/images/lasierra.jpg");
        clubService.save(club8);

        Club club9 = new Club(
            "Club Mar y Sol",
            "Alicante",
            "Calle del Mediterráneo, 9",
            "684274304",
            "marysol@hotmail.com",
            "www.clubmarysol.com"
        );
        club9.setSports(List.of(tennis,football));
        club9.setNumberOfCourts(List.of(7,2));
        club9.setSchedule(new Schedule("09:00", "23:00"));
        club9.setPriceRange(new PriceRange(16, 24, "€/hora"));
        setClubImage(club9, "/images/marysol.jpg");
        clubService.save(club9);

        Club club10 = new Club(
            "Multisport Arena",
            "Madrid",
            "Avenida de Europa, 45",
            "684274305",
            "multisportarena@hotmail.com",
            "www.multisportarena.com"
        );
        club10.setSports(List.of(football, volley, tennis));
        club10.setNumberOfCourts(List.of(5, 2, 18));
        club10.setSchedule(new Schedule("08:00", "23:30"));
        club10.setPriceRange(new PriceRange(15, 32, "€/hora"));
        setClubImage(club10, "/images/multisport_arena.jpg");
        clubService.save(club10);

       Club club11 = new Club(
            "Club Deportivo La Vega",
            "Córdoba",
            "Camino de la Vega, 11",
            "684274306",
            "info@clublavega.com",
            "www.clublavega.com"
        );

        club11.setSports(List.of(paddle, volley));
        club11.setNumberOfCourts(List.of(5, 2));
        club11.setSchedule(new Schedule("08:00", "22:30"));
        club11.setPriceRange(new PriceRange(20, 32, "€/hora"));
        setClubImage(club11, "/images/lavega.jpg");
        clubService.save(club11);
        
        Match match1 = new Match(date1,true,false,true,0,120,pedro,3.49f,tennis,club1);
        Match match2 = new Match(date2,false,true,true,1,90,maria,8.99f, paddle,club2);
        Match match3 = new Match(date3,true,false,true,1,120,luis,6.49f,tennis,club3);
        Match match4 = new Match(date4,true,false,true,0,90,juan,4.49f,football,club4);
        Match match5 = new Match(date5,true,false,true,1,120,silvia,3.25f,volley,club5);
        Match match6 = new Match(date6, true, false, true, 0, 90, carlos, 5.25f, football, club6);
        Match match7 = new Match(date7, false, true, true, 1, 120, ana, 4.75f, volley, club8);
        Match match8 = new Match(date8, true, false, true, 0, 120, fernando, 6.10f, volley, club5);
        Match match9 = new Match(date9, true, false, true, 1, 90, jorge, 3.95f, volley, club10);
        Match match10 = new Match(date10, false, true, true, 0, 120, pedro, 7.20f, tennis, club9);
        Match match11 = new Match(date11, true, false, true, 1, 90, maria, 5.85f, paddle, club11);
        Match match12 = new Match(date12, true, false, true, 0, 90, carlos, 4.15f, football, club7);

        admin.addSport(tennis, 7.00f);
        pedro.addSport(tennis, 5.12f);
        maria.addSport(paddle, 4.57f);
        luis.addSport(tennis, 6.03f);
        juan.addSport(football, 3.51f);
        carlos.addSport(football, 4.32f);
        silvia.addSport(volley, 5.37f);
        ana.addSport(volley,5.41f);
        fernando.addSport(volley, 4.96f);
        jorge.addSport(volley, 4.73f);
        
        userSportProfileService.save(admin.getProfileForSport(tennis));
        userSportProfileService.save(pedro.getProfileForSport(tennis));
        userSportProfileService.save(maria.getProfileForSport(paddle));
        userSportProfileService.save(luis.getProfileForSport(tennis));
        userSportProfileService.save(juan.getProfileForSport(football));
        userSportProfileService.save(carlos.getProfileForSport(football));
        userSportProfileService.save(silvia.getProfileForSport(volley));
        userSportProfileService.save(ana.getProfileForSport(volley));
        userSportProfileService.save(fernando.getProfileForSport(volley));
        userSportProfileService.save(jorge.getProfileForSport(volley));

        match1.setTeam1Players(Set.of(match1.getOrganizer()));
        match1.setTeam2Players(new HashSet<>());

        match2.setTeam1Players(Set.of(match2.getOrganizer()));
        match2.setTeam2Players(new HashSet<>());

        match3.setTeam1Players(Set.of(match3.getOrganizer()));
        match3.setTeam2Players(new HashSet<>());

        match4.setTeam1Players(Set.of(match4.getOrganizer()));
        match4.setTeam2Players(new HashSet<>());

        match5.setTeam1Players(Set.of(match5.getOrganizer()));
        match5.setTeam2Players(new HashSet<>());

        match6.setTeam1Players(Set.of(match6.getOrganizer()));
        match6.setTeam2Players(new HashSet<>());

        match7.setTeam1Players(Set.of(match7.getOrganizer()));
        match7.setTeam2Players(new HashSet<>());

        match8.setTeam1Players(Set.of(match8.getOrganizer()));
        match8.setTeam2Players(new HashSet<>());

        match9.setTeam1Players(Set.of(match9.getOrganizer()));
        match9.setTeam2Players(new HashSet<>());

        match10.setTeam1Players(Set.of(match10.getOrganizer()));
        match10.setTeam2Players(new HashSet<>());

        match11.setTeam1Players(Set.of(match11.getOrganizer()));
        match11.setTeam2Players(new HashSet<>());

        match12.setTeam1Players(Set.of(match12.getOrganizer()));
        match12.setTeam2Players(new HashSet<>());
        
        matchService.save(match1);
        matchService.save(match2);
        matchService.save(match3);
        matchService.save(match4);
        matchService.save(match5);
        matchService.save(match6);
        matchService.save(match7);
        matchService.save(match8);
        matchService.save(match9);
        matchService.save(match10);
        matchService.save(match11);
        matchService.save(match12);

        ChatMessage systemMessage1 = ChatMessage.builder()
            .content(pedro.getUsername() + " ha creado el partido")
            .sender(pedro)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match1)
            .build();

        chatMessageService.save(systemMessage1);

        ChatMessage systemMessage2 = ChatMessage.builder()
            .content(maria.getUsername() + " ha creado el partido")
            .sender(maria)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match2)
            .build();
        
        chatMessageService.save(systemMessage2);

        ChatMessage systemMessage3 = ChatMessage.builder()
            .content(luis.getUsername() + " ha creado el partido")
            .sender(luis)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match3)
            .build();
        chatMessageService.save(systemMessage3);

        ChatMessage systemMessage4 = ChatMessage.builder()
            .content(juan.getUsername() + " ha creado el partido")
            .sender(juan)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match4)
            .build();
        chatMessageService.save(systemMessage4);

        ChatMessage systemMessage5 = ChatMessage.builder()
            .content(silvia.getUsername() + " ha creado el partido")
            .sender(silvia)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match5)
            .build();
        chatMessageService.save(systemMessage5);
        
        ChatMessage systemMessage6 = ChatMessage.builder()
            .content(carlos.getUsername() + " ha creado el partido")
            .sender(carlos)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match6)
            .build();

        chatMessageService.save(systemMessage6);

        ChatMessage systemMessage7 = ChatMessage.builder()
            .content(ana.getUsername() + " ha creado el partido")
            .sender(ana)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match7)
            .build();

        chatMessageService.save(systemMessage7);

        ChatMessage systemMessage8 = ChatMessage.builder()
            .content(fernando.getUsername() + " ha creado el partido")
            .sender(fernando)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match8)
            .build();

        chatMessageService.save(systemMessage8);

        ChatMessage systemMessage9 = ChatMessage.builder()
            .content(jorge.getUsername() + " ha creado el partido")
            .sender(jorge)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match9)
            .build();

        chatMessageService.save(systemMessage9);

        ChatMessage systemMessage10 = ChatMessage.builder()
            .content(pedro.getUsername() + " ha creado el partido")
            .sender(pedro)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match10)
            .build();

        chatMessageService.save(systemMessage10);

        ChatMessage systemMessage11 = ChatMessage.builder()
            .content(maria.getUsername() + " ha creado el partido")
            .sender(maria)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match11)
            .build();

        chatMessageService.save(systemMessage11);

        ChatMessage systemMessage12 = ChatMessage.builder()
            .content(carlos.getUsername() + " ha creado el partido")
            .sender(carlos)
            .type(MessageType.JOIN)
            .timestamp(LocalDateTime.now())
            .match(match12)
            .build();

        chatMessageService.save(systemMessage12);

        Match tennisMatch1 = new Match(
            LocalDateTime.of(2025, 9, 10, 10, 30),
            true, 
            false,
            false,
            0,
            120,
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
            0,
            120,
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
            0,
            120,
            maria,
            4.50,
            tennis,
            club3
        );

        Match volleyMatch1 = new Match(
            LocalDateTime.of(2025, 10, 15, 18, 30),
            true,
            false,
            false,
            0,
            90,
            silvia,
            5.50,
            volley,
            club5
        );

        Match volleyMatch2 = new Match(
            LocalDateTime.of(2025, 10, 19, 17, 0),
            true,
            false,
            false,
            0,
            90,
            ana,
            5.25,
            volley,
            club8
        );

        tennisMatch1.setTeam1Players(Set.of(pedro));
        tennisMatch1.setTeam2Players(Set.of(maria));

        tennisMatch2.setTeam1Players(Set.of(pedro));
        tennisMatch2.setTeam2Players(Set.of(luis));

        tennisMatch3.setTeam1Players(Set.of(maria));
        tennisMatch3.setTeam2Players(Set.of(pedro));


        volleyMatch1.setTeam1Players(Set.of(silvia, ana));
        volleyMatch1.setTeam2Players(Set.of(fernando, jorge));

        volleyMatch2.setTeam1Players(Set.of(ana, silvia));
        volleyMatch2.setTeam2Players(Set.of(fernando, jorge));

        maria.addSport(tennisMatch1.getSport(), 4.57f);

        matchService.save(tennisMatch1);
        matchService.save(tennisMatch2);
        matchService.save(tennisMatch3);

        matchService.save(volleyMatch1);
        matchService.save(volleyMatch2);

        ChatMessage tennisChat1Msg1 = ChatMessage.builder()
            .content(pedro.getUsername() + " ha creado el partido")
            .sender(pedro)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch1.getDate().minusDays(1).withHour(18))
            .match(tennisMatch1)
            .build();

        ChatMessage tennisChat1Msg2 = ChatMessage.builder()
            .content(maria.getUsername() + " se ha unido al partido")
            .sender(maria)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch1.getDate().minusHours(3))
            .match(tennisMatch1)
            .build();

        chatMessageService.save(tennisChat1Msg1);
        chatMessageService.save(tennisChat1Msg2);


        ChatMessage tennisChat2Msg1 = ChatMessage.builder()
            .content(pedro.getUsername() + " ha creado el partido")
            .sender(pedro)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch2.getDate().minusDays(1).withHour(19))
            .match(tennisMatch2)
            .build();

        ChatMessage tennisChat2Msg2 = ChatMessage.builder()
            .content(luis.getUsername() + " se ha unido al partido")
            .sender(luis)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch2.getDate().minusHours(2))
            .match(tennisMatch2)
            .build();

        chatMessageService.save(tennisChat2Msg1);
        chatMessageService.save(tennisChat2Msg2);

        ChatMessage tennisChat3Msg1 = ChatMessage.builder()
            .content(maria.getUsername() + " ha creado el partido")
            .sender(maria)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch3.getDate().minusDays(1).withHour(20))
            .match(tennisMatch3)
            .build();

        ChatMessage tennisChat3Msg2 = ChatMessage.builder()
            .content(pedro.getUsername() + " se ha unido al partido")
            .sender(pedro)
            .type(MessageType.JOIN)
            .timestamp(tennisMatch3.getDate().minusHours(1))
            .match(tennisMatch3)
            .build();

        chatMessageService.save(tennisChat3Msg1);
        chatMessageService.save(tennisChat3Msg2);

        ChatMessage volleyChat1Msg1 = ChatMessage.builder()
            .content(silvia.getUsername() + " ha creado el partido")
            .sender(silvia)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch1.getDate().minusDays(1).withHour(20))
            .match(volleyMatch1)
            .build();

        ChatMessage volleyChat1Msg2 = ChatMessage.builder()
            .content(ana.getUsername() + " se ha unido al partido")
            .sender(ana)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch1.getDate().minusHours(4))
            .match(volleyMatch1)
            .build();

        ChatMessage volleyChat1Msg3 = ChatMessage.builder()
            .content(fernando.getUsername() + " se ha unido al partido")
            .sender(fernando)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch1.getDate().minusHours(3))
            .match(volleyMatch1)
            .build();

        ChatMessage volleyChat1Msg4 = ChatMessage.builder()
            .content(jorge.getUsername() + " se ha unido al partido")
            .sender(jorge)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch1.getDate().minusHours(2))
            .match(volleyMatch1)
            .build();

        chatMessageService.save(volleyChat1Msg1);
        chatMessageService.save(volleyChat1Msg2);
        chatMessageService.save(volleyChat1Msg3);
        chatMessageService.save(volleyChat1Msg4);

        ChatMessage volleyChat2Msg1 = ChatMessage.builder()
            .content(ana.getUsername() + " ha creado el partido")
            .sender(ana)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch2.getDate().minusDays(1).withHour(19))
            .match(volleyMatch2)
            .build();

        ChatMessage volleyChat2Msg2 = ChatMessage.builder()
            .content(silvia.getUsername() + " se ha unido al partido")
            .sender(silvia)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch2.getDate().minusHours(5))
            .match(volleyMatch2)
            .build();

        ChatMessage volleyChat2Msg3 = ChatMessage.builder()
            .content(fernando.getUsername() + " se ha unido al partido")
            .sender(fernando)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch2.getDate().minusHours(4))
            .match(volleyMatch2)
            .build();

        ChatMessage volleyChat2Msg4 = ChatMessage.builder()
            .content(jorge.getUsername() + " se ha unido al partido")
            .sender(jorge)
            .type(MessageType.JOIN)
            .timestamp(volleyMatch2.getDate().minusHours(3))
            .match(volleyMatch2)
            .build();

        chatMessageService.save(volleyChat2Msg1);
        chatMessageService.save(volleyChat2Msg2);
        chatMessageService.save(volleyChat2Msg3);
        chatMessageService.save(volleyChat2Msg4);

        matchService.applyMatchResult(
            tennisMatch1,
            new MatchResult("Pedro Garcia", "Maria Lopez",List.of(6,4,6),List.of(3,6,2)) // Pedro wins
        );

        matchService.applyMatchResult(
            tennisMatch2,
            new MatchResult("Pedro Garcia", "Luis Sanchez",List.of(3,4), List.of(6,6)) // Pedro loses
        );

        matchService.applyMatchResult(
            tennisMatch3,
            new MatchResult("Maria Lopez", "Pedro Garcia",List.of(2,1), List.of(6,6)) // Pedro wins
        );

        matchService.applyMatchResult(
            volleyMatch1,
            new MatchResult("Equipo Silvia","Equipo Fernando",List.of(25, 25),List.of(18, 22))
        );

        matchService.applyMatchResult(
            volleyMatch2,
            new MatchResult("Equipo Ana","Equipo Fernando",List.of(25, 22, 25),List.of(20, 25, 19))
        );
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
