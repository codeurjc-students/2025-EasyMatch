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
import es.codeurjc.model.Match;

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

        Match match1 = new Match(date1, false, true, false, "Ana", "Voleibol");
        Match match2 = new Match(date2, true, true, false, "Carlos", "Baloncesto");
        Match match3 = new Match(date3, false, false, true, "Sofia", "Tenis");
        Match match4 = new Match(date4, true, false, true, "Miguel", "Futbol");

        List<Match> matchList = List.of(match1,match2,match3,match4);

        Page<Match> page = new PageImpl<>(matchList,pageable,matchList.size());

        //WHEN
        when(matchRepository.findAll(pageable)).thenReturn(page);

        Page<MatchDTO> result = matchService.getMatches(pageable);
        Page<MatchDTO> expected =page.map(m -> mapper.toDTO(m));

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));
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

}
