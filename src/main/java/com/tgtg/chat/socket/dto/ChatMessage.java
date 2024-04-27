package com.tgtg.chat.socket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private int roomId;
    private String sender;
    private String gameRole;
    private String senderEmail;
    private String senderImage;
    private String message;
    private LocalDateTime sendDate;

    @Builder
    public ChatMessage(int roomId, String sender, String senderEmail,String senderImage, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
        this.message = message;
    }
}
