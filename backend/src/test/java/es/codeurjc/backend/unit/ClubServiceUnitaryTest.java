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

    private Long defaultClubId;

    private Club defaultClub;
    private Club updatedClub;

    private ClubDTO updatedClubDTO;

    @BeforeEach
    public void setUp() {
        clubRepository = mock(ClubRepository.class);
        mapper = Mappers.getMapper(ClubMapper.class);
        clubService = new ClubService(clubRepository, mapper);

        defaultClubId = 1L;

        defaultClub = new Club(
                "Club Deportivo",
                "Madrid",
                "Calle Falsa 123",
                "912345678",
                "clubdeportivo@emeal.com",
                "www.clubdeportivo.com"
        );
        defaultClub.setId(defaultClubId);

        updatedClub = new Club(
                "Club Social",
                "Barcelona",
                "Calle Falsa 456",
                "987654321",
                "clubsocial@emeal.com",
                "www.clubsocial.com"
        );
        updatedClub.setId(defaultClubId);

        updatedClubDTO = mapper.toDTO(updatedClub);
    }

    @Test
    public void getClubsShouldReturnCorrectPage(){
        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        List<Club> clubsList = List.of(defaultClub);
        Page<Club> clubPage = new PageImpl<>(clubsList,pageable,clubsList.size());

        //WHEN
        when(clubRepository.findAll(pageable)).thenReturn(clubPage);

        Page<ClubDTO> result = clubService.getClubs(pageable);
        Page<ClubDTO> expected = clubPage.map(c -> mapper.toDTO(c));

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));
    }

    @Test
    public void getClubByIdShouldReturnCorrectClub(){
        //GIVEN
        Optional<Club> optionalClub = Optional.of(defaultClub);
        //WHEN
        when(clubRepository.findById(defaultClubId)).thenReturn(optionalClub);
        ClubDTO result = clubService.getClub(defaultClubId);
        ClubDTO expected = mapper.toDTO(defaultClub);
        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void deleteExistingClubShouldSucceed(){
        //GIVEN
        Optional<Club> optionalClub = Optional.of(defaultClub);

        //WHEN
        when(clubRepository.findById(defaultClubId)).thenReturn(optionalClub);
        clubService.delete(defaultClubId);

        //THEN
        verify(clubRepository,times(1)).deleteById(defaultClubId);
    }

    @Test
    public void deleteNonExistingClubShouldThrowException(){
        //GIVEN
        Optional<Club> emptyClub = Optional.empty();

        //WHEN
        when(clubRepository.findById(defaultClubId + 1)).thenReturn(emptyClub);

        //THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> clubService.delete(defaultClubId + 1));
        assertThat(ex.getMessage(),equalTo("Club with id " + (defaultClubId + 1) + " does not exist."));

    }

    @Test
    public void saveClubShouldSucceed(){
        //WHEN
        when(clubRepository.save(defaultClub)).thenReturn(defaultClub);
        Club savedClub = clubService.save(defaultClub);

        //THEN
        assertThat(savedClub, equalTo(defaultClub));
    }
    @Test
    public void replaceNonExistingClubShouldThrowException(){
        //GIVEN
        Optional<Club> emptyClub = Optional.empty();

        //WHEN
        when(clubRepository.findById(defaultClubId + 1)).thenReturn(emptyClub);

        //THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->  clubService.replaceClub(defaultClubId + 1, updatedClubDTO));
        assertThat(ex.getMessage(),equalTo("Club with id " + (defaultClubId + 1) + " does not exist."));

    }

    @Test
    public void replaceExistingClubShouldSucceed(){
        //GIVEN
        Optional<Club> clubOptional = Optional.of(defaultClub);

        //WHEN
        when(clubRepository.existsById(defaultClubId)).thenReturn(true);
        when(clubRepository.findById(defaultClubId)).thenReturn(clubOptional);
        ClubDTO replacedClubDTO = clubService.replaceClub(defaultClubId, updatedClubDTO);

        //THEN
        assertThat(updatedClubDTO, equalTo(replacedClubDTO));
        verify(clubRepository,times(1)).save(any(Club.class));

    }


}
