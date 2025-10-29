package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;
import es.codeurjc.service.UserService;

@Tag("unit")
@ActiveProfiles("test")
public class UserServiceUnitaryTest {

    private UserRepository userRepository;
    private UserService userService;
    private UserMapper mapper;
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        mapper = Mappers.getMapper(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, mapper,passwordEncoder);
        
    }

    @Test
    public void getUsersUnitarytest(){
        //GIVEN
        User user1 = new User("Carlos López", "carlos_10", "carlos@example.com", "password123", LocalDateTime.of(1995, 3, 12, 0, 0), true, "Amante del fútbol y los torneos locales.", 4.5f, "USER");
        User user2 = new User("Laura Gómez", "laura_admin", "laura@example.com", "adminPass!", LocalDateTime.of(1988, 7, 23, 0, 0), false, "Administradora de la plataforma.", 3.75f, "ADMIN", "USER");
        User user3 = new User("Pedro Martín", "pedro_m", "pedro@example.com", "newuserpass", LocalDateTime.of(2000, 1, 15, 0, 0), true, "Nuevo en la aplicación, aprendiendo.", 1.0f, "USER");
        List<User> allUsers = List.of(user1,user2,user3);

        //WHEN
        when(userRepository.findAll()).thenReturn(allUsers);
        Collection<UserDTO> result = userService.getUsers();
        Collection<UserDTO> expected = mapper.toDTOs(allUsers);

        //THEN
        assertThat(result.size(), equalTo(expected.size()));

    }

    @Test
    public void getExistingUserByIdUnitaryTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        User user = new User("Carlos López", "carlos_10", "carlos@example.com", "password123", LocalDateTime.of(1995, 3, 12, 0, 0), true, "Amante del fútbol y los torneos locales.", 4.5f, "USER");
        user.setId(id);
        Optional<User> optionalUser = Optional.of(user);

        //WHEN
        when(userRepository.findById(id)).thenReturn(optionalUser);
        UserDTO result = userService.getUser(id);
        UserDTO expected = mapper.toDTO(user);

        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void getNonExistingUserByIdUnitaryTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> {
            userService.getUser(id);
        });
        //THEN
        assertThat(ex.getMessage(), equalTo("No value present"));
    }

    @Test
    public void deleteExistingUserUnitaryTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        User user = new User("Pedro Martín", "pedro_m", "pedro@example.com", "newuserpass", LocalDateTime.of(2000, 1, 15, 0, 0), true, "Nuevo en la aplicación, aprendiendo.", 1.0f, "USER");
        user.setId(id);
        Optional<User> optionalUser = Optional.of(user);

        //WHEN
        when(userRepository.findById(id)).thenReturn(optionalUser);
        userService.deleteById(id);

        //THEN
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteNonExistingUserUnitaryTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteById(id);
        });

        //GIVEN
        assertThat(ex.getMessage(), equalTo("User with id " + id + " does not exist."));
    }

    @Test
    public void saveUserUnitaryTest(){
        //GIVEN 
        User originalUser = new User("Laura Gómez", "laura_admin", "laura@example.com", "adminPass!", LocalDateTime.of(1988, 7, 23, 0, 0), false, "Administradora de la plataforma.", 3.75f, "ADMIN", "USER");

        //WHEN
        when(userRepository.save(originalUser)).thenReturn(originalUser);
        when(passwordEncoder.encode("adminPass!")).thenReturn("encoded_pass");
        User savedUser = userService.save(originalUser);

        //GIVEN
        assertThat(savedUser, equalTo(originalUser));
        assertThat(savedUser.getPassword(), equalTo("encoded_pass"));
    }

}
