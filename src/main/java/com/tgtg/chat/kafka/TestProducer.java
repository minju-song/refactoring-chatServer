package com.tgtg.chat.kafka;

import com.tgtg.chat.chatroom.dto.Chatroom;
import com.tgtg.chat.chatroom.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {

    @Autowired
    ChatroomService chatroomService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create(Chatroom room) {
        String data = chatroomService.serializeChatroom(room);
        kafkaTemplate.send("testTopic", data);
    }
}
