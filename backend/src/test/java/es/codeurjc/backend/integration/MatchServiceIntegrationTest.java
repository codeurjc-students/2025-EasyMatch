package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.model.Match;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.MatchService;

@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@ActiveProfiles("test")

public class MatchServiceIntegrationTest {

    @Autowired
    private MatchRepository matchRepository;

    private MatchService matchService;
    private MatchMapper mapper;

    @BeforeEach
    public void setUp(){
        mapper = Mappers.getMapper(MatchMapper.class);
        matchService = new MatchService(matchRepository, mapper);
    }

    @Test
    public void getMatchesIntegrationTest(){
        int numMatches = 4;
        PageRequest pageable = PageRequest.of(0, 10);
        Page<MatchDTO> pageOfMatches = matchService.getMatches(pageable);
        assertThat(pageOfMatches.getNumberOfElements(), equalTo(numMatches));
    }

    /* @Test
    public void deleteExistingMatchIntegrationTest(){
        Random random = new Random();
        int numMatches = 4;
        long id = 1 + random.nextLong(numMatches);
        matchService.delete(id);
        List<Match> matches = matchService.findAll();
        assertThat(matchService.exist(id), equalTo(false));
        assertThat(matches.size(), equalTo(numMatches - 1));
    } */

    @Test
    public void deleteNonExistingMatchUnitaryTest(){
        Random random = new Random();
        int numMatches = 4;
        long id = numMatches + random.nextInt(100);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));

    }
}
