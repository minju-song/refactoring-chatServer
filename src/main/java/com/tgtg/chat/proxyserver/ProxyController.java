package com.tgtg.chat.proxyserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

@RestController
public class ProxyController {

    public void saveResult(String memberId, String result) {

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("memberId", memberId);
        requestData.put("result", result);

        // 2서버로 요청 전송 및 응답 받기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://localhost:8098/saveResult", requestData, String.class);

    }
}
