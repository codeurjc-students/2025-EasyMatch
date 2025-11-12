package es.codeurjc.backend.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
    public void getClubsUnitaryTest(){
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
    public void getClubByIdUnitaryTest(){
        //GIVEN
        long id = 1;
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
    public void deleteExistingClubUnitaryTest(){
        //GIVEN
        long id = 1;
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
    public void deleteNonExistingClubUnitaryTest(){
        //GIVEN
        long id = 5;
        Optional<Club> emptyClub = Optional.empty();

        //WHEN
        when(clubRepository.findById(id)).thenReturn(emptyClub);

        //THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> clubService.delete(id));
        assertThat(ex.getMessage(),equalTo("Club with id " + id + " does not exist."));

    }

    @Test
    public void saveClubUnitaryTest(){
        //GIVEN
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");

        //WHEN
        when(clubRepository.save(club)).thenReturn(club);
        Club savedClub = clubService.save(club);

        //THEN
        assertThat(savedClub, equalTo(club));
    }

}
