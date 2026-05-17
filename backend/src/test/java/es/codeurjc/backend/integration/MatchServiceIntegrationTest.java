package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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

import es.codeurjc.dto.BasicClubDTO;
import es.codeurjc.dto.BasicUserDTO;
import es.codeurjc.dto.ClubDTO;
import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.dto.MatchResultDTO;
import es.codeurjc.dto.SportDTO;
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

    private static final long DEFAULT_MATCH_ID = 1L;
    private static final long MATCH_TO_DELETE_ID = 3L;
    private static final long MATCH_TO_JOIN_ID = 4L;
    private static final long MATCH_TO_REPLACE_ID = 5L;
    private static final long MATCH_WITH_RESULT_ID = 13L;

    private static final long DEFAULT_CLUB_ID = 1L;
    private static final long SECOND_CLUB_ID = 2L;

    private static final long DEFAULT_SPORT_ID = 1L;
    private static final long DEFAULT_USER_ID = 2L;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private Sport defaultSport;
    private Club defaultClub;
    private ClubDTO secondClubDTO;

    private int getTotalMatches() {
        return matchService.findAll().size();
    }

    private Match createBaseMatch() {
        Match match = new Match();
        match.setDate(LocalDateTime.now().plusDays(10));
        match.setType(true);
        match.setIsPrivate(false);
        match.setPrice(15.0f);
        match.setModeSelected(0);
        match.setDuration(120);
        match.setSport(defaultSport);
        match.setClub(defaultClub);
        return match;
    }

    private MatchDTO createUpdatedMatchDTO(Long id) {
        MatchDTO currentMatch = matchService.getMatch(id);

        BasicClubDTO club = new BasicClubDTO(
            secondClubDTO.id(),
            secondClubDTO.name(),
            secondClubDTO.city()
        );

        return new MatchDTO(
            id,
            LocalDateTime.of(2025, 12, 25, 12, 0),
            true,
            false,
            true,
            0,
            120,
            currentMatch.organizer(),
            5.49f,
            secondClubDTO.sports().get(0),
            club,
            currentMatch.team1Players(),
            currentMatch.team2Players()
        );
    }

    private MatchResultDTO createDefaultResult() {
        return new MatchResultDTO(
            "A",
            "B",
            0,
            0,
            new ArrayList<>(List.of(6,3,7)),
            new ArrayList<>(List.of(4,6,5))
        );
    }

    @BeforeEach
    public void setUp() {
        defaultSport = sportService.findById(DEFAULT_SPORT_ID).orElseThrow();
        defaultClub = clubService.findById(DEFAULT_CLUB_ID).orElseThrow();
        secondClubDTO = clubService.getClub(SECOND_CLUB_ID);
    }

    @Test
    @Order(1)
    public void getMatchesShouldReturnPageOfMatchesDTOs(){
        int numMatches = getTotalMatches();
        PageRequest pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
        Page<MatchDTO> pageOfMatches = matchService.getMatches(pageable);
        assertThat((int) pageOfMatches.getTotalElements(), equalTo(numMatches));
    }

    @Test
    @Order(2)
    public void getMatchByIdShouldReturnMatchDTO(){
        MatchDTO matchDTO = matchService.getMatch(DEFAULT_MATCH_ID);
        assertThat(matchDTO.id(), equalTo(DEFAULT_MATCH_ID));
        assertThat(matchDTO.club().name(), equalTo("Tennis Club Elite"));
    }

    @Test
    @Order(3)
    public void deleteNonExistingMatchShouldThrowIllegalArgumentException(){
        long numMatches = getTotalMatches();
        long id = numMatches + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));
    }

    
    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteExistingMatchShouldRemoveMatch(){
        int numMatches = getTotalMatches();
        matchService.delete(MATCH_TO_DELETE_ID);
        List<Match> matches = matchService.findAll();
        assertThat(matchService.exist(MATCH_TO_DELETE_ID), equalTo(false));
        assertThat(matches.size(), equalTo(numMatches - 1));
    } 

    @Test
    @Order(5)
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void createMatchShouldReturnSavedMatch(){
        int numMatchesBefore = getTotalMatches();

        Match match = createBaseMatch();

        MatchDTO createdMatch = matchService.createMatch(mapper.toDTO(match));

        int numMatchesAfter = getTotalMatches();
        assertThat(numMatchesAfter, equalTo(numMatchesBefore + 1));
        assertThat(matchService.exist(createdMatch.id()), equalTo(true));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void joinAndLeaveMatchAsRegularUserShouldSucceed(){
        String selectedTeam = "B";
        MatchDTO match = matchService.getMatch(MATCH_TO_JOIN_ID);
        User loggedUser = userService.getLoggedUser();
        int teamSizeBefore = match.team2Players().size();
        MatchDTO joinedMatchDTO = matchService.joinMatch(MATCH_TO_JOIN_ID, selectedTeam);

        int teamSizeAfter = joinedMatchDTO.team2Players().size();
        
        assertThat(teamSizeAfter,equalTo(teamSizeBefore + 1));

        
        teamSizeBefore = teamSizeAfter;
        matchService.leaveMatch(MATCH_TO_JOIN_ID, loggedUser);
        MatchDTO matchLeftDTO = matchService.getMatch(MATCH_TO_JOIN_ID);

        assertThat(matchLeftDTO.team2Players().size(),equalTo(teamSizeBefore - 1));

    }

    @Test 
    @Order(7)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void replaceMatchShouldReturnReplacedMatchDTO(){
        MatchDTO updatedMatchDTO = createUpdatedMatchDTO(MATCH_TO_REPLACE_ID);

        MatchDTO replacedMatch = matchService.replaceMatch(MATCH_TO_REPLACE_ID, updatedMatchDTO);
        assertThat(replacedMatch.id(), equalTo(MATCH_TO_REPLACE_ID));
        assertThat(replacedMatch.type(), equalTo(true));
        assertThat(replacedMatch.isPrivate(), equalTo(false));
        assertThat(replacedMatch.state(), equalTo(true));
        assertThat(replacedMatch.price(), equalTo(5.49f));
        assertThat(replacedMatch.organizer(), isA(BasicUserDTO.class));
        assertThat(replacedMatch.sport(), isA(SportDTO.class));
        assertThat(replacedMatch.club(), isA(BasicClubDTO.class));
        assertThat(replacedMatch.team1Players(), isA(Set.class));
        assertThat(replacedMatch.team2Players(), isA(Set.class));
    }

    @Test
    @Order(8)
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void updateMatchResultWithValidResultShouldUpdateResult(){
        MatchDTO match = matchService.getMatch(MATCH_WITH_RESULT_ID);
        
        assertThat(match.state(), equalTo(false));

        MatchResultDTO resultDTO = createDefaultResult();

        matchService.updateMatchResult(MATCH_WITH_RESULT_ID, resultDTO);

        MatchDTO matchWithResult = matchService.getMatch(MATCH_WITH_RESULT_ID);
        assertThat(matchWithResult.result(), isA(MatchResultDTO.class));
        assertThat(matchWithResult.result().team1GamesPerSet(), equalTo(List.of(6,3,7)));
        assertThat(matchWithResult.result().team2GamesPerSet(), equalTo(List.of(4,6,5)));
        assertThat(matchWithResult.state(), equalTo(false));
    }

    @Test
    @Order(9)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void addPlayerToIncompleteTeamAsAdminShouldAddPlayerToTeam(){
        MatchDTO match = matchService.getMatch(MATCH_TO_JOIN_ID);
        int teamSizeBefore = match.team1Players().size();
        matchService.addPlayerToTeam1(MATCH_TO_JOIN_ID, DEFAULT_USER_ID);
        MatchDTO matchAfter = matchService.getMatch(MATCH_TO_JOIN_ID);
        int teamSizeAfter = matchAfter.team1Players().size();
        assertThat(teamSizeAfter, equalTo(teamSizeBefore + 1));
    }

    @Test
    @Order(10)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void removePlayerFromTeamAsAdminShouldRemovePlayerFromTeam(){
        MatchDTO matchInitial = matchService.getMatch(MATCH_TO_JOIN_ID);
        assertFalse(matchInitial.team1Players().isEmpty());
        matchService.addPlayerToTeam1(MATCH_TO_JOIN_ID, DEFAULT_USER_ID);

        MatchDTO match = matchService.getMatch(MATCH_TO_JOIN_ID);
        int teamSizeBefore = match.team1Players().size();
        matchService.removePlayerFromTeam1(MATCH_TO_JOIN_ID, DEFAULT_USER_ID);

        MatchDTO matchAfter = matchService.getMatch(MATCH_TO_JOIN_ID);
        int teamSizeAfter = matchAfter.team1Players().size();
        assertThat(teamSizeAfter, equalTo(teamSizeBefore - 1));
    }
}
