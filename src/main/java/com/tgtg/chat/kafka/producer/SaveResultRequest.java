package com.tgtg.chat.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SaveResultRequest {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SaveResultRequest(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create(String memberId, String result) {
        Map<String, String> request = new HashMap<>();
        request.put("memberId", memberId);
        request.put("result", result);
        kafkaTemplate.send("SaveResultRequest",serializeRequest(request));
    }

    public String serializeRequest(Map<String, String> request) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
