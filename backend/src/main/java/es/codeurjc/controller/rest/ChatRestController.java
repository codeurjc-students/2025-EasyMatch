package es.codeurjc.controller.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.dto.UserDTO;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.UserService;

@RestController
@RequestMapping("/api/v1/chats")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatRestController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public Collection<ChatMessageDTO> getUserChats() {
        UserDTO loggedUser = userService.getLoggedUserDTO();
        return chatMessageService.getUserChats(loggedUser.id());
    }
}