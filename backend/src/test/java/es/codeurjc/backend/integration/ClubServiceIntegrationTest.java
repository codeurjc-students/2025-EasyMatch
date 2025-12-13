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
import es.codeurjc.dto.PriceRangeDTO;
import es.codeurjc.dto.ScheduleDTO;
import es.codeurjc.dto.SportDTO;
import es.codeurjc.model.Club;
import es.codeurjc.service.ClubService;
import jakarta.transaction.Transactional;



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


    @Test 
    @Order(4)
    public void saveClubIntegrationTest(){
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

    
    @Transactional
    @Test 
    @Order(5)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void replaceClubIntegrationTest(){
        Long id = 3L;
        List<SportDTO> sportsInitialized = clubService.getClub(id).sports();
        List<Integer> numberOfCourts = clubService.getClub(id).numberOfCourts();
        ClubDTO updatedDTO = new ClubDTO(
            id,
            "Club Actualizado",
            "Barcelona",
            "Avenida Dos 22",
            "600333444",
            "actualizado@club.com",
            "www.actualizado.com",
            new ScheduleDTO("8:00","20:00"),
            new PriceRangeDTO(10,20,"€/hora"),
            sportsInitialized,
            numberOfCourts
        );

        ClubDTO replacedClub = clubService.replaceClub(id, updatedDTO);

        assertThat(replacedClub.id(), equalTo(id));
        assertThat(replacedClub.name(), equalTo("Club Actualizado"));
        assertThat(replacedClub.city(), equalTo("Barcelona"));
        assertThat(replacedClub.address(), equalTo("Avenida Dos 22"));
        assertThat(replacedClub.phone(), equalTo("600333444"));
        assertThat(replacedClub.email(), equalTo("actualizado@club.com"));
        assertThat(replacedClub.web(), equalTo("www.actualizado.com"));
        assertThat(replacedClub.schedule(), isA(ScheduleDTO.class));
        assertThat(replacedClub.priceRange(), isA(PriceRangeDTO.class));
        assertThat(replacedClub.sports(), isA(List.class));

    }

    
    @Test
    @Order(6)
    @WithMockUser(username = "admin@emeal.com", roles = {"ADMIN"})
    public void deleteExistingClubIntegrationTest(){
        long id = 3L;
        int numClubs = clubService.findAll().size();
        clubService.delete(id);
        List<Club> clubs = clubService.findAll();
        assertThat(clubService.exist(id), equalTo(false));
        assertThat(clubs.size(), equalTo(numClubs - 1));
    } 




}
