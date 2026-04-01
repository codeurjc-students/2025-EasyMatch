package es.codeurjc.controller.chat;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.Match;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.MatchService;
import es.codeurjc.service.UserService;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatMessageMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{matchId}")
    public ChatMessageDTO sendMessage(@DestinationVariable long matchId, @Payload ChatMessageDTO dto) {
        Match match = matchService.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Match not found"));
        ChatMessage entity = ChatMessage.builder()
                .match(match)
                .content(dto.content())
                .sender(userService.findByUsername(dto.senderUsername()))
                .type(dto.type())
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageService.save(entity);

        messagingTemplate.convertAndSend(
            "/topic/match/" + matchId,
            mapper.toDTO(entity)
        );

        return dto;
    }
}
