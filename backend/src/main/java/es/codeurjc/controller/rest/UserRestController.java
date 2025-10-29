package es.codeurjc.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:9876")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper mapper;


    @GetMapping("/")
	public List<UserDTO> getUsers() {
		return mapper.toDTOs(userService.findAll());
	}

    
	@GetMapping("/me")
	public UserDTO me() {
		return userService.getLoggedUserDTO();
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

    
}
