package es.codeurjc.backend.unit;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.MatchService;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.User;
import es.codeurjc.model.Sport;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@ActiveProfiles("test")
public class MatchServiceUnitaryTest {

    private MatchRepository matchRepository;
    private MatchService matchService;
    private MatchMapper mapper;
    
    
    @BeforeEach
    public void setUp() {
        matchRepository = mock(MatchRepository.class);
        mapper = Mappers.getMapper(MatchMapper.class);
        matchService = new MatchService(matchRepository, mapper);
    }

    @Test
    public void getMatchesUnitaryTest(){   

        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        LocalDateTime date1 = LocalDateTime.of(2025, 11, 2, 14, 00);
        LocalDateTime date2 = LocalDateTime.of(2025, 11, 4, 16, 30);
        LocalDateTime date3 = LocalDateTime.of(2025, 11, 6, 9, 45);
        LocalDateTime date4 = LocalDateTime.of(2025, 11, 8, 18, 15);

        Club club1 = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();

        Match match1 = new Match(date1, false, true, false, user1,3.50, new Sport("Volley",List.of()),club1);
        Match match2 = new Match(date2, true, true, false, user2,2.75, new Sport("Baloncesto",List.of()),club1);
        Match match3 = new Match(date3, false, false, true, user3,9.95, new Sport("Tenis",List.of()),club1);
        Match match4 = new Match(date4, true, false, true, user4,2.50, new Sport("Futbol",List.of()),club1);

        List<Match> matchList = List.of(match1,match2,match3,match4);

        Page<Match> page = new PageImpl<>(matchList,pageable,matchList.size());

        //WHEN
        when(matchRepository.findAll(pageable)).thenReturn(page);

        Page<MatchDTO> result = matchService.getMatches(pageable);
        Page<MatchDTO> expected = page.map(m -> mapper.toDTO(m));

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));
    }
    @Test
    public void getMatchByIdUnitaryTest(){
        //GIVEN
        long id = 1;
        LocalDateTime date = LocalDateTime.of(2025, 12, 12, 15, 00);
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        User organizer = new User();
        organizer.setRealname("Laura");
        Match match = new Match(date, true, false, true, organizer,5.00, new Sport("Padel",List.of()),club);
        match.setId(id);
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        MatchDTO result = matchService.getMatch(id);
        MatchDTO expected = mapper.toDTO(match);
        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void deleteExistingMatchUnitaryTest(){
        //GIVEN
        long id = 1;
        LocalDateTime date = LocalDateTime.of(2025, 10, 31, 17, 15);
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        User organizer = new User();
        organizer.setRealname("Juan");
        Match match = new Match(date, false, true, false, organizer,2.00, new Sport("Volley playa",List.of()),club);
        match.setId(id);
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        matchService.delete(id);
        //THEN
        verify(matchRepository,times(1)).deleteById(id);
    }
    @Test
    public void deleteNonExistingMatchUnitaryTest(){
        Random random = new Random();
        long id = 1 + random.nextInt(100);
        Optional<Match> match = Optional.empty();
        when(matchRepository.findById(id)).thenReturn(match);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));
    }

    @Test
    public void saveMatchUnitaryTest(){
        //GIVEN
        LocalDateTime date = LocalDateTime.of(2025, 9, 20, 11, 30);
        Club club = new Club("Club Deportivo","Madrid","Calle Falsa 123","912345678","clubdeportivo@emeal.com","www.clubdeportivo.com");
        User organizer = new User();    
        organizer.setRealname("Marta");
        Match match = new Match(date, true, false, true, organizer,4.00, new Sport("Rugby",List.of()),club);
        //WHEN
        when(matchRepository.save(match)).thenReturn(match);

        Match savedMatch = matchService.save(match);
        //THEN
        assertThat(savedMatch, equalTo(match));
    }

}
