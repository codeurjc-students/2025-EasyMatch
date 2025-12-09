package es.codeurjc.backend.integration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.User;
import es.codeurjc.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")

public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper mapper;

    @Test
    @Order(1)
    public void getUsersIntegrationTest() {
        int numUsers = userService.findAll().size();
        Page<UserDTO> pageUsers = userService.getUsers(Pageable.ofSize(numUsers));
        assertThat(pageUsers.getTotalElements(), equalTo(Integer.toUnsignedLong(numUsers)));
    }

    @Test
    @Order(2)
    public void getUserByIdIntegrationTest() {
        long id = 2L;
        UserDTO userDTO = userService.getUser(id);
        assertThat(userDTO.id(), equalTo(id));
        assertThat(userDTO.realname(), equalTo("Pedro Garcia"));
    }
    @Test
    @Order(3)
    public void deleteNonExistingUserIntegrationTest() {
        int numUsers = userService.findAll().size();
        long id = numUsers + 1;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {userService.delete(id);});
        assertThat(ex.getMessage(), equalTo("User with id " + id + " does not exist."));
        
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Order(4)
    public void testDeleteExistingUserIntegrationTest() {
        int numUsers = userService.findAll().size();
        long id = 4L;
        userService.delete(id);
        Collection<User> users = userService.findAll();
        assertThat(users.size(), equalTo(numUsers - 1));
        assertFalse(userService.exist(id));
    }

    @Test
    @Order(5)
    public void createUserIntegrationTest() throws IOException {
        int numUsers = userService.findAll().size();
        User newUser = new User();

        newUser.setRealname("New User");
        newUser.setUsername("new_user");
        newUser.setEmail("user@emeal.com");
        newUser.setPassword("user@emeal.com");
        newUser.setBirthDate(LocalDateTime.of(2002,6,5,0,0));
        newUser.setGender(true);

        UserDTO newUserDTO = mapper.toDTO(newUser); 
        UserDTO savedUser = userService.createUser(newUserDTO,false);
        Collection<User> users = userService.findAll();
        assertThat(users.size(), equalTo(numUsers + 1));
        assertThat(savedUser.id(), notNullValue());
        int id = users.size() + 1;
        assertThat(savedUser.id(), equalTo(Long.parseLong(String.valueOf(id))));
        assertThat(savedUser.realname(), equalTo("New User"));
    }


}
