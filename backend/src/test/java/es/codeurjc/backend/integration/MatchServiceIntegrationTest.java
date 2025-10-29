package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import java.util.Random;

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

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.model.Match;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.MatchService;

@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")

public class MatchServiceIntegrationTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchService matchService;

    

    @Test
    @Order(1)
    public void getMatchesIntegrationTest(){
        int numMatches = 4;
        PageRequest pageable = PageRequest.of(0, 10);
        Page<MatchDTO> pageOfMatches = matchService.getMatches(pageable);
        assertThat(pageOfMatches.getNumberOfElements(), equalTo(numMatches));
    }

    @Test
    @Order(2)
    public void getMatchByIdIntegrationTest(){
        long id = 1;
        MatchDTO matchDTO = matchService.getMatch(id);
        assertThat(matchDTO.id(), equalTo(id));
        assertThat(matchDTO.club().name(), equalTo("Tennis Club Elite"));
    }

    @Test
    @Order(3)
    public void deleteNonExistingMatchIntegrationTest(){
        Random random = new Random();
        int numMatches = matchService.findAll().size();
        long id = numMatches + random.nextLong(100);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));
    }

    @Test
    @Order(4)
    public void deleteExistingMatchIntegrationTest(){
        Random random = new Random();
        int numMatches = 4;
        long id = 1 + random.nextLong(numMatches);
        matchService.delete(id);
        List<Match> matches = matchService.findAll();
        assertThat(matchService.exist(id), equalTo(false));
        assertThat(matches.size(), equalTo(numMatches - 1));
    } 

    @Test 
    @Order(5)
    public void testSaveMatchIntegrationTest(){
        int numMatchesBefore = matchService.findAll().size();

        Match match = new Match();
        match.setDate(java.time.LocalDateTime.now().plusDays(10));
        match.setType(true);
        match.setIsPrivate(false);
        match.setState(false);
        match.setPrice(15.0f);
        match.setSport("Tenis");

        Match savedMatch = matchService.save(match);
        assertNotNull(savedMatch);
        assertNotNull(savedMatch.getId());

        int numMatchesAfter = matchService.findAll().size();
        assertThat(numMatchesAfter, equalTo(numMatchesBefore + 1));
        assertThat(matchService.exist(savedMatch.getId()), equalTo(true));
    }

    
}
