package com.drc.stomp.controller;

import com.drc.stomp.dto.ChatMessage;
import com.drc.stomp.service.ActiveUserChangeListener;
import com.drc.stomp.service.ActiveUserManager;

import java.io.IOException;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ChatController implements ActiveUserChangeListener {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveUserManager     activeUserManager;

    @PostConstruct
    private void init() {
        activeUserManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeUserManager.removeListener(this);
    }

    @MessageMapping("/chat")
    public void send(SimpMessageHeaderAccessor sha, @Payload ChatMessage chatMessage) throws Exception {
        String sender = sha.getUser().getName();
        ChatMessage message = new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), chatMessage.getRecipient(),
                null);
        if (!sender.equals(chatMessage.getRecipient())) {
            webSocket.convertAndSendToUser(sender, "/queue/messages", message);
        }
        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", message);
    }

    @PostMapping("/uploadFile")
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("sender") String sender,
            @RequestParam("recipient") String recipient) throws IOException {
        ChatMessage message = new ChatMessage(sender, file.getOriginalFilename(), recipient, file.getBytes());
        if (!sender.equals(recipient)) {
            webSocket.convertAndSendToUser(sender, "/queue/messages", message);
        }
        webSocket.convertAndSendToUser(recipient, "/queue/messages", message);
    }

    @Override
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeUserManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }
}
