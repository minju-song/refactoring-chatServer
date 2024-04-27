package com.tgtg.chat.socket.lobby;

import com.tgtg.chat.access.connect.ConnectedUserService;
import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins="*")
@Controller
public class AccessController {

    @Autowired
    ConnectedUserService connectedUserService;

    //회원이 게임 대기방 들어왔을 때
    @MessageMapping("/{roomId}/enter")
    @SendTo("/room/{roomId}/connect")
    public Map enter(@DestinationVariable int roomId, AnonymousDTO anonymous) {

        //인원수 추가해줌
        connectedUserService.userEntered(roomId, anonymous);

        //해당방 회원
        Set<AnonymousDTO> memberList = connectedUserService.getAllMembersInRoom(roomId);

        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", true);
        map.put("memberList", memberList);

        return map;
    }

    @MessageMapping("/{roomId}/leave")
    @SendTo("/room/{roomId}/connect")
    public Map leave(@DestinationVariable int roomId, AnonymousDTO anonymous) {

        connectedUserService.userLeft(roomId, anonymous);

        //해당방 회원
        Set<AnonymousDTO> memberList = connectedUserService.getAllMembersInRoom(roomId);

        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", false);
        map.put("memberList", memberList);

        return map;
    }
}
