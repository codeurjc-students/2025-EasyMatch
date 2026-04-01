package es.codeurjc.backend.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.repository.ChatMessageRepository;
import es.codeurjc.service.ChatMessageService;

@Tag("unit")
@ActiveProfiles("test")
public class ChatMessageServiceUnitaryTest {

    private ChatMessageRepository chatMessageRepository;
    private ChatMessageService chatMessageService;
    private ChatMessageMapper mapper;

    @BeforeEach
    void setUp() {
        chatMessageRepository = mock(ChatMessageRepository.class);
        mapper = Mappers.getMapper(ChatMessageMapper.class);
        chatMessageService = new ChatMessageService(chatMessageRepository, mapper);
    }

    @Test
    public void saveChatMessageTest() {
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setContent("Hello, my friend!");

        chatMessageService.save(message);
        verify(chatMessageRepository,times(1)).save(message);
    }

    @Test
    public void getUserChatsTest() {
        Long userId = 1L;
        chatMessageService.getUserMessages(userId);
        verify(chatMessageRepository,times(1)).findMessagesByUser(userId);
    }

    @Test
    public void getMatchMessagesTest() {
        Long matchId = 1L;
        chatMessageService.getMatchMessages(matchId);
        verify(chatMessageRepository,times(1)).findByMatchId(matchId);
    }

}
