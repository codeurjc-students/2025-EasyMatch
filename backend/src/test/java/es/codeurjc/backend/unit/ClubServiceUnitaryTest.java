package es.codeurjc.backend.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.ClubDTO;
import es.codeurjc.dto.ClubMapper;
import es.codeurjc.model.Club;
import es.codeurjc.repository.ClubRepository;
import es.codeurjc.service.ClubService;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Tag("unit")
@ActiveProfiles("test")
public class ClubServiceUnitaryTest {

    private ClubRepository clubRepository;
    private ClubService clubService;
    private ClubMapper mapper;

    @BeforeEach
    public void setUp() {
        clubRepository = mock(ClubRepository.class);
        mapper = Mappers.getMapper(ClubMapper.class);
        clubService = new ClubService(clubRepository, mapper);
    }

    @Test
    public void getClubsTest(){
        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        Club club1 = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        List<Club> clubsList = List.of(club1);
        Page<Club> clubPage = new PageImpl<>(clubsList,pageable,clubsList.size());

        //WHEN
        when(clubRepository.findAll(pageable)).thenReturn(clubPage);

        Page<ClubDTO> result = clubService.getClubs(pageable);
        Page<ClubDTO> expected = clubPage.map(c -> mapper.toDTO(c));

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));
    }

    @Test
    public void getClubByIdTest(){
        //GIVEN
        long id = 1L;
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        club.setId(id);
        Optional<Club> optionalClub = Optional.of(club);
        //WHEN
        when(clubRepository.findById(id)).thenReturn(optionalClub);
        ClubDTO result = clubService.getClub(id);
        ClubDTO expected = mapper.toDTO(club);
        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void deleteExistingClubTest(){
        //GIVEN
        long id = 1L;
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        club.setId(id);
        Optional<Club> optionalClub = Optional.of(club);

        //WHEN
        when(clubRepository.findById(id)).thenReturn(optionalClub);
        clubService.delete(id);

        //THEN
        verify(clubRepository,times(1)).deleteById(id);
    }

    @Test
    public void deleteNonExistingClubTest(){
        //GIVEN
        long id = 5L;
        Optional<Club> emptyClub = Optional.empty();

        //WHEN
        when(clubRepository.findById(id)).thenReturn(emptyClub);

        //THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> clubService.delete(id));
        assertThat(ex.getMessage(),equalTo("Club with id " + id + " does not exist."));

    }

    @Test
    public void saveClubTest(){
        //GIVEN
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");

        //WHEN
        when(clubRepository.save(club)).thenReturn(club);
        Club savedClub = clubService.save(club);

        //THEN
        assertThat(savedClub, equalTo(club));
    }
    @Test
    public void replaceNonExistingClubTest(){
        //GIVEN
        long id = 9L;
        Optional<Club> emptyClub = Optional.empty();
        Club updatedClub =  new Club();
        ClubDTO updatedClubDTO = mapper.toDTO(updatedClub);

        //WHEN
        when(clubRepository.findById(id)).thenReturn(emptyClub);

        //THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->  clubService.replaceClub(id, updatedClubDTO));
        assertThat(ex.getMessage(),equalTo("Club with id " + id + " does not exist."));

    }

    @Test
    public void replaceExistingClubTest(){
        //GIVEN
        long id = 1L;
        Optional<Club> clubOptional = Optional.of(new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com"));
        Club updatedClub = new Club("Club Social","Barcelona","Calle Falsa 456","987654321","clubsocial@emeal.com","www.clubsocial.com");
        updatedClub.setId(id);
        ClubDTO updatedClubDTO = mapper.toDTO(updatedClub);

        //WHEN
        when(clubRepository.existsById(id)).thenReturn(true);
        when(clubRepository.findById(id)).thenReturn(clubOptional);
        ClubDTO replacedClubDTO = clubService.replaceClub(id, updatedClubDTO);

        //THEN
        assertThat(updatedClubDTO, equalTo(replacedClubDTO));
        verify(clubRepository,times(1)).save(any(Club.class));

    }


}
