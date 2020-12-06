package com.drc.stomp.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.drc.stomp.dto.ChatMessage;

@Controller
public class BroadcastController {

    @MessageMapping("/broadcast")
    @SendTo("/topic/broadcast")
    public ChatMessage send(ChatMessage chatMessage) throws Exception {
        return new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), "ALL", null);
    }
}
