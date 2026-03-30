package es.codeurjc.dto;

import org.mapstruct.Mapper;

import es.codeurjc.model.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    
    default ChatMessageDTO toDTO(ChatMessage message) {
        return new ChatMessageDTO(
            message.getMatch().getId(),
            message.getContent(),
            message.getSender().getUsername(),
            message.getType(),
            message.getTimestamp()
        );
    }
    ChatMessage toDomain(ChatMessageDTO chatMessageDTO);
} 

