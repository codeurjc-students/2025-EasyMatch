package es.codeurjc.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.Match;
import es.codeurjc.model.PlayerStats;
import es.codeurjc.model.User;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    public UserService(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder, MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.matchRepository = matchRepository;
    }
    @Autowired
	private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper mapper;

    

    public Optional<User> findById(long id) {
		return userRepository.findById(id);
	}

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Collection<UserDTO> getUsers() {
		return toDTOs(userRepository.findAll());
	}

	public UserDTO getUser(long id) {
		return toDTO(userRepository.findById(id).orElseThrow());
	}

    public boolean exist(long id) {
		return userRepository.existsById(id);
	}

    public User update(User user){
        return userRepository.save(user);
    }
    
    public User save(User user){
		String password = user.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
		if (user.getUsername().equals("admin")) {
			user.setRoles(List.of("USER", "ADMIN"));
		} else {
			user.setRoles(List.of("USER"));
		}
		User savedUser = userRepository.save(user);
        userRepository.flush();
		return savedUser;
	}


    @Transactional 
    public void delete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getId() > 0) {
                for (Match match : user.getMatchesAsTeam1Player()) {
                    match.getTeam1Players().remove(user);
                }
                for (Match match : user.getMatchesAsTeam2Player()) {
                    match.getTeam2Players().remove(user);
                }

                for (Match match : user.getOrganizedMatches()) {
                    match.setOrganizer(null);
                    matchRepository.delete(match);
                }
                userRepository.deleteById(id);
            }
        }else {
            throw new IllegalArgumentException("User with id " + id + " does not exist.");
        }
            
    }
    

    public UserDTO getLoggedUserDTO() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;

        if (principal instanceof UserDetails) {

            email = ((UserDetails)principal).getUsername();

        } else {

            email = principal.toString();

        }

        return toDTO(userRepository.findByEmail(email).orElseThrow());

	}

    public void createUserImage(long id, InputStream inputStream, long size) { 
		User user = userRepository.findById(id).orElseThrow();
		user.setImage(BlobProxy.generateProxy(inputStream, size)); 
		userRepository.save(user); 
	}

    public Resource getUserImage(long id) throws SQLException{
		User user = userRepository.findById(id).orElseThrow();

		if (user.getImage() != null) {
			return new InputStreamResource(user.getImage().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
    }

    public UserDTO createUser(UserDTO userDTO) throws IOException {
        User user = mapper.toDomain(userDTO);
        user.setLevel(0.0f);
        user.setStats(new PlayerStats(0,0,0,0));
        setUserImage(user,"/images/default-avatar.jpg");
        this.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO (User user) {
        return mapper.toDTO(user);
    }


    private List<UserDTO> toDTOs(Collection<User> users){
        return mapper.toDTOs(users);
    }

    private void setUserImage(User user, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}
}
