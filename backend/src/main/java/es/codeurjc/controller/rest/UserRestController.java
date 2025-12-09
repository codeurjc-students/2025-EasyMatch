package es.codeurjc.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.UserDTO;
import es.codeurjc.model.User;
import es.codeurjc.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:9876")
public class UserRestController {

    @Autowired
    private UserService userService;


    @GetMapping("/")
	public Page<UserDTO> getUsers(Pageable pageable) {
		return userService.getUsers(pageable);
	}

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) throws IOException {
        boolean isAdmin = false;
        try {
            User logged = userService.getLoggedUser();
            isAdmin = logged.getRoles().contains("ADMIN");
        } catch (NoSuchElementException e ) {
            isAdmin = false;
        }
        userDTO = userService.createUser(userDTO, isAdmin);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();

        return ResponseEntity.created(location).body(userDTO);
    }
    
	@GetMapping("/me")
	public ResponseEntity<UserDTO> me() {
		return ResponseEntity.ok(userService.getLoggedUserDTO());
	}

    @GetMapping("/{id}")
	public UserDTO getUser(@PathVariable long id) {
        return userService.getUser(id);
	}

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        if(userService.getLoggedUserDTO().id() == id){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        
        userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getUserImage(@PathVariable long id) throws SQLException, IOException {
        Resource productImage = userService.getUserImage(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(productImage);
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable long id) {
        if((id != userService.getLoggedUser().getId() || id == 1) && !userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin o el mismo usuario puede borrar su cuenta");
        }
        UserDTO deletedUser = userService.getUser(id);
        userService.delete(id);
        return deletedUser;
    }

    @PutMapping("/{id}")
    public UserDTO replaceUser(@PathVariable long id, @RequestBody UserDTO updatedUserDTO) {
        
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") || userService.getLoggedUser().getId() != id){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userService.replaceUser(id, updatedUserDTO);
    }

    @GetMapping("/{id}/matches/")
    public List<MatchDTO> getUserMatches(@PathVariable Long id) {
        List<MatchDTO> matches = userService.getMatchesByUser(id);
        return matches;
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> putUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        if(userService.getLoggedUser() != userService.findById(id).get() && !userService.getLoggedUser().getRoles().contains("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userService.replaceUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        return ResponseEntity
                .noContent()
                .build();
        
    }
    
}
