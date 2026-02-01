package es.codeurjc.controller.rest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchResultDTO;
import es.codeurjc.model.User;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.UserService;

@RestController
@RequestMapping("/api/v1/matches")
@CrossOrigin(origins = "http://localhost:9876")
public class MatchRestController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
	public Page<MatchDTO> getMatches(Pageable pageable) {
        return matchService.getMatches(pageable);
	}

    @GetMapping
    public Page<MatchDTO> getFilteredMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) Boolean includeFriendlies,
            @RequestParam(required = false) String timeRange
            
    ) {
        
        Pageable pageable = PageRequest.of(page, size);

        return matchService.getFilteredMatches(pageable,search, sport, includeFriendlies, timeRange);
    }

    @GetMapping("/{id}")
	public MatchDTO getMatch(@PathVariable long id) {
        return matchService.getMatch(id);
	}

    @PostMapping
    public ResponseEntity<MatchDTO> creatematch(@RequestBody MatchDTO matchDTO) {
        matchDTO = matchService.createMatch(matchDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(matchDTO.id()).toUri();
        return ResponseEntity.created(location).body(matchDTO);
    }

    @PutMapping("/{id}/users/me")
    public ResponseEntity<Map<String, String>> joinMatch(@PathVariable long id, @RequestBody JoinMatchRequest request) {
        matchService.joinMatch(id, request.getTeam());
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Player added to team " + request.getTeam());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/users/me")
    public ResponseEntity<Map<String, String> > leaveMatch(@PathVariable long id) {
        User user = userService.getLoggedUser();
        matchService.leaveMatch(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message","Player removed from match");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public MatchDTO deleteMatch(@PathVariable long id) {
        if (!userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo un administrador puede eliminar un partido");
        }
        MatchDTO deletedMatch = matchService.getMatch(id);
        matchService.delete(id);
        return deletedMatch;
    }

    @PutMapping("/{id}")
    public MatchDTO replaceMatch(@PathVariable long id, @RequestBody MatchDTO updatedMatchDTO) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un administrador puede editar un partido");
        }
        return matchService.replaceMatch(id, updatedMatchDTO);
    }

    @GetMapping("{id}/result")
    public MatchResultDTO getMatchResult(@PathVariable long id) {
        return matchService.getMatchResult(id);
    }

    @PutMapping("/{id}/result")
    public MatchResultDTO addOrUpdateMatchResult(@PathVariable long id, @RequestBody MatchResultDTO resultData) {
        if(matchService.getMatch(id).organizer().id() != userService.getLoggedUserDTO().id()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el organizador del partido puede añadir o editar el resultado");
        }
        return matchService.addOrUpdateMatchResult(id, resultData);
    }

}