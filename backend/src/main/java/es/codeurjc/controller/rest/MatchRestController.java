package es.codeurjc.controller.rest;

import java.net.URI;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.service.MatchService;

@RestController
@RequestMapping("/api/v1/matches")
@CrossOrigin(origins = "http://localhost:9876")
public class MatchRestController {

    @Autowired
    private MatchService matchService;

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



}
