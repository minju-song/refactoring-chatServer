package com.tgtg.chat.chatroom.dto;

import lombok.Getter;
import lombok.Setter;
import com.tgtg.chat.anonymous.dto.AnonymousDTO;

@Getter
@Setter
public class GameRequest {

    private AnonymousDTO anonymous;
    private Chatroom room;

}
