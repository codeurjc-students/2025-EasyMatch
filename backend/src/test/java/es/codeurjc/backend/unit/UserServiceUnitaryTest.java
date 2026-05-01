package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.hibernate.engine.jdbc.BlobProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.UserDTO;
import es.codeurjc.dto.UserMapper;
import es.codeurjc.dto.UserSportProfileDTO;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.Sport;
import es.codeurjc.model.User;
import es.codeurjc.repository.UserRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.SportService;
import es.codeurjc.service.UserService;

@Tag("unit")
@ActiveProfiles("test")
public class UserServiceUnitaryTest {

    private UserRepository userRepository;
    private MatchRepository matchRepository;
    private UserService userService;
    private SportService sportService;
    private ChatMessageService chatMessageService;
    private UserMapper mapper;
    private PasswordEncoder passwordEncoder;

    private Long defaultUserId;
    private Long defaultSportId;

    private User defaultUser;
    private User secondUser;
    private User adminUser;

    private Sport defaultSport;
    
    
    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        matchRepository = mock(MatchRepository.class);
        sportService = mock(SportService.class);
        mapper = Mappers.getMapper(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, mapper,passwordEncoder,matchRepository,chatMessageService, sportService);
        
        //GIVEN
        defaultUserId = 1L;
        defaultSportId = 1L;

        defaultUser = new User(
                "Carlos López",
                "carlos_10",
                "carlos@example.com",
                "password123",
                LocalDateTime.of(1995, 3, 12, 0, 0),
                true,
                "Amante del fútbol y los torneos locales.",
                "USER"
        );
        defaultUser.setId(defaultUserId);
        defaultUser.setMatchesAsTeam1Player(List.of());
        defaultUser.setMatchesAsTeam2Player(List.of());
        defaultUser.setOrganizedMatches(List.of());

        secondUser = new User(
                "Pedro Martín",
                "pedro_m",
                "pedro@example.com",
                "newuserpass",
                LocalDateTime.of(2000, 1, 15, 0, 0),
                true,
                "Nuevo en la aplicación, aprendiendo.",
                "USER"
        );
        secondUser.setId(2L);

        adminUser = new User(
                "Laura Gómez",
                "laura_admin",
                "laura@example.com",
                "adminPass!",
                LocalDateTime.of(1988, 7, 23, 0, 0),
                false,
                "Administradora de la plataforma.",
                "ADMIN",
                "USER"
        );
        adminUser.setId(3L);

        defaultSport = new Sport(
                "Fútbol",
                List.of(),
                ScoringType.SCORE
        );
        defaultSport.setId(defaultSportId);
    }

    private User createBaseUser() {
        User user = new User(
                "Carlos López",
                "carlos_10",
                "carlos@example.com",
                "password123",
                LocalDateTime.of(1995, 3, 12, 0, 0),
                true,
                "Usuario base",
                "USER"
        );
        user.setId(defaultUserId);
        user.setMatchesAsTeam1Player(List.of());
        user.setMatchesAsTeam2Player(List.of());
        user.setOrganizedMatches(List.of());
        return user;
    }

    @Test
    public void getUsersShouldReturnPageOfUserDTOs(){
        //GIVEN
        PageRequest pageable = PageRequest.of(0, 10);
        List<User> userList = List.of(createBaseUser(), secondUser, adminUser);


        Page<User> userPage = new PageImpl<>(userList,pageable,userList.size());

        //WHEN
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        
        Page<UserDTO> result = userService.getUsers(pageable);
        Page<UserDTO> expected = userPage.map(u -> mapper.toDTO(u));
        

        //THEN
        assertThat(result.getNumberOfElements(),equalTo(expected.getNumberOfElements()));

    }

    @Test
    public void getExistingUserByIdShouldReturnCorrectUserDTO(){
        //GIVEN
        Optional<User> optionalUser = Optional.of(defaultUser);

        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(optionalUser);
        UserDTO result = userService.getUser(defaultUserId);
        UserDTO expected = mapper.toDTO(defaultUser);

        //THEN
        assertThat(result, equalTo(expected));
    }

    @Test
    public void getNonExistingUserByIdShouldThrowException404(){
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
        assertThat(ex.getReason(), equalTo("User not found"));
    }

    @Test
    public void deleteExistingUserShouldSucceed(){
        //GIVEN
        defaultUser.setMatchesAsTeam1Player(List.of());
        defaultUser.setMatchesAsTeam2Player(List.of());
        defaultUser.setOrganizedMatches(List.of());
        Optional<User> optionalUser = Optional.of(defaultUser);

        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(optionalUser);
        userService.delete(defaultUserId);

        //THEN
        verify(userRepository, times(1)).deleteById(defaultUserId);
    }

    @Test
    public void deleteNonExistingUserShouldThrowException404(){
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
    public void createUserShouldSucceed() throws IOException{
        //GIVEN 
        UserDTO originalUserDTO = mapper.toDTO(adminUser);
        //WHEN
        when(userRepository.save(adminUser)).thenReturn(adminUser);
        when(passwordEncoder.encode("adminPass!")).thenReturn("encoded_pass");
        UserDTO createdUser = userService.createUser(originalUserDTO,false);
        
        //GIVEN
        assertThat(createdUser.password(), equalTo("encoded_pass"));
        assertThat(createdUser.realname(), equalTo(adminUser.getRealname()));
    }

    @Test
    public void replaceExistingUserShouldSucceed(){
        //GIVEN 
        Optional<User> userOptional = Optional.of(defaultUser);
        User updatedUser = new User("Jose Lopez","jose12","jose@email.com","joseito2",LocalDateTime.now(),false, "");
        updatedUser.setId(defaultUserId);
        UserDTO updatedUserDTO = mapper.toDTO(updatedUser);

        //WHEN
        when(userRepository.existsById(defaultUserId)).thenReturn(true);
        when(userRepository.findById(defaultUserId)).thenReturn(userOptional);
        UserDTO replacedClubDTO = userService.replaceUser(defaultUserId, updatedUserDTO);

        //THEN
        
        assertThat(replacedClubDTO.id(), equalTo(defaultUserId));
        assertThat(replacedClubDTO.realname(), equalTo(updatedUserDTO.realname()));
        assertThat(replacedClubDTO.username(), equalTo(updatedUserDTO.username()));
        assertThat(replacedClubDTO.email(), equalTo(updatedUserDTO.email()));
        assertThat(replacedClubDTO.birthDate(), equalTo(updatedUserDTO.birthDate()));
        assertThat(replacedClubDTO.gender(), equalTo(updatedUserDTO.gender()));
        assertThat(replacedClubDTO.description(), equalTo(updatedUserDTO.description()));

        assertThat(replacedClubDTO.password(), nullValue());

        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    public void replaceNonExistingUserShouldThrowException404(){
        //GIVEN
        long id = 8L;
        Optional<User> emptyUser = Optional.empty();
        UserDTO updatedUserDTO = mapper.toDTO(new User());

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);

        //THEN
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, ()->  userService.replaceUser(id, updatedUserDTO));
        assertThat(HttpStatus.NOT_FOUND, equalTo(ex.getStatusCode()));
        assertThat(ex.getReason(), equalTo("User with id " + id + " does not exist."));

    }

    @Test
    public void createUserImageShouldSucceed() throws IOException {

        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUser));
        userService.createUserImage(defaultUserId, new ByteArrayInputStream(new byte[0]), 512);

        //THEN
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void replaceUserImageShouldSucceed() throws IOException {
        //GIVEN
        User user = createBaseUser();
        user.setImage(BlobProxy.generateProxy(new ByteArrayInputStream(new byte[0]), 512));

        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(Optional.of(user));
        userService.replaceUserImage(defaultUserId, new ByteArrayInputStream(new byte[0]), 1024);

        //THEN
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getUserImageNonExistingUserShouldThrowException404() {
        //GIVEN
        long id = 5L;
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.getUserImage(id);
        });
        //THEN
        assertThat(ex.getReason(), equalTo("User with id " + id + " does not exist."));
    }

    @Test
    public void addUserSportProfileNonExistingUserShouldThrowException404() {
        //GIVEN
        long id = 6L;
        Optional<User> emptyUser = Optional.empty();

        //WHEN
        when(userRepository.findById(id)).thenReturn(emptyUser);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.addSportProfileToUser(id, 1L, new UserSportProfileDTO(null, null, id, null));
        });
        //THEN
        assertThat(ex.getReason(), equalTo("User not found"));
    }

    @Test 
    public void addUserSportProfileNonExistingSportShouldThrowException404() {
        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUser));
        when(sportService.findById(defaultSportId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.addSportProfileToUser(defaultUserId, defaultSportId, new UserSportProfileDTO(1L, null, defaultUserId, null));
        });

        //THEN
        assertThat(ex.getReason(), equalTo("Sport not found"));
        assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void addUserSportProfileShouldSucceed() {
        //WHEN
        when(userRepository.findById(defaultUserId)).thenReturn(Optional.of(defaultUser));
        when(sportService.findById(defaultSportId)).thenReturn(Optional.of(defaultSport));
        userService.addSportProfileToUser(defaultUserId, defaultSportId, new UserSportProfileDTO(1L, defaultSport.getName(), defaultUserId, null));

        //THEN
        verify(userRepository, times(1)).save(any(User.class));
    }
}
