package com.drc.stomp.dto;

import static java.time.LocalDateTime.now;

import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    
    private String from;
    private String text;
    private String recipient;
    private String time;
    private byte[] file;

    public ChatMessage() {
        this.time = now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    public ChatMessage(String from, String text, String recipient, byte[] file) {
        this();
        this.from = from;
        this.text = text;
        this.recipient = recipient;
        this.file = file;
    }
}
