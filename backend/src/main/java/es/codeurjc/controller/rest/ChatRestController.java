package es.codeurjc.controller.rest;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.service.ChatMessageService;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatRestController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/")
    public Collection<ChatMessageDTO> getChatMessages(){
       return chatMessageService.getChatMessages();
    }

    @GetMapping("/{id}")
    public ChatMessageDTO getChatMessage(@PathVariable Long id) {
        return chatMessageService.getChatMessage(id);
    }
}