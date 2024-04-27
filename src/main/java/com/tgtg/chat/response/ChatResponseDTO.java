package com.tgtg.chat.response;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import com.tgtg.chat.chatroom.dto.Chatroom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDTO {

    private AnonymousDTO anonymousDTO;
    private Chatroom chatroom;

    public ChatResponseDTO(AnonymousDTO anonymousDTO, Chatroom chatroom) {
        this.anonymousDTO = anonymousDTO;
        this.chatroom = chatroom;
    }
}
