package es.codeurjc.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.dto.ChatMessageDTO;
import es.codeurjc.service.ChatMessageService;
import es.codeurjc.service.UserService;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatRestController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @GetMapping
	public Page<ChatMessageDTO> getMessages(Pageable pageable) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin puede acceder a todos los mensajes");
        }
        return chatMessageService.getMessages(pageable);
	}

    @GetMapping("/{id}")
    public ChatMessageDTO getMessage(@PathVariable Long id) {
        ChatMessageDTO chatMessage = chatMessageService.getChatMessage(id);
        if (chatMessage.senderUsername().equals(userService.getLoggedUser().getUsername()) || userService.getLoggedUser().getRoles().contains("ADMIN")) {
            return chatMessage;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este mensaje");
        }
    }

    @PutMapping("/{id}")
    public ChatMessageDTO replacechatMessage(@PathVariable long id, @RequestBody ChatMessageDTO updatedchatMessageDTO) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin puede editar un mensaje");
        }
        return chatMessageService.replacechatMessage(id, updatedchatMessageDTO);
    }

    @DeleteMapping("/{id}")
    public ChatMessageDTO deletechatMessage(@PathVariable long id) {
        if(!userService.getLoggedUser().getRoles().contains("ADMIN") ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el admin puede eliminar un mensaje");
        }
        ChatMessageDTO deletedMessage = chatMessageService.getChatMessage(id);
        chatMessageService.delete(id);
        return deletedMessage;
    }
}