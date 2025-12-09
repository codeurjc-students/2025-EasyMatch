package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.ClubDTO;
import es.codeurjc.model.Club;
import es.codeurjc.service.ClubService;



@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")

public class ClubServiceIntegrationTest {
    
    @Autowired
    private ClubService clubService;

    @Test
    @Order(1)
    public void getClubsIntegrationTest(){
        int numClubs = clubService.findAll().size();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ClubDTO> pageOfClubs = clubService.getClubs(pageable);
        assertThat(pageOfClubs.getNumberOfElements(), equalTo(numClubs));
    }

    @Test
    @Order(2)
    public void getClubByIdIntegrationTest(){
        long id = 2L;
        ClubDTO clubDTO = clubService.getClub(id);
        assertThat(clubDTO.id(), equalTo(id));
        assertThat(clubDTO.name(), equalTo("Padel Pro Center"));
    }

    @Test
    @Order(3)
    public void deleteNonExistingClubIntegrationTest(){
        long numClubs = clubService.findAll().size();
        long id = numClubs + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> clubService.delete(id));
        assertThat(ex.getMessage(),equalTo("Club with id " + id + " does not exist."));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Order(4)
    public void deleteExistingClubIntegrationTest(){
        long id = 3L;
        int numClubs = clubService.findAll().size();
        clubService.delete(id);
        List<Club> clubs = clubService.findAll();
        assertThat(clubService.exist(id), equalTo(false));
        assertThat(clubs.size(), equalTo(numClubs - 1));
    } 

    @Test 
    @Order(5)
    public void testSaveClubIntegrationTest(){
        int numClubsBefore = clubService.findAll().size();

        Club club = new Club();
        club.setName("Club Deportivo");
        club.setCity("Madrid");
        club.setAddress("Calle Falsa 123");
        club.setPhone("912345678");
        club.setEmail("clubdeportivo@emeal.com");
        club.setWeb("www.clubdeportivo.com");

        Club savedClub = clubService.save(club);
        assertNotNull(savedClub);
        assertNotNull(savedClub.getId());

        int numClubsAfter = clubService.findAll().size();
        assertThat(numClubsAfter, equalTo(numClubsBefore + 1));
        assertThat(clubService.exist(savedClub.getId()), equalTo(true));
    }



}
