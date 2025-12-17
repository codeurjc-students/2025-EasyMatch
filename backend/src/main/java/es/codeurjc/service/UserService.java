package es.codeurjc.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.Match;
import es.codeurjc.model.PlayerStats;
import es.codeurjc.model.User;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MatchMapper matchMapper;

    

    public Optional<User> findById(long id) {
		return userRepository.findById(id);
	}

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(Pageable pageable) {
        return findAll(pageable).map(this::toDTO);
    }


	public UserDTO getUser(long id) {
		return toDTO(userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado")));
	}

    public boolean exist(long id) {
		return userRepository.existsById(id);
	}

    public User update(User user) {
        User managed = userRepository.findById(user.getId()).orElseThrow();
        user.setImage(managed.getImage());
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
        }else {
            throw new IllegalArgumentException("User with id " + id + " does not exist.");
        }
            
    }
    

    public UserDTO getLoggedUserDTO() {

        return toDTO(this.getLoggedUser());

	}

    public User getLoggedUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;

        if (principal instanceof UserDetails) {

            email = ((UserDetails)principal).getUsername();

        } else {

            email = principal.toString();

        }
        return userRepository.findByEmail(email).orElseThrow();
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

    public UserDTO createUser(UserDTO userDTO, boolean isAdmin) throws IOException {
        User user = mapper.toDomain(userDTO);
        if (!isAdmin) {
            user.setLevel(0.0f);
        }
        user.setStats(new PlayerStats(0,0,0,0));
        user.setRoles(List.of("USER"));
        setUserImage(user,"/images/default-avatar.jpg");
        this.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO (User user) {
        return mapper.toDTO(user);
    }


    private void setUserImage(User user, String classpathResource) throws IOException {
         try {
            Resource image = new ClassPathResource(classpathResource);
		    user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error at processing the image");
        }
	
	}

    public List<MatchDTO> getMatchesByUser(Long id) {
        if (userRepository.existsById(id)){
            List<Match> myMatches = matchRepository.findByTeam1PlayersIdOrTeam2PlayersId(id, id);
            return myMatches.stream()
                .map(matchMapper::toDTO)
                .toList();
        }
        return List.of();
        
    }

    public UserDTO replaceUser(long id, UserDTO updatedUserDTO) {
        if (userRepository.existsById(id)) {
			User user = userRepository.findById(id).orElseThrow();
            User updatedUser = mapper.toDomain(updatedUserDTO);
            updatedUser.setId(id);
            updatedUser.setImage(user.getImage());
            if(!updatedUser.getPassword().isEmpty() && !updatedUser.getPassword().equals(user.getPassword())){
                String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
                updatedUser.setPassword(encodedPassword);
            }else if(updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()){
                updatedUser.setPassword(user.getPassword());
            }
            updatedUser.setRoles(user.getRoles());
            updatedUser.setStats(user.getStats());
            userRepository.save(updatedUser);
            return toDTO(updatedUser);
 		} else {
 			throw new NoSuchElementException("User with id " + id + " does not exist.");
 		}
    }

    public void replaceUserImage(long id, InputStream inputStream, long size) {
		User user = userRepository.findById(id).orElseThrow();

		if(user.getImage() == null){
			throw new NoSuchElementException();
		}

		user.setImage(BlobProxy.generateProxy(inputStream, size));

		userRepository.save(user);
	}
}
