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
import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.UserService;
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
    private UserService userService;
    private MatchService matchService;
    private MatchMapper mapper;
    private UserMapper userMapper;
    
    
    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        matchRepository = mock(MatchRepository.class);
        mapper = Mappers.getMapper(MatchMapper.class);
        matchService = new MatchService(matchRepository, mapper, userService,userMapper);
    }

    @Test
    public void getMatchesUnitaryTest(){   

        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);

        Match match1 = new Match(null, false, true, false, new User(),3.50, new Sport(),null);
        Match match2 = new Match(null, true, true, false, new User(),2.75, new Sport(),null);
        Match match3 = new Match(null, false, false, true, new User(),9.95, new Sport(),null);
        Match match4 = new Match(null, true, false, true, new User(),2.50, new Sport(),null);

        List<Match> matchList = List.of(match1,match2,match3,match4);

        Page<Match> matchPage = new PageImpl<>(matchList,pageable,matchList.size());

        //WHEN
        when(matchRepository.findAll(pageable)).thenReturn(matchPage);

        Page<MatchDTO> result = matchService.getMatches(pageable);
        Page<MatchDTO> expected = matchPage.map(m -> mapper.toDTO(m));

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));
    }
    @Test
    public void getMatchByIdUnitaryTest(){
        //GIVEN
        long id = 1;
        Match match = new Match(null, true, false, true, null,5.00, null,null);
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
        Match match = new Match(null, false, true, false, null,2.00, null,null);
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
        //GIVEN
        Random random = new Random();
        long id = 1 + random.nextInt(100);
        Optional<Match> match = Optional.empty();

        //WHEN
        when(matchRepository.findById(id)).thenReturn(match);

        //THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> matchService.delete(id));
        assertThat(ex.getMessage(),equalTo("Match with id " + id + " does not exist."));
    }

    @Test
    public void createMatchUnitaryTest(){
        //GIVEN
        UserDTO userDTO = new UserDTO(
            1L,
            "Pedro García",
            "pedro123",
            "pedro@emeal.com",
            "pedroga4",
            LocalDateTime.of(1995, 5, 10, 0, 0),
            true,
            "Jugador de pádel",
            4.5f,
            List.of("USER")
        );
        
        Match match = new Match(null, true, false, true, null,4.00, null,null);
        MatchDTO matchDTO = mapper.toDTO(match);
        //WHEN
        when(userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(matchRepository.save(match)).thenReturn(match);
        MatchDTO createdMatch = matchService.createMatch(matchDTO);
        //THEN
        assertThat(createdMatch.id(), equalTo(matchDTO.id()));
        assertThat(createdMatch.organizer().id(), equalTo(userDTO.id()));
        assertThat(createdMatch.team1Players(), contains(userDTO));
        assertThat(createdMatch.state(), is(true));
    }

}
