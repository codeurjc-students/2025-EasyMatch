package es.codeurjc.backend.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.dto.MatchResultDTO;
import es.codeurjc.dto.PlayerStatsDTO;
import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.dto.UserSportProfileDTO;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.SportService;
import es.codeurjc.service.UserService;
import es.codeurjc.service.UserSportProfileService;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.Club;
import es.codeurjc.model.Match;
import es.codeurjc.model.MatchResult;
import es.codeurjc.model.Mode;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.User;
import es.codeurjc.model.UserSportProfile;
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
    private ChatMessageService chatMessageService;
    private ChatMessageMapper chatMessageMapper;
    private UserSportProfileService profileService;
    private SimpMessagingTemplate messagingTemplate;
    private SportService sportService;
    

    private Sport defaultSport;
    private Mode defaultMode;

    private User defaultUser;
    private User organizer;
    private User secondPlayer;
    private User thirdPlayer;

    private UserDTO defaultUserDTO;

    private Long defaultMatchId;
    private Long defaultPlayerId;
    private Long defaultSportId;
    
    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        userMapper = mock(UserMapper.class);
        matchRepository = mock(MatchRepository.class);
        mapper = Mappers.getMapper(MatchMapper.class);
        chatMessageService = mock(ChatMessageService.class);
        chatMessageMapper = Mappers.getMapper(ChatMessageMapper.class);
        profileService = mock(UserSportProfileService.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        sportService = mock(SportService.class);
        matchService = new MatchService(matchRepository, mapper, userService,userMapper, chatMessageService, chatMessageMapper, profileService, messagingTemplate, sportService);

        defaultMatchId = 1L;
        defaultPlayerId = 2L;
        defaultSportId = 3L;

        defaultMode = new Mode("Dobles", 4);

        defaultSport = new Sport(
                "Tenis",
                List.of(defaultMode),
                ScoringType.SETS
        );

        defaultSport.setId(defaultSportId);

        organizer = new User();
        organizer.setId(1L);

        defaultUser = new User();
        defaultUser.setId(defaultPlayerId);

        secondPlayer = new User();
        secondPlayer.setId(3L);

        thirdPlayer = new User();
        thirdPlayer.setId(4L);

        defaultUserDTO = new UserDTO(
                defaultPlayerId,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null
        );
    }

    private Match createBaseMatch(User organizer) {
        Match match = new Match(
                null,
                true,
                false,
                true,
                0,
                120,
                organizer,
                5.00,
                defaultSport,
                null
        );
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        return match;
    }

    @Test
    public void getMatchesShouldReturnCorrectPageOfMatchDTOs(){   

        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);

        Match match1 = new Match(null, false, true, false,0, 120, new User(),3.50, new Sport(),null);
        Match match2 = new Match(null, true, true, false,0, 120, new User(),2.75, new Sport(),null);
        Match match3 = new Match(null, false, false, true,0, 120, new User(),9.95, new Sport(),null);
        Match match4 = new Match(null, true, false, true,0, 120, new User(),2.50, new Sport(),null);

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
    public void getMatchByIdShouldReturnCorrectMatch(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        MatchDTO result = matchService.getMatch(defaultMatchId);
        MatchDTO expected = mapper.toDTO(match);
        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void deleteExistingMatchShouldSucceed(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        matchService.delete(defaultMatchId);
        //THEN
        verify(matchRepository,times(1)).deleteById(defaultMatchId);
    }

    @Test
    public void deleteNonExistingMatchShouldThrowException404(){
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
    public void createMatchShouldSucceed(){
        //GIVEN

        UserSportProfile profile = new UserSportProfile(defaultUser, new Sport(), 4.5f);
        defaultUser.setSportProfiles(new ArrayList<>(List.of(profile)));

        Optional<Sport> optionalSport = Optional.of(defaultSport);
          
        Match match = createBaseMatch(organizer);
        ChatMessage chat = ChatMessage.builder()
            .match(match)
            .sender(defaultUser)
            .content("Chat del partido")
            .timestamp(LocalDateTime.now())
            .build();
        MatchDTO matchDTO = mapper.toDTO(match);
        //WHEN
        when(userService.getLoggedUser()).thenReturn(defaultUser);
        when(sportService.findById(defaultSportId)).thenReturn(optionalSport);
        when(matchRepository.save(match)).thenReturn(match);
        when(chatMessageService.save(chat)).thenReturn(chat);
        when(userMapper.toDTO(defaultUser)).thenReturn(defaultUserDTO);
        MatchDTO createdMatch = matchService.createMatch(matchDTO);
        //THEN
        assertThat(createdMatch.id(), equalTo(matchDTO.id()));
        assertThat(createdMatch.organizer().id(), equalTo(defaultUser.getId()));
        assertThat(createdMatch.team1Players(), contains(defaultUserDTO));
        assertThat(createdMatch.state(), is(true));
    }

    @Test
    public void joinExistingMatchShouldSucceed(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(defaultUser);
        //THEN
        matchService.joinMatch(defaultMatchId,"A");
    }

    @Test
    public void joinNonExistingMatchShouldThrowException(){
        when(matchRepository.existsById(defaultMatchId)).thenReturn(false);
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->{
            matchService.joinMatch(defaultMatchId, "D");
        });
        assertThat(ex.getMessage(), equalTo("No existe ningun partido con el id: " + defaultMatchId));
    }

    @Test
    public void joinMatchWithInvalidTeamShouldThrowException400(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(organizer);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->{
            matchService.joinMatch(defaultMatchId, "C");
        });

        //THEN
        assertThat(ex.getReason(), equalTo("El equipo debe ser A o B"));
        assertThat(ex.getStatusCode().toString(),equalTo("400 BAD_REQUEST"));
    }

    @Test 
    public void joinMatchUserAlreadyJoinedShouldThrowException409(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(organizer);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.joinMatch(defaultMatchId,"B");
        });
        //THEN
        assertThat(ex.getReason(),equalTo("Ya se ha unido a este partido"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test 
    public void joinFullMatchShouldThrowException409(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer, new User())));
        match.setTeam2Players(new HashSet<>(Set.of(secondPlayer, new User())));
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getLoggedUser()).thenReturn(thirdPlayer);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.joinMatch(defaultMatchId,"B");
        });
        //THEN
        assertThat(ex.getReason(),equalTo("El partido esta lleno"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test 
    public void leaveMatchShouldSucceed(){
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer, secondPlayer)));
        match.setTeam2Players(new HashSet<>(Set.of(thirdPlayer)));
        Optional<Match> optionalMatch = Optional.of(match);
        
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        
        matchService.leaveMatch(defaultMatchId, secondPlayer);
        verify(matchRepository,times(1)).save(match);
        assertThat(match.getTeam1Players(),not(hasItem(secondPlayer)));    
    }

    @Test 
    public void replaceExistingMatchShouldSucceed() {
        // GIVEN
        Match existingMatch = createBaseMatch(organizer);
        Optional<Match> matchOptional = Optional.of(existingMatch);

        Match updatedMatch = createBaseMatch(organizer);
        updatedMatch.setDate(LocalDateTime.of(2025, 5, 12, 12, 30));
        updatedMatch.setIsPrivate(true);
        updatedMatch.setType(true);
        updatedMatch.setPrice(10.49f);
        updatedMatch.setClub(new Club());

        MatchDTO updatedMatchDTO = mapper.toDTO(updatedMatch);

        // WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(matchOptional);

        MatchDTO replacedMatchDTO =
                matchService.replaceMatch(defaultMatchId, updatedMatchDTO);

        // THEN
        assertThat(replacedMatchDTO, equalTo(updatedMatchDTO));
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test 
    public void replaceNonExistingMatchShouldThrowException404(){
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
    public void addMatchResultToExistingMatchShouldSucceed() {
        // GIVEN
        defaultUser.setSportProfiles(new ArrayList<>(List.of(new UserSportProfile(defaultUser, defaultSport, 5.0f))));
        organizer.setSportProfiles(new ArrayList<>(List.of(new UserSportProfile(organizer, defaultSport, 5.0f))));
        secondPlayer.setSportProfiles(new ArrayList<>(List.of(new UserSportProfile(secondPlayer, defaultSport, 5.0f))));
        thirdPlayer.setSportProfiles(new ArrayList<>(List.of(new UserSportProfile(thirdPlayer, defaultSport, 5.0f))));
        

        Match match = createBaseMatch(organizer);

        match.setTeam1Players(new HashSet<>(Set.of(organizer, secondPlayer)));
        match.setTeam2Players(new HashSet<>(Set.of(thirdPlayer, defaultUser)));

        MatchResult existingResult = new MatchResult();
        match.setResult(existingResult);

        Optional<Match> optionalMatch = Optional.of(match);

        // WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(profileService.save(any(UserSportProfile.class)))
        .thenReturn(new UserSportProfileDTO(
            1L,
            "Tenis",
            5.0f,
            new PlayerStatsDTO(0, 0, 0, 0, 0)
        ));

        MatchResultDTO resultDTO = new MatchResultDTO(
            "A", "B",
            6, 4,
            List.of(6, 7),
            List.of(4, 5)
        );

        MatchResultDTO addedResultDTO = matchService.addMatchResult(defaultMatchId, resultDTO);

        // THEN
        assertThat(addedResultDTO.team1Name(), equalTo(resultDTO.team1Name()));
        assertThat(addedResultDTO.team2Name(), equalTo(resultDTO.team2Name()));
        assertThat(addedResultDTO.team1GamesPerSet(), equalTo(resultDTO.team1GamesPerSet()));
        assertThat(addedResultDTO.team2GamesPerSet(), equalTo(resultDTO.team2GamesPerSet()));

        int totalPlayers = match.getTeam1Players().size() + match.getTeam2Players().size();

        verify(userService, times(totalPlayers)).update(any(User.class));
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    public void addMatchResultToIncompleteMatchShouldThrowException409(){
        //GIVEN
        Match match = createBaseMatch(organizer);
        match.setTeam1Players(new HashSet<>(Set.of(organizer)));
        match.setTeam2Players(new HashSet<>(Set.of(secondPlayer,thirdPlayer)));
        match.setResult(new MatchResult());
        Optional<Match> optionalMatch = Optional.of(match);
        //WHEN
        when(matchRepository.existsById(defaultMatchId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
       

        MatchResultDTO resultDTO = new MatchResultDTO("A","B",6,4,List.of(6,7),List.of(4,5));

        //THEN
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.addMatchResult(defaultMatchId, resultDTO);
        });
        assertThat(ex.getReason(),equalTo("No se puede añadir el resultado a un partido incompleto"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test
    public void addPlayerToIncompleteTeamShouldSucceed(){
        //GIVEN

        UserDTO dto = userMapper.toDTO(defaultUser);
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>());
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(dto);

        matchService.addPlayerToTeam1(defaultMatchId, defaultPlayerId);

        //THEN
        assertThat(match.getTeam1Players().size(), equalTo(1));
        verify(matchRepository,times(1)).save(match);
    }

    @Test
    public void addPlayerToFullTeamShouldThrowException409(){
        //GIVEN

        UserDTO playerDTO = userMapper.toDTO(defaultUser);
        Match match = createBaseMatch(organizer);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(organizer,defaultUser)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(playerDTO);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.addPlayerToTeam1(defaultMatchId, defaultPlayerId);
        });

        //THEN
        assertThat(ex.getReason(),equalTo("Equipo 1 lleno"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));
    }

    @Test
    public void addPlayerAlreadyInMatchToTeam1ShouldThrowException409() {
        // GIVEN
        Match match = createBaseMatch(defaultUser);

        match.setTeam1Players(new HashSet<>(Set.of(defaultUser)));
        match.setTeam2Players(new HashSet<>());

        when(matchRepository.findById(defaultMatchId)).thenReturn(Optional.of(match));
        when(userService.getUser(defaultPlayerId)).thenReturn(defaultUserDTO);
        when(userMapper.toDomain(defaultUserDTO)).thenReturn(defaultUser);

        // WHEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> matchService.addPlayerToTeam1(defaultMatchId, defaultPlayerId)
        );

        // THEN
        assertThat(ex.getReason(), equalTo("Ya se ha unido a este partido"));
        assertThat(ex.getStatusCode(), equalTo(HttpStatus.CONFLICT));

        verify(matchRepository, never()).save(any());
    }

    @Test 
    public void removeOnlyPlayerFromTeamShouldSucceed(){
        //GIVEN

        Match match = createBaseMatch(defaultUser);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(defaultUser)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(defaultUserDTO);

        matchService.removePlayerFromTeam1(defaultMatchId, defaultPlayerId);

        //THEN
        assertThat(match.getTeam1Players().size(), equalTo(0));
        verify(matchRepository,times(1)).deleteById(defaultMatchId);
    }


    @Test
    public void removePlayerFromFullTeamShouldSucceed(){
        //GIVEN
        Match match = createBaseMatch(defaultUser);
        match.addPlayerToTeam1(secondPlayer);
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(defaultUserDTO);

        matchService.removePlayerFromTeam1(defaultMatchId, defaultPlayerId);

        //THEN
        assertThat(match.getTeam1Players().size(), equalTo(1));
        verify(matchRepository,times(1)).save(match);
    }

    @Test
    public void removePlayerFromEmptyTeamShouldThrowException409(){
        //GIVEN
        Match match = createBaseMatch(defaultUser);
        match.setTeam1Players(new HashSet<>());
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(defaultUserDTO);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            matchService.removePlayerFromTeam1(defaultMatchId, defaultPlayerId);
        });

        //THEN
        assertThat(ex.getReason(),equalTo("El equipo 1 no tiene jugadores"));
        assertThat(ex.getStatusCode().toString(),equalTo("409 CONFLICT"));

        verify(matchRepository, never()).save(any());
    }

    @Test
    public void removeOrganizerFromTeamShouldSucceed(){
        //GIVEN
        Match match =createBaseMatch(defaultUser);
        match.setId(defaultMatchId);
        match.setTeam1Players(new HashSet<>(Set.of(defaultUser,secondPlayer)));
        match.setTeam2Players(new HashSet<>());
        Optional<Match> optionalMatch = Optional.of(match);

        //WHEN
        when(matchRepository.findById(defaultMatchId)).thenReturn(optionalMatch);
        when(userService.getUser(defaultPlayerId)).thenReturn(defaultUserDTO);

        matchService.removePlayerFromTeam1(defaultMatchId, defaultPlayerId);

        //THEN
        assertThat(match.getTeam1Players().size(), equalTo(1));
        assertThat(match.getOrganizer(), equalTo(secondPlayer));
    }

}
