package es.codeurjc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.dto.MatchResultDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.Match;
import es.codeurjc.model.MatchResult;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.User;
import es.codeurjc.repository.MatchRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchService {

    public MatchService(MatchRepository matchRepository, MatchMapper mapper, UserService userService, UserMapper userMapper) {
        this.matchRepository = matchRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.userMapper = userMapper; 
    }

    @Autowired
	private MatchRepository matchRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchMapper mapper;

    @Autowired 
    private UserMapper userMapper;

    private MatchDTO toDTO (Match Match) {
        return mapper.toDTO(Match);
    }

    public Optional<Match> findById(long id) {
		return matchRepository.findById(id);
	}

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Page<Match> findAll(Pageable pageable) {
		return matchRepository.findAll(pageable);
	}

    @Transactional(readOnly = true)
    public MatchDTO getMatch(long id) {
        return toDTO(findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
    
    @Transactional(readOnly = true)
    public Page<MatchDTO> getMatches(Pageable pageable) {
        return findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MatchDTO> getFilteredMatches( Pageable pageable, String search, String sport, Boolean includeFriendlies, String timeRange) {
        if (includeFriendlies == null) {
            includeFriendlies = true;
        }
        return matchRepository.findFilteredMatches(pageable,
            search != null ? "%" + search.toLowerCase() + "%" : null, 
            sport != null ? sport.toLowerCase() : null, 
            includeFriendlies, 
            timeRange != null ? timeRange.toLowerCase() : null, true)
            .map(this::toDTO);
    }

    public boolean exist(long id) {
        return matchRepository.existsById(id);  
    }
    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public void delete(Long id) {
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            matchRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Match with id " + id + " does not exist.");
        }
    }

     public MatchDTO createMatch(MatchDTO matchDTO) {
        Match match = mapper.toDomain(matchDTO);
        match.setState(true);

        User loggedUser = userMapper.toDomain(userService.getLoggedUserDTO());
		match.setOrganizer(loggedUser);
        match.setTeam1Players(Set.of(loggedUser));
        
 		matchRepository.save(match);
 		return toDTO(match);
    }

     public MatchDTO joinMatch(long id, String team) {
        if (matchRepository.existsById(id)) {
			Match match = matchRepository.findById(id).orElseThrow();
			User loggedUser = userService.getLoggedUser();
            int playersPerGame = match.getSport().getModes().get(match.getModeSelected()).getPlayersPerGame();
            
            if (!team.equalsIgnoreCase("A") && !team.equalsIgnoreCase("B")) { 
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El equipo debe ser A o B");
            }
                
            if (!match.isFull()){
                
                if (team.equalsIgnoreCase("A")) {
                    if (match.getTeam1Players().size() >= playersPerGame / 2 )
                        throw new ResponseStatusException(HttpStatus.CONFLICT,"Equipo A lleno");

                    if (match.containsPlayer(loggedUser))
                        throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya se ha unido a este partido");
                    else
                        match.addPlayerToTeam1(loggedUser);
                }else{
                    if (match.getTeam2Players().size() >= playersPerGame / 2)
                        throw new ResponseStatusException(HttpStatus.CONFLICT,"Equipo B lleno");
                    if (match.containsPlayer(loggedUser))
                        throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya se ha unido a este partido");
                    else
                        match.addPlayerToTeam2(loggedUser);
                }
                if (match.isFull()) match.setState(false);
                matchRepository.save(match);
            }else{
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El partido esta lleno");
            }
			return toDTO(match);
 		} else {
 			throw new NoSuchElementException("No existe ningun partido con el id: " + id);
 		}
     }

    public void leaveMatch(long id, User user) {
        Match match = matchRepository.findById(id).orElseThrow();

        match.getTeam1Players().removeIf(p -> p.getId() == user.getId());
        match.getTeam2Players().removeIf(p -> p.getId() == user.getId());
        if (match.getTeam1Players().isEmpty() && match.getTeam2Players().isEmpty()){
            matchRepository.deleteById(id);
        }else{
            matchRepository.save(match);
        }
    }
    public MatchDTO replaceMatch(long id, MatchDTO updatedMatchDTO) {
        if (matchRepository.existsById(id)) {
            Match match = matchRepository.findById(id).orElseThrow();
            Match updatedMatch = mapper.toDomain(updatedMatchDTO);
            updatedMatch.setId(id);
            updatedMatch.setOrganizer(match.getOrganizer());
            updatedMatch.setTeam1Players(match.getTeam1Players());
            updatedMatch.setTeam2Players(match.getTeam2Players());
            updatedMatch.setState(match.getState());
            matchRepository.save(updatedMatch);
            return toDTO(updatedMatch);
 		} else {
 			throw new NoSuchElementException("El partido con id " + id + " no existe.");
 		}
    }

    public MatchResultDTO addOrUpdateMatchResult(long id, MatchResultDTO resultData) {
        if (matchRepository.existsById(id)) {
            Match match = matchRepository.findById(id).orElseThrow();
            if (!match.isFull()){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"No se puede añadir el resultado a un partido incompleto");
            }
            boolean isNewResult = !match.getResult().isCompleted();
            MatchResult result = new MatchResult();
            if (resultData.team1Name() != null && resultData.team2Name() != null) {
                result.setTeam1Name(resultData.team1Name());
                result.setTeam2Name(resultData.team2Name());
            } else {
                result.setTeam1Name("A");
                result.setTeam2Name("B");
            }
            if(match.getSport().getScoringType() == ScoringType.SCORE){ 
                result.setTeam1Score(resultData.team1Score());
                result.setTeam2Score(resultData.team2Score());
                result.setTeam1GamesPerSet(new ArrayList<>());
                result.setTeam2GamesPerSet(new ArrayList<>());
            }else if (match.getSport().getScoringType() == ScoringType.SETS){
                result.setTeam1Score(0);
                result.setTeam2Score(0);
                result.setTeam1GamesPerSet(resultData.team1GamesPerSet());
                result.setTeam2GamesPerSet(resultData.team2GamesPerSet());
            }
            match.setResult(result);
            try {
                match.validateResult(result);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
            if (isNewResult) {
                 match.getTeam1Players().forEach(user -> {
                    boolean won = match.didPlayerWin(user);
                    user.updateStats(won, false);
                    user.applyMatchResult(won, match.getDate());
                    userService.update(user);
                });
                match.getTeam2Players().forEach(user -> {
                    boolean won = match.didPlayerWin(user);
                    user.updateStats(won, false);
                    user.applyMatchResult(won, match.getDate());
                    userService.update(user);
                });
            }
            matchRepository.save(match);
            return resultData;
 		} else {
 			throw new NoSuchElementException("El partido con id " + id + " no existe.");
 		}
    }

    public MatchResultDTO getMatchResult(long id) {
        return getMatch(id).result() == null ? new MatchResultDTO(null, null, null, null, null, null) : getMatch(id).result();
    }

}
