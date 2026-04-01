package es.codeurjc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.repository.ChatMessageRepository;

@Service
public class ChatMessageService {

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.mapper = chatMessageMapper;
    }
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatMessageMapper mapper;

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDTO> getUserMessages(Long userId) {

        List<ChatMessage> messages = chatMessageRepository.findMessagesByUser(userId);

        return messages.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<ChatMessageDTO> getMatchMessages(Long matchId) {
        return chatMessageRepository.findByMatchId(matchId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<ChatMessageDTO> getChatMessages() {
        return chatMessageRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ChatMessageDTO getChatMessage(Long id) {
        return mapper.toDTO(
            chatMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );
    }

    public void delete(Long id) {
        if (!chatMessageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        chatMessageRepository.deleteById(id);
    }
}
