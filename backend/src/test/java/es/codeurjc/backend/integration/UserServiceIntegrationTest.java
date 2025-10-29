package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;



import es.codeurjc.dto.UserDTO;
import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;
import es.codeurjc.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Random;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")

public class UserServiceIntegrationTest {
     @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    public void getUsersIntegrationTest() {
        int numUsers = 4;
        Collection<UserDTO> users = userService.getUsers();
        assertThat(users.size(), equalTo(numUsers));
    }

    @Test
    @Order(2)
    public void getUserByIdIntegrationTest() {
        long id = 1;
        UserDTO userDTO = userService.getUser(id);
        assertThat(userDTO.id(), equalTo(id));
        assertThat(userDTO.realname(), equalTo("Pedro Garcia"));
    }
    @Test
    @Order(3)
    public void deleteNonExistingUserIntegrationTest() {
        Random random = new Random();
        int numUsers = userService.getUsers().size();
        long id = numUsers + random.nextLong(100);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {userService.deleteById(id);});
        assertThat(ex.getMessage(), equalTo("User with id " + id + " does not exist."));
        
    }

    @Test
    @Order(4)
    public void testDeleteExistingUserIntegrationTest() {
        Random random = new Random();
        int numUsers = 4;
        long id = 1 + random.nextLong(numUsers);
        userService.deleteById(id);
        Collection<UserDTO> users = userService.getUsers();
        assertThat(users.size(), equalTo(numUsers - 1));
        assertFalse(userService.exist(id));
    }

    @Test
    @Order(5)
    public void saveUserIntegrationTest() {
        int numUsers = 3;
        User newUser = new User(
            "New User",
            "new_user",
            "user@emeal.com",               
            "newpassword",                  
            LocalDateTime.now(),    
            true, 
            "I am a new user",            
            5.5f            
        );

        User savedUser = userService.save(newUser);
        Collection<UserDTO> users = userService.getUsers();
        assertThat(users.size(), equalTo(numUsers + 1));
        assertThat(savedUser.getRealname(), equalTo("New User"));
    }


}
