package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.SportDTO;
import es.codeurjc.dto.SportMapper;
import es.codeurjc.model.Mode;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.Sport;
import es.codeurjc.repository.ClubRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.SportRepository;
import es.codeurjc.service.SportService;

@Tag("unit")
@ActiveProfiles("test")
public class SportServiceUnitaryTest {
    private MatchRepository matchRepository;
    private ClubRepository clubRepository;
    private SportRepository sportRepository;
    private SportService sportService;
    private SportMapper mapper;

    @BeforeEach
    public void setUp() {
        sportRepository = mock(SportRepository.class);
        matchRepository = mock(MatchRepository.class);
        clubRepository = mock(ClubRepository.class);
        mapper = Mappers.getMapper(SportMapper.class);
        sportService = new SportService(sportRepository, matchRepository, clubRepository, mapper);

    }

    @Test
    public void deleteSportWithNoMatchesOrClubsAssociatedTest() {
        //GIVEN
        long id = 3L;
        Sport sport = new Sport();
        sport.setId(id);

        //WHEN
        when(sportRepository.findById(id)).thenReturn(Optional.of(sport));
        when(matchRepository.existsBySportId(id)).thenReturn(false);
        when(clubRepository.existsBySportsId(id)).thenReturn(false);

        sportService.delete(id);

        //THEN
        verify(sportRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteSportWithMatchesAssociatedTest() {
        //GIVEN
        long id = 7L;
        Sport sport = new Sport();
        sport.setId(id);
        
        //WHEN
        when(sportRepository.findById(id)).thenReturn(Optional.of(sport));
        when(matchRepository.existsBySportId(id)).thenReturn(true);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> sportService.delete(id));
        
        //THEN
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertThat(ex.getReason(),equalTo("Ese deporte no se puede borrar porque hay partidos que lo usan."));
    }

    @Test
    public void deleteSportWithoutMatchesAssociatedButWithClubsTest() {
        //GIVEN
        long id = 8L;
        Sport sport = new Sport();
        sport.setId(id);

        //WHEN
        when(sportRepository.findById(id)).thenReturn(Optional.of(sport));
        when(matchRepository.existsBySportId(id)).thenReturn(false);
        when(clubRepository.existsBySportsId(id)).thenReturn(true);

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> sportService.delete(id));

        //THEN
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertThat(ex.getReason(),equalTo("Ese deporte no se puede borrar porque hay clubes que lo usan."));
    }

    @Test
    public void getSportsTest() {
        //GIVEN
        Sport s1 = new Sport();
        s1.setName("Tenis");
        Sport s2 = new Sport();
        s2.setName("Padel");

        //WHEN
        when(sportRepository.findAll()).thenReturn(List.of(s1, s2));

        Collection<SportDTO> result = sportService.getSports();

        //THEN
        assertThat(result, hasSize(2));
    }

    @Test
    public void getSportTest() {
        //GIVEN
        long id = 1L;
        Sport sport = new Sport();
        sport.setId(id);
        sport.setName("Tenis");

        //WHEN
        when(sportRepository.findById(id)).thenReturn(Optional.of(sport));

        SportDTO dto = sportService.getSport(id);

        //THEN
        assertThat(dto.name(), equalTo("Tenis"));
    }

    @Test
    public void getNonExistingSport() {
        //GIVEN
        long id = 2L;
        Optional<Sport> emptySport = Optional.empty();

        //WHEN
        when(sportRepository.findById(id)).thenReturn(emptySport);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> sportService.getSport(id));

        //THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertThat(ex.getReason(),equalTo("Deporte no encontrado"));
        
    }

    @Test
    public void testCreateSport() throws IOException {
        //GIVEN
        long id = 5L;
        Sport sport = new Sport(
            "Rugby",
            List.of(new Mode("Union",30)),
            ScoringType.SCORE
        );
        SportDTO sportDTO = mapper.toDTO(sport);

        //WHEN
        when(sportRepository.save(any(Sport.class))).thenAnswer(inv -> {
            Sport saved = inv.getArgument(0);
            saved.setId(id);
            return saved;
        });

        //THEN
        SportDTO createdSport = sportService.createSport(sportDTO);

        assertEquals("Rugby", createdSport.name());
        assertThat(createdSport.id(), is(5L));
    }

    @Test
    public void replaceSportTest() {
        //GIVEN
        long id = 6L;
        Sport updatedSport = new Sport("Rugby", List.of(new Mode("Union",30)),ScoringType.SCORE);
        SportDTO sportDTO = mapper.toDTO(updatedSport);

        //WHEN
        when(sportRepository.existsById(id)).thenReturn(true);

        //THEN
        SportDTO replaced = sportService.replaceSport(id, sportDTO);

        assertEquals("Rugby", replaced.name());
        verify(sportRepository, times(1)).save(any(Sport.class));
    }


}
