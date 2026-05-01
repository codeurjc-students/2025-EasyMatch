package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.Match;
import es.codeurjc.model.MessageType;
import es.codeurjc.model.User;
import es.codeurjc.repository.ChatMessageRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.UserRepository;
import es.codeurjc.service.ChatMessageService;

@Tag("unit")
@ActiveProfiles("test")
public class ChatMessageServiceUnitaryTest {

    private ChatMessageRepository chatMessageRepository;
    private ChatMessageService chatMessageService;
    private UserRepository userRepository;
    private MatchRepository matchRepository;
    private ChatMessageMapper mapper;

    private Long defaultMessageId;
    private Long defaultMatchId;
    private Long defaultUserId;

    private User defaultUser;
    private Match defaultMatch;

    private ChatMessage defaultMessage;
    private ChatMessageDTO defaultMessageDTO;

    @BeforeEach
    void setUp() {
        chatMessageRepository = mock(ChatMessageRepository.class);
        userRepository = mock(UserRepository.class);
        matchRepository = mock(MatchRepository.class);
        mapper = Mappers.getMapper(ChatMessageMapper.class);
        chatMessageService = new ChatMessageService(chatMessageRepository, mapper, matchRepository, userRepository);

        defaultMessageId = 1L;
        defaultMatchId = 1L;
        defaultUserId = 1L;

        defaultUser = new User();
        defaultUser.setId(defaultUserId);
        defaultUser.setUsername("user1");

        defaultMatch = new Match();
        defaultMatch.setId(defaultMatchId);

        defaultMessage = new ChatMessage();
        defaultMessage.setId(defaultMessageId);
        defaultMessage.setContent("Test message");
        defaultMessage.setSender(defaultUser);
        defaultMessage.setMatch(defaultMatch);
        defaultMessage.setType(MessageType.CHAT);
        defaultMessage.setTimestamp(LocalDateTime.now());

        defaultMessageDTO = new ChatMessageDTO(
                defaultMessageId,
                defaultMatchId,
                "Updated message",
                "user1",
                MessageType.CHAT,
                LocalDateTime.now()
        );
    }

    @Test
    public void saveChatMessageShouldSucceed() {

        //WHEN
        when(chatMessageRepository.save(defaultMessage)).thenReturn(defaultMessage);
        ChatMessage savedMessage = chatMessageService.save(defaultMessage); 

        //THEN
        verify(chatMessageRepository,times(1)).save(defaultMessage);
        assertThat(savedMessage, notNullValue());
        assertThat(savedMessage.getContent(), equalTo("Test message"));
    }

    @Test
    public void getUserChatsShouldSucceed() {
        //WHEN
        List<ChatMessageDTO> userMessages = chatMessageService.getUserMessages(defaultUserId);

        //THEN
        verify(chatMessageRepository,times(1)).findMessagesByUser(defaultUserId);
        assertThat(userMessages, notNullValue());
    }

    @Test
    public void getMatchMessagesShouldSucceed() {
        //WHEN
        when(chatMessageRepository.findByMatchId(defaultMatchId)).thenReturn(List.of(defaultMessage));
        List<ChatMessageDTO> matchMessages = chatMessageService.getMatchMessages(defaultMatchId);

        //THEN
        verify(chatMessageRepository,times(1)).findByMatchId(defaultMatchId);
        assertThat(matchMessages, notNullValue());
        assertThat(matchMessages.size(), greaterThan(0));
    }

    @Test
    public void getChatMessageShouldReturnCorrectMessage() {
        //WHEN
        when(chatMessageRepository.findById(defaultMessageId)).thenReturn(Optional.of(defaultMessage));

        chatMessageService.getChatMessage(defaultMessageId);

        //THEN
        verify(chatMessageRepository, times(1)).findById(defaultMessageId);
    }

    @Test
    public void getNonExistentChatMessageShouldThrowException404() {
        //WHEN
        when(chatMessageRepository.findById(defaultMessageId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            chatMessageService.getChatMessage(defaultMessageId);
        });
        
        //THEN
        assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(ex.getReason(), equalTo("Chat message with id " + defaultMessageId + " not found"));

        verify(chatMessageRepository, times(1)).findById(defaultMessageId);
    }

    @Test
    public void replaceChatMessageShouldSucceed() {
        //WHEN
        when(chatMessageRepository.existsById(defaultMessageId)).thenReturn(true);
        when(matchRepository.findById(defaultMatchId)).thenReturn(Optional.of(defaultMatch));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(defaultUser));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(defaultMessage);

        chatMessageService.replacechatMessage(defaultMessageId, defaultMessageDTO);

        //THEN
        verify(chatMessageRepository, times(1)).existsById(defaultMessageId);
        verify(matchRepository, times(1)).findById(defaultMatchId);
        verify(userRepository, times(1)).findByUsername("user1");
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    public void replaceNonExistentChatMessageShouldThrowException404() {
        //WHEN
        when(chatMessageRepository.existsById(defaultMessageId)).thenReturn(false);       
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> {
            chatMessageService.replacechatMessage(defaultMessageId, defaultMessageDTO);
        });

        //THEN
        assertThat(ex.getMessage(), equalTo("Message with id " + defaultMessageId + " does not exist."));
        verify(chatMessageRepository, times(1)).existsById(defaultMessageId);
    }

}
