package com.tgtg.chat.socket.game;

import com.tgtg.chat.access.connect.ConnectedUserService;
import com.tgtg.chat.access.ready.ReadyUserService;
import com.tgtg.chat.anonymous.service.AnonymousService;
import com.tgtg.chat.chatroom.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalTime;

@CrossOrigin(origins="*")
@Controller
public class GameController {

    @Autowired
    ConnectedUserService connectedUserService;

    @Autowired
    ChatroomService chatroomService;

    @Autowired
    ReadyUserService readyUserService;

    @Autowired
    AnonymousService anonymousService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //게임방 타이머 현재시간
    @MessageMapping("/{roomId}/sendTime")
    public void gameTimer(@DestinationVariable int roomId) {
        LocalTime endTime = connectedUserService.gameStartUser(roomId);
        if(endTime != null) {
            simpMessagingTemplate.convertAndSend("/room/" + roomId + "/sendTime", endTime);
        }
    }

    //게임방 투표
    @MessageMapping("/{roomId}/gameVote")
    public void gameVote(@DestinationVariable int roomId, int gameSelect) {
        connectedUserService.gameVoteCount(roomId, gameSelect);
    }

    //    게임 결과 전송
    @MessageMapping("/{roomId}/getResult")
    public void gameResult(@DestinationVariable int roomId) {
        if(connectedUserService.getZeroCount(roomId)) {
            String result = connectedUserService.getVoteResult(roomId);
//    		먼저 DB에 저장
            connectedUserService.saveResult(roomId, result);
//    		관련정보 삭제
            connectedUserService.deleteRoom(roomId);
            chatroomService.removeRoomById(roomId);
            readyUserService.deleteReadyUser(roomId);
            anonymousService.deleteCount(roomId);
//    		결과전송
            simpMessagingTemplate.convertAndSend("/room/" + roomId + "/getResult", result);
        }
    }

}
