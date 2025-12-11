package es.codeurjc.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ClubDTO;
import es.codeurjc.dto.ClubMapper;
import es.codeurjc.service.ClubService;
import es.codeurjc.service.UserService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/clubs")
@CrossOrigin(origins = "http://localhost:9876")
public class ClubRestController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubMapper mapper;

    @GetMapping("/")
	public Page<ClubDTO> getClubs(Pageable pageable) {
		return clubService.getClubs(pageable);
	}

    @GetMapping
    public Page<ClubDTO> getFilteredClubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String city
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return clubService.getFilteredClubs(pageable, search, sport, city)
                .map(mapper::toDTO);
    }

    @GetMapping("/{id}")
	public ClubDTO getClub(@PathVariable long id) {
        return clubService.getClub(id);
	}

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createClubImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        clubService.createClubImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getClubImage(@PathVariable long id) throws SQLException, IOException {
        Resource productImage = clubService.getClubImage(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(productImage);
    }

    @PostMapping("/")
    public ResponseEntity<ClubDTO> createClub(@RequestBody ClubDTO clubDTO) throws IOException {
        clubDTO = clubService.createClub(clubDTO);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(clubDTO.id()).toUri();

        return ResponseEntity.created(location).body(clubDTO);
    }

    @DeleteMapping("/{id}")
    public ClubDTO deleteClub(@PathVariable long id) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin puede eliminar un club");
        }
        ClubDTO deletedClub = clubService.getClub(id);
        clubService.delete(id);
        return deletedClub;
    }

    @PutMapping("/{id}")
    public ClubDTO replaceClub(@PathVariable long id, @RequestBody ClubDTO updatedClubDTO) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin puede editar un club");
        }
        return clubService.replaceClub(id, updatedClubDTO);
    }
    
    @PutMapping("/{id}/image")
    public ResponseEntity<Object> putclubImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
         if (!userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        clubService.replaceClubImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity
                .noContent()
                .build();
        
    }
}
