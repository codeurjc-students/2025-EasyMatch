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

    @Test
    @Order(1)
    public void getSportsTest(){
        int numSports = sportService.findAll().size();
        Collection<SportDTO> sports = sportService.getSports();
        assertThat(sports.size(), equalTo(numSports));
    }

    @Test
    @Order(2)
    public void getSportByIdTest(){
        long id = 1L;
        SportDTO sportDTO = sportService.getSport(id);
        assertThat(sportDTO.id(), equalTo(id));
        assertThat(sportDTO.name(), equalTo("Tenis"));
        assertThat(sportDTO.modes(), isA(List.class));
        assertThat(sportDTO.scoringType(), equalTo(ScoringType.SETS));
    }

    @Test
    @Order(3)
    public void deleteNonExistingMatchTest(){
        long numSports = sportService.findAll().size();
        long id = numSports + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> sportService.delete(id));
        assertThat(ex.getMessage(),equalTo("Sport with id " + id + " does not exist."));
    }

    @Test
    @Order(4)
    @Commit
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void createMatchTest(){
        int numSportsBefore = sportService.findAll().size();

        Sport sport = new Sport();
        sport.setName("Rugby");
        Mode mode = new Mode("Union", 30);
        sport.setModes(List.of(mode));
        sport.setScoringType(ScoringType.SCORE);
        SportDTO sportDTO = mapper.toDTO(sport);
        SportDTO createdSport = sportService.createSport(sportDTO);

        int numSportsAfter = sportService.findAll().size();
        assertThat(numSportsAfter, equalTo(numSportsBefore + 1));
        assertThat(sportService.exist(createdSport.id()), equalTo(true));
    }

    
    @Test
    @Order(5)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void deleteExistingMatchTest(){
        long id = 5L;
        int numSports = sportService.findAll().size();
        sportService.delete(id);
        List<Sport> sports = sportService.findAll();
        assertThat(sportService.exist(id), equalTo(false));
        assertThat(sports.size(), equalTo(numSports - 1));
    } 


    
}
