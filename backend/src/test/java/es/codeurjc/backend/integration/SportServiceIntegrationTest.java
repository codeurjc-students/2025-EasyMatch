package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.SportDTO;
import es.codeurjc.dto.SportMapper;
import es.codeurjc.model.Mode;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.Sport;
import es.codeurjc.service.SportService;
import jakarta.transaction.Transactional;

@Tag("integration")
@Transactional
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class SportServiceIntegrationTest {

    @Autowired
    private SportService sportService;

    @Autowired
    private SportMapper mapper;

    private static final long DEFAULT_SPORT_ID = 1L;
    private static final long SPORT_TO_DELETE_ID = 5L;

    private int getTotalSports() {
        return sportService.findAll().size();
    }

    private Sport createBaseSport() {
        Sport sport = new Sport();
        sport.setName("Rugby");
        Mode mode = new Mode("Union", 30);
        sport.setModes(List.of(mode));
        sport.setScoringType(ScoringType.SCORE);
        return sport;
    }

    @Test
    @Order(1)
    public void getSportsShouldReturnCollectionOfSports(){
        int numSports = getTotalSports();
        Collection<SportDTO> sports = sportService.getSports();
        assertThat(sports.size(), equalTo(numSports));
    }

    @Test
    @Order(2)
    public void getSportByIdShouldReturnSportDTO(){
        SportDTO sportDTO = sportService.getSport(DEFAULT_SPORT_ID);
        assertThat(sportDTO.id(), equalTo(DEFAULT_SPORT_ID));
        assertThat(sportDTO.name(), equalTo("Tenis"));
        assertThat(sportDTO.modes(), isA(List.class));
        assertThat(sportDTO.scoringType(), equalTo(ScoringType.SETS));
    }

    @Test
    @Order(3)
    public void deleteNonExistingMatchShouldThrowIllegalArgumentException(){
        long numSports = getTotalSports();
        long id = numSports + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> sportService.delete(id));
        assertThat(ex.getMessage(),equalTo("Sport with id " + id + " does not exist."));
    }

    @Test
    @Order(4)
    @Commit
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void createMatchShouldCreateNewMatch(){
        int numSportsBefore = getTotalSports();

        Sport sport = createBaseSport();
        SportDTO sportDTO = mapper.toDTO(sport);
        SportDTO createdSport = sportService.createSport(sportDTO);

        int numSportsAfter = getTotalSports();
        assertThat(numSportsAfter, equalTo(numSportsBefore + 1));
        assertThat(sportService.exist(createdSport.id()), equalTo(true));
    }

    
    @Test
    @Order(5)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void deleteExistingMatchShouldDeleteMatch(){
        int numSports = getTotalSports();
        sportService.delete(SPORT_TO_DELETE_ID);
        List<Sport> sports = sportService.findAll();
        assertThat(sportService.exist(SPORT_TO_DELETE_ID), equalTo(false));
        assertThat(sports.size(), equalTo(numSports - 1));
    } 


    
}
