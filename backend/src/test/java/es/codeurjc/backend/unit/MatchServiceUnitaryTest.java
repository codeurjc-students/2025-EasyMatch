package es.codeurjc.backend.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.dto.MatchResultDTO;
import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.UserService;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.Mode;
import es.codeurjc.model.ScoringType;
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
    public void getMatchesTest(){   

        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);

        Match match1 = new Match(null, false, true, false,0, new User(),3.50, new Sport(),null);
        Match match2 = new Match(null, true, true, false,0,new User(),2.75, new Sport(),null);
        Match match3 = new Match(null, false, false, true,0, new User(),9.95, new Sport(),null);
        Match match4 = new Match(null, true, false, true,0, new User(),2.50, new Sport(),null);

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
    public void getMatchByIdTest(){
        //GIVEN
        long id = 1;
        Match match = new Match(null, true, false, true,0, null,5.00, null,null);
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
    public void deleteExistingMatchTest(){
        //GIVEN
        long id = 1;
        Match match = new Match(null, false, true, false,0, null,2.00, null,null);
        match.setId(id);
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        matchService.delete(id);
        //THEN
        verify(matchRepository,times(1)).deleteById(id);
    }

    @Test
    public void deleteNonExistingMatchTest(){
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
    public void createMatchTest(){
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
        
        Match match = new Match(null, true, false, true,0, null,4.00, null,null);
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

    @Test
    public void joinExistingMatchTest(){
        //GIVEN
        long id = 4L;
        User user = new User();
        User organizer =  new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Dobles",4)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, organizer,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(user);
        //THEN
        matchService.joinMatch(id,"A");
    }

    @Test
    public void joinNonExistingMatchTest(){
        long id = 1L;
        when(matchRepository.existsById(id)).thenReturn(false);
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->{
            matchService.joinMatch(id, "D");
        });
        assertThat(ex.getMessage(), equalTo("No existe ningun partido con el id: " + id));
    }

    @Test
    public void joinMatchWithInvalidTeamTest(){
        long id = 1L;
        User organizer =  new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Dobles",4)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, organizer,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(organizer);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->{
            matchService.joinMatch(id, "C");
        });
        assertThat(ex.getReason(), equalTo("El equipo debe ser A o B"));
        assertThat(ex.getStatusCode().toString(),equalTo("400 BAD_REQUEST"));
    }

    @Test 
    public void joinMatchUserAlreadyJoinedTest(){
        //GIVEN
        long id = 4L;
        User organizer =  new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Dobles",4)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, organizer,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(organizer);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.joinMatch(id,"B");
        });
        //THEN
        assertThat(ex.getReason(),equalTo("Ya se ha unido a este partido"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test 
    public void joinFullMatchTest(){
        //GIVEN
        long id = 4L;
        User user1 =  new User();
        User user2 =  new User();
        User user3 = new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Singles",2)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, user1,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(user1)));
        match.setTeam2Players(new HashSet<>(Set.of(user2)));
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(user3);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.joinMatch(id,"B");
        });
        assertThat(ex.getReason(),equalTo("El partido esta lleno"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test 
    public void leaveMatchTest(){
        long id = 4L;
        User user1 =  new User();
        user1.setId(1L);
        User user2 =  new User();
        user2.setId(2L);
        User user3 =  new User();
        user3.setId(3L);
        Match match = new Match(null, true, false, true,0, user1,5.00,null,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(user1,user2)));
        match.setTeam2Players(new HashSet<>(Set.of(user3)));
        Optional<Match> optionalMatch = Optional.of(match);
        
        when(matchRepository.findById(id)).thenReturn(optionalMatch);
        
        matchService.leaveMatch(id, user2);
        verify(matchRepository,times(1)).save(match);
        assertThat(match.getTeam1Players(),not(hasItem(user2)));    
    }

    @Test 
    public void replaceExistingMatchTest(){
        //GIVEN
        long id = 2L;
        Optional<Match> matchOptional = Optional.of(new Match(LocalDateTime.of(2025,5,12,11,0),true,true,true,0,new User(),9.99f,new Sport(), new Club()));
        Match updatedMatch = new Match(LocalDateTime.of(2025,5,12,12,30),true,true,true,0,new User(),10.49f,new Sport(), new Club());
        updatedMatch.setId(id);
        MatchDTO updatedMatchDTO = mapper.toDTO(updatedMatch);

        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(matchOptional);

        MatchDTO replacedMatchDTO = matchService.replaceMatch(id, updatedMatchDTO);

        //THEN
        assertThat(updatedMatchDTO, equalTo(replacedMatchDTO));
        verify(matchRepository,times(1)).save(any(Match.class));

    }

    @Test 
    public void replaceNonExistingMatchTest(){
        //GIVEN
        Random random = new Random();
        long id = 1 + random.nextInt(100);
        Optional<Match> emptyMatch = Optional.empty();
        Match updatedMatch =  new Match();
        MatchDTO updatedMatchDTO = mapper.toDTO(updatedMatch);

        //WHEN
        when(matchRepository.findById(id)).thenReturn(emptyMatch);

        //THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->  matchService.replaceMatch(id, updatedMatchDTO));
        assertThat(ex.getMessage(),equalTo("El partido con id " + id + " no existe."));
    }

    @Test
    public void addOrUpdateMatchResultToExistingMatchTest(){
        //GIVEN
        long id = 3L;
        User organizer =  new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Dobles",4)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, organizer,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(organizer,new User())));
        match.setTeam2Players(new HashSet<>(Set.of(new User(),new User())));
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);

        MatchResultDTO resultDTO = new MatchResultDTO("A","B",6,4,List.of(6,7),List.of(4,5));
        MatchResultDTO addedResultDTO = matchService.addOrUpdateMatchResult(id, resultDTO);

        //THEN
        assertThat(addedResultDTO.team1Name(), equalTo(resultDTO.team1Name()));
        assertThat(addedResultDTO.team2Name(), equalTo(resultDTO.team2Name()));
        verify(matchRepository,times(1)).save(any(Match.class));
    }

    @Test
    public void addOrUpdateMatchResultToIncompleteMatchTest(){
        //GIVEN
        long id = 3L;
        User organizer =  new User();
        Sport sport = new Sport("Tenis",List.of(new Mode("Dobles",4)),ScoringType.SETS);
        Match match = new Match(null, true, false, true,0, organizer,5.00, sport,null);
        match.setId(id);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>(Set.of(new User(),new User())));
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(id)).thenReturn(true);
        when(matchRepository.findById(id)).thenReturn(optionalMatch);

        MatchResultDTO resultDTO = new MatchResultDTO("A","B",6,4,List.of(6,7),List.of(4,5));

        //THEN
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.addOrUpdateMatchResult(id, resultDTO);
        });
        assertThat(ex.getReason(),equalTo("No se puede añadir el resultado a un partido incompleto"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

}
