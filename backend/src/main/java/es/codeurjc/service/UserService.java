package es.codeurjc.service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;

@Service
public class UserService {
    public UserService(UserRepository userRepository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }
    @Autowired
	private UserRepository userRepository;

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

    public boolean exist(long id) {
		return userRepository.existsById(id);
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


    public void deleteById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
        } else {
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

    private UserDTO toDTO (User user) {
        return mapper.toDTO(user);
    }


    private List<UserDTO> toDTOs(Collection<User> users){
        return mapper.toDTOs(users);
    }

    public Collection<UserDTO> getUsers() {
		return toDTOs(userRepository.findAll());
	}

	public UserDTO getUser(long id) {
		return toDTO(userRepository.findById(id).orElseThrow());
	}
    
}
