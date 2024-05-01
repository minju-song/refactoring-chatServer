package com.tgtg.chat.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer {

    @KafkaListener(topics = "testTopic", groupId = "group1")
    public void listener(Object data) {
        System.out.println(">>>>>>"
                +data);
    }
}
