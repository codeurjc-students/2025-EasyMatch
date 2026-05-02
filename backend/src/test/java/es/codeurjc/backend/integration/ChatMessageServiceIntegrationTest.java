package es.codeurjc.backend.integration;


import static org.hamcrest.Matchers.not;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.MatchDTO;
import es.codeurjc.dto.MatchMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.MessageType;
import es.codeurjc.model.User;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.UserService;

@Tag("integration")
@SpringBootTest(classes = es.codeurjc.easymatch.EasyMatchApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class ChatMessageServiceIntegrationTest {
    
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchMapper matchMapper;

    @Test
    public void getUserChatsShouldReturnMessagesWhereUserIsSender(){
        Long userId = 2L;
        List<ChatMessageDTO> userChats = chatMessageService.getUserMessages(userId);
        assertThat(userChats, is(not(empty())));
    }

    @Test 
    public void getMatchMessagesShouldReturnMessagesRelatedToMatch(){
        Long matchId = 1L;
        List<ChatMessageDTO> matchMessages = chatMessageService.getMatchMessages(matchId);
        assertThat(matchMessages.size(), is(greaterThan(0)));
    }

    @Test
    @WithMockUser(username = "pedro@emeal.com", roles = {"USER"})
    public void saveChatMessageShouldSucceed(){
        User user = userService.getLoggedUser();
        MatchDTO matchDTO = matchService.getMatch(1L);
        ChatMessage newMessage = new ChatMessage(MessageType.CHAT,"Hey!", user, matchMapper.toDomain(matchDTO));
        ChatMessage savedMessage = chatMessageService.save(newMessage);
        assertThat(savedMessage.getId(), is(greaterThan(0L)));
        assertThat(savedMessage.getContent(), is("Hey!"));
    }
    

}
