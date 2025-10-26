package es.codeurjc.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{id}")
	public MatchDTO getMatch(@PathVariable long id) {
        return matchService.getMatch(id);
	}



    
}
