package com.tgtg.chat.chatroom.web;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import com.tgtg.chat.anonymous.service.AnonymousService;
import com.tgtg.chat.chatroom.dto.Chatroom;
import com.tgtg.chat.chatroom.service.ChatroomService;
import com.tgtg.chat.kafka.TestConsumer;
import com.tgtg.chat.kafka.TestProducer;
import com.tgtg.chat.proxyserver.TrustAllCertificates;
import com.tgtg.chat.response.ChatResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins="*")
@RestController
public class ChatroomController {

    @Autowired
    ChatroomService chatroomService;

    @Autowired
    AnonymousService anonymousService;

    @Autowired
    TrustAllCertificates tr;

    @Autowired
    TestProducer tp;

    @Autowired
    TestConsumer tc;

    @PostMapping("/getChatroom")
    public ChatResponseDTO startChat(@RequestBody Map<String, Object> requestData) {

        tr.install();

        String memberId = (String) requestData.get("memberId");
        String type = (String) requestData.get("type");

        Chatroom room = new Chatroom(0, type, "ready");

        if(type.equals("text")) {
            room = chatroomService.findTextRoom();
        }
        else {
            room = chatroomService.findVoiceRoom();
        }

        room.setType(type);

        AnonymousDTO anonymous = anonymousService.createAnonymous(room.getRoomId(), memberId);

        //tp.create(anonymous);
        ChatResponseDTO response = new ChatResponseDTO(anonymous, room);

        return response;
    }

    @PostMapping("/getGame")
    public Map<String, Object> textGame(@RequestBody Map<String, Object> requestData) {

        int roomId = (int) requestData.get("roomId");
        Chatroom room = chatroomService.getRoomById(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("room", room);

        return response;
    }
}
