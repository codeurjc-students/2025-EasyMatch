package es.codeurjc.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.ChatMessageMapper;
import es.codeurjc.model.ChatMessage;
import es.codeurjc.model.Match;
import es.codeurjc.model.User;
import es.codeurjc.repository.ChatMessageRepository;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.UserRepository;

@Service
public class ChatMessageService {

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.mapper = chatMessageMapper;
    }
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageMapper mapper;


    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public Page<ChatMessage> findAll(Pageable pageable) {
		return chatMessageRepository.findAll(pageable);
	}  

    public Page<ChatMessageDTO> getMessages(Pageable pageable) {
        return findAll(pageable).map(mapper::toDTO);
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

    public ChatMessageDTO getChatMessage(Long id) {
        return mapper.toDTO(
            chatMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );
    }

     public ChatMessageDTO replacechatMessage(long id, ChatMessageDTO updatedchatMessageDTO) {
        if (chatMessageRepository.existsById(id)) {
            ChatMessage updatedchatMessage = mapper.toDomain(updatedchatMessageDTO);
            Match match = matchRepository.findById(updatedchatMessageDTO.matchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            User sender = userRepository.findByUsername(updatedchatMessageDTO.senderUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            
            updatedchatMessage.setId(id);
            updatedchatMessage.setMatch(match);
            updatedchatMessage.setSender(sender);
            
            chatMessageRepository.save(updatedchatMessage);
            return mapper.toDTO(updatedchatMessage);
 		} else {
 			throw new NoSuchElementException("chatMessage with id " + id + " does not exist.");
 		}
    }

    public void delete(long id) {
        Optional<ChatMessage> messageOptional = chatMessageRepository.findById(id);
        if (messageOptional.isPresent()) {
            chatMessageRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("chatMessage with id " + id + " does not exist.");
        }
    }
}
