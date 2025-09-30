package es.codeurjc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;

@Service
public class UserService {
    @Autowired
	private UserRepository userRepository;

    public Optional<User> findById(long id) {
		return userRepository.findById(id);
	}

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean exist(long id) {
		return userRepository.existsById(id);
	}

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User with id " + id + " does not exist.");
        }
    }

    
}
