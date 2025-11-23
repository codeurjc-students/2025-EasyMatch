package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.Sport;
import es.codeurjc.model.User;
import es.codeurjc.service.ClubService;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.SportService;
import es.codeurjc.service.UserService;
import jakarta.transaction.Transactional;

@Tag("integration")
@Transactional
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")

public class MatchServiceIntegrationTest {

    @Autowired
    private MatchService matchService;
    
    @Autowired  
    private MatchMapper mapper;

    @Autowired
    private ClubService clubService;

    @Autowired
    private SportService sportService;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    public void getMatchesIntegrationTest(){
        int numMatches = matchService.findAll().size();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<MatchDTO> pageOfMatches = matchService.getMatches(pageable);
        assertThat(pageOfMatches.getNumberOfElements(), equalTo(numMatches));
    }

    @Test
    @Order(2)
    public void getMatchByIdIntegrationTest(){
        long id = 1L;
        MatchDTO matchDTO = matchService.getMatch(id);
        assertThat(matchDTO.id(), equalTo(id));
        assertThat(matchDTO.club().name(), equalTo("Tennis Club Elite"));
    }

    @Test
    @Order(3)
    public void deleteNonExistingMatchIntegrationTest(){
        long numMatches = matchService.findAll().size();
        long id = numMatches + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));
    }

    @Test
    @Order(4)
    public void deleteExistingMatchIntegrationTest(){
        long id = 3L;
        int numMatches = matchService.findAll().size();
        matchService.delete(id);
        List<Match> matches = matchService.findAll();
        assertThat(matchService.exist(id), equalTo(false));
        assertThat(matches.size(), equalTo(numMatches - 1));
    } 

    @Test
    @Order(5)
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void createMatchIntegrationTest(){
        int numMatchesBefore = matchService.findAll().size();

        Match match = new Match();
        match.setDate(java.time.LocalDateTime.now().plusDays(10));
        match.setType(true);
        match.setIsPrivate(false);
        match.setPrice(15.0f);
        Sport sport = sportService.findById(1).orElseThrow();
        match.setSport(sport);
        Club club = clubService.findById(1).orElseThrow();
        match.setClub(club);

        MatchDTO createdMatch = matchService.createMatch(mapper.toDTO(match));

        int numMatchesAfter = matchService.findAll().size();
        assertThat(numMatchesAfter, equalTo(numMatchesBefore + 1));
        assertThat(matchService.exist(createdMatch.id()), equalTo(true));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void joinAndLeaveMatchIntegrationTest(){
        long id = 4L;
        String selectedTeam = "B";
        MatchDTO match = matchService.getMatch(id);
        User loggedUser = userService.getLoggedUser();
        int teamSizeBefore = match.team2Players().size();
        MatchDTO joinedMatchDTO = matchService.joinMatch(id, selectedTeam);

        int teamSizeAfter = joinedMatchDTO.team2Players().size();
        
        assertThat(teamSizeAfter,equalTo(teamSizeBefore + 1));

        
        teamSizeBefore = teamSizeAfter;
        matchService.leaveMatch(id, loggedUser);
        MatchDTO matchLeftDTO = matchService.getMatch(id);

        assertThat(matchLeftDTO.team2Players().size(),equalTo(teamSizeBefore - 1));

    }

    


    
}
