package com.tgtg.chat.chatroom.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("ChatRoom")
public class Chatroom {

    private int roomId;
    private String type;
    //ready or run
    private String status;

    private String title;
    private String answerA;
    private String answerB;

    @Builder
    public Chatroom(int roomId, String type, String status) {
        //채팅방 아이디 임의로 지정
        this.roomId = roomId;
        this.type = type;
        this.status = status;
    }


}
