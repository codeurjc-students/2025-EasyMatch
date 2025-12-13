package es.codeurjc.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.SportDTO;
import es.codeurjc.service.SportService;
import es.codeurjc.service.UserService;


@RestController
@RequestMapping("/api/v1/sports")
@CrossOrigin(origins = "http://localhost:9876")
public class SportRestController {

    @Autowired
    private SportService sportService;

    @Autowired
    private UserService userService;
    
    @GetMapping("/")
	public Collection<SportDTO> getSports() {
        return sportService.getSports();
	}

    @GetMapping("/{id}")
    public SportDTO getSport(@PathVariable Long id) {
        return sportService.getSport(id);
    }

    @DeleteMapping("/{id}")
    public SportDTO deleteSport(@PathVariable Long id) {
        if (!userService.getLoggedUser().getRoles().contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el admin puede eliminar un deporte.");
        }

        SportDTO deletedSport = sportService.getSport(id);
        sportService.delete(id);
        return deletedSport;
    }

    @PostMapping("/")
    public ResponseEntity<SportDTO> createSport(@RequestBody SportDTO sportDTO) throws IOException {
        if (!userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN); 
        }
        sportDTO = sportService.createSport(sportDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(sportDTO.id()).toUri();

        return ResponseEntity.created(location).body(sportDTO);
    }

    @PutMapping("/{id}")
    public SportDTO replaceSport(@PathVariable long id, @RequestBody SportDTO updatedSportDTO) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return sportService.replaceSport(id, updatedSportDTO);
    }

}
