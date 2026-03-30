package es.codeurjc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.repository.ChatMessageRepository;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDTO> getUserChats(Long userId) {

        List<ChatMessage> messages = chatMessageRepository.findMessagesByUser(userId);

        return messages.stream()
                .map(chatMessageMapper::toDTO)
                .toList();
    }

    public List<ChatMessageDTO> getMatchMessages(Long matchId) {
        return chatMessageRepository.findByMatchId(matchId)
                .stream()
                .map(chatMessageMapper::toDTO)
                .toList();
    }
}
