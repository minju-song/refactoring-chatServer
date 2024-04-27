package com.tgtg.chat.socket.lobby;

import com.tgtg.chat.access.connect.ConnectedUserService;
import com.tgtg.chat.access.ready.ReadyUserService;
import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import com.tgtg.chat.chatroom.service.ChatroomService;
import com.tgtg.chat.socket.dto.GameRoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins="*")
@EnableAsync
@Controller
public class ReadyController {

    @Autowired
    ReadyUserService readyUserService;

    @Autowired
    ConnectedUserService connectedUserService;

    @Autowired
    ChatroomService chatroomService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //게임 준비 메소드
    @MessageMapping("/{roomId}/ready")
    @SendTo("/room/{roomId}/getReady")
    public int ready(@DestinationVariable int roomId) {
        //준비인원 추가
        readyUserService.readyUser(roomId);
        //준비한 인원 수가 방에 접속한 인원 수와 같고, 접속한 인원 수가 3명 이상이면 게임 시작
        if(readyUserService.getReady(roomId) == connectedUserService.getConnectedUserCount(roomId) &&
                readyUserService.getReady(roomId)>= 3	) {
            //해당 방의 상태를 'run'으로 업데이트해주고, 주제 및 팀을 랜덤으로 select
            //해당 방에 접속한 익명 프로필 리스트를 return 받음
            Set<AnonymousDTO> memberList = chatroomService.startGame(roomId);

            //해당 방에 접속한 리스트에게 각각 역할을 부여해줌 (심판/A팀/B팀)
            GameRoleDTO role = new GameRoleDTO();
            role.setRoleList(memberList);
            //텍스트 게임방 url
            if(roomId < 100) {
                role.setUrl("/user/textGame?roomId=");
            }
            //음성 게임방 url
            else {
                role.setUrl("/user/voiceGame?roomId=");
            }

            startGame(roomId, role);
            //simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", role);
        }
        return readyUserService.getReady(roomId);
    }

    @Async
    public void startGame(int roomId, GameRoleDTO role) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", role);
        }, 1, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    //게임 준비취소 메소드
    @MessageMapping("/{roomId}/unready")
    @SendTo("/room/{roomId}/getReady")
    public int unready(@DestinationVariable int roomId) {
        readyUserService.unreadyUser(roomId);
        return readyUserService.getReady(roomId);
    }

    //현재 준비한 회원 수
    @MessageMapping("/{roomId}/getReadyCount")
    @SendTo("/room/{roomId}/getReady")
    public int getReady(@DestinationVariable int roomId) {
        return readyUserService.getReady(roomId);
    }

}
