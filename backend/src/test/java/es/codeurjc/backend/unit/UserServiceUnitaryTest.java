package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.UserService;

@Tag("unit")
@ActiveProfiles("test")
public class UserServiceUnitaryTest {

    private UserRepository userRepository;
    private MatchRepository matchRepository;
    private UserService userService;
    private UserMapper mapper;
    private PasswordEncoder passwordEncoder;
    
    
    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        matchRepository = mock(MatchRepository.class);
        mapper = Mappers.getMapper(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, mapper,passwordEncoder,matchRepository);
        
    }

    @Test
    public void getUsersTest(){
        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        User user1 = new User("Carlos López", "carlos_10", "carlos@example.com", "password123", LocalDateTime.of(1995, 3, 12, 0, 0), true, "Amante del fútbol y los torneos locales.", 4.5f, "USER");
        User user2 = new User("Laura Gómez", "laura_admin", "laura@example.com", "adminPass!", LocalDateTime.of(1988, 7, 23, 0, 0), false, "Administradora de la plataforma.", 3.75f, "ADMIN", "USER");
        User user3 = new User("Pedro Martín", "pedro_m", "pedro@example.com", "newuserpass", LocalDateTime.of(2000, 1, 15, 0, 0), true, "Nuevo en la aplicación, aprendiendo.", 1.0f, "USER");
        List<User> userList = List.of(user1,user2,user3);


        Page<User> userPage = new PageImpl<>(userList,pageable,userList.size());

        //WHEN
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        
        Page<UserDTO> result = userService.getUsers(pageable);
        Page<UserDTO> expected = userPage.map(u -> mapper.toDTO(u));
        

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));

    }

    @Test
    public void getExistingUserByIdTest(){
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
    public void getNonExistingUserByIdTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.getUser(id);
        });
        //THEN
        assertThat(ex.getReason(), equalTo("Usuario no encontrado"));
    }

    @Test
    public void deleteExistingUserTest(){
        //GIVEN
        Random random = new Random();
        long id = Math.abs(random.nextLong());
        User user = new User("Pedro Martín", "pedro_m", "pedro@example.com", "newuserpass", LocalDateTime.of(2000, 1, 15, 0, 0), true, "Nuevo en la aplicación, aprendiendo.", 1.0f, "USER");
        user.setMatchesAsTeam1Player(List.of());
        user.setMatchesAsTeam2Player(List.of());
        user.setOrganizedMatches(List.of());
        user.setId(id);
        Optional<User> optionalUser = Optional.of(user);

        //WHEN
        when(userRepository.findById(id)).thenReturn(optionalUser);
        userService.delete(id);

        //THEN
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteNonExistingUserTest(){
        //GIVEN
        Random random = new Random();
        long id = random.nextLong();
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.delete(id);
        });

        //GIVEN
        assertThat(ex.getMessage(), equalTo("User with id " + id + " does not exist."));
    }

    @Test
    public void createUserTest() throws IOException{
        //GIVEN 
        User originalUser = new User("Laura Gómez", "laura_admin", "laura@example.com", "adminPass!", LocalDateTime.of(1988, 7, 23, 0, 0), false, "Creadora de la plataforma.", 0.0f, "USER");
        UserDTO originalUserDTO = mapper.toDTO(originalUser);
        //WHEN
        when(userRepository.save(originalUser)).thenReturn(originalUser);
        when(passwordEncoder.encode("adminPass!")).thenReturn("encoded_pass");
        UserDTO createdUser = userService.createUser(originalUserDTO,false);
        
        //GIVEN
        assertThat(createdUser.password(), equalTo("encoded_pass"));
        assertThat(createdUser.realname(), equalTo(originalUser.getRealname()));
    }

    @Test
    public void replaceExistingUserTest(){
        //GIVEN 
        long id = 2L;
        Optional<User> userOptional = Optional.of(new User("Jose Martinez","jose45","jose@emeal.com","joselito1", LocalDateTime.of(1978,10,8,12,0),true,"",2.53f));
        User updatedUser = new User("Jose Lopez","jose12","jose@email.com","joseito2",LocalDateTime.now(),false, "",7.0f);
        updatedUser.setId(id);
        UserDTO updatedUserDTO = mapper.toDTO(updatedUser);

        //WHEN
        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(userOptional);
        UserDTO replacedClubDTO = userService.replaceUser(id, updatedUserDTO);

        //THEN
        
        assertThat(replacedClubDTO.id(), equalTo(id));
        assertThat(replacedClubDTO.realname(), equalTo(updatedUserDTO.realname()));
        assertThat(replacedClubDTO.username(), equalTo(updatedUserDTO.username()));
        assertThat(replacedClubDTO.email(), equalTo(updatedUserDTO.email()));
        assertThat(replacedClubDTO.birthDate(), equalTo(updatedUserDTO.birthDate()));
        assertThat(replacedClubDTO.gender(), equalTo(updatedUserDTO.gender()));
        assertThat(replacedClubDTO.description(), equalTo(updatedUserDTO.description()));
        assertThat(replacedClubDTO.level(), equalTo(updatedUserDTO.level()));

        assertThat(replacedClubDTO.password(), nullValue());

        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    public void replaceNonExistingUserTest(){
        //GIVEN
        long id = 8L;
        Optional<User> emptyUser = Optional.empty();
        User updatedUser =  new User();
        UserDTO updatedUserDTO = mapper.toDTO(updatedUser);

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);

        //THEN
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->  userService.replaceUser(id, updatedUserDTO));
        assertThat(ex.getMessage(),equalTo("User with id " + id + " does not exist."));

    }



}
