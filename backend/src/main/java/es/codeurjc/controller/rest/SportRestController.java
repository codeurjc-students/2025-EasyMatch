package es.codeurjc.controller.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.dto.SportDTO;
import es.codeurjc.service.SportService;


@RestController
@RequestMapping("/api/v1/sports")
@CrossOrigin(origins = "http://localhost:9876")
public class SportRestController {

    @Autowired
    private SportService sportService;
    
    @GetMapping("/")
	public Collection<SportDTO> getSports() {
        return sportService.getSports();
	}
}
