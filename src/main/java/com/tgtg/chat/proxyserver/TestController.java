package com.tgtg.chat.proxyserver;

import com.tgtg.chat.chatroom.dto.Chatroom;
import com.tgtg.chat.kafka.TestProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "test")
public class TestController {

    @Autowired
    TestProducer producer;

    @GetMapping(value = "/{firstName}")
    public String helloGet(@PathVariable("firstName") String firstName,
                           @RequestParam("lastName") String lastName) {
        Chatroom room = new Chatroom(1, "test", "test");
        producer.create(room);

        return String.format("{\"message\":\"Hello %s %s\"}", firstName, lastName);
    }

    @PostMapping
    public String helloPost(@RequestBody Chatroom room) {

        return String.format("{\"message\":\"Hello %s\"}", room.getTitle());
    }
}
