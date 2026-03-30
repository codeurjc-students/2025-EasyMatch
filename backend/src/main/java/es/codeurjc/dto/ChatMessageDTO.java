package es.codeurjc.dto;

import java.time.LocalDateTime;

import es.codeurjc.model.MessageType;

public record ChatMessageDTO (
    Long matchId,
    String content,
    String senderUsername,
    MessageType type,
    LocalDateTime timestamp
){}
