package com.tgtg.chat.chatroom.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgtg.chat.access.connect.ConnectedUserService;
import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import com.tgtg.chat.chatroom.dto.Chatroom;
import com.tgtg.chat.subject.dto.SubjectDTO;
import com.tgtg.chat.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ChatroomServiceImpl implements ChatroomService{

    @Autowired
    ConnectedUserService connectedUserService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Chatroom findTextRoom() { return findOrCreateRoom("text"); }

    @Override
    public Chatroom findVoiceRoom() { return findOrCreateRoom("voice"); }

    @Override
    public Chatroom findOrCreateRoom(String type) {
        int baseRoomId = type.equals("text") ? 0 : 100;
        TreeSet<Integer> usedIds = new TreeSet<>(); // 사용 중인 방 번호를 저장

        String key = "chatRooms";
        List<String> roomsString = redisTemplate.opsForList().range(key, 0, -1);

        List<Chatroom> rooms = new ArrayList<>();
        for(String r : roomsString) {
            Chatroom room = deserializeChatroom(r);
            rooms.add(room);
        }

        // 기존 방 중에서 조건에 맞는 방이 있는지 확인하고, 사용 중인 roomId 기록
        for (Chatroom room : rooms) {
            if (room.getType().equals(type)) {
                usedIds.add(room.getRoomId());
                if (room.getStatus().equals("ready") && connectedUserService.getConnectedUserCount(room.getRoomId()) < 12) {
                    return room;
                }
            }
        }

        // 가능한 가장 낮은 roomId 찾기
        int newRoomId = baseRoomId;
        for (Integer id : usedIds) {
            if (id == newRoomId) {
                newRoomId++; // 현재 ID가 사용 중이면, 다음 번호로
            } else {
                break; // 사용 중이지 않은 첫 번호에서 반복문 탈출
            }
        }

        // 새 방 생성
        Chatroom newRoom = Chatroom.builder()
                .roomId(newRoomId)
                .type(type)
                .status("ready")
                .build();

        String value = serializeChatroom(newRoom);
        redisTemplate.opsForList().rightPush(key, value);

        return newRoom;
    }

    @Override
    public Chatroom getRoomById(int roomId) {
        String key = "chatRooms";
        List<String> roomsString = redisTemplate.opsForList().range(key, 0, -1);

        List<Chatroom> rooms = new ArrayList<>();
        for(String r : roomsString) {
            Chatroom room = deserializeChatroom(r);
            rooms.add(room);
        }

        for (Chatroom room : rooms) {
            if (room.getRoomId() == roomId) {
                return room;
            }
        }
        return null; // 찾지 못한 경우 null 반환
    }

    @Override
    public void setRoomStatusToRun(int roomId) {
        String key = "chatRooms";
        List<String> roomsString = redisTemplate.opsForList().range(key, 0, -1);

        List<Chatroom> rooms = new ArrayList<>();
        boolean isUpdated = false;

        for(String r : roomsString) {
            Chatroom room = deserializeChatroom(r);
            if(room.getRoomId() == roomId) {
                room.setStatus("run");
                isUpdated = true;
            }
            rooms.add(room);
        }

        if(isUpdated) {
            redisTemplate.delete(key);
            for(Chatroom room : rooms) {
                redisTemplate.opsForList().rightPush(key, serializeChatroom(room));
            }
        }
        else {
            System.out.println("해당하는 방이 없습니다.");
        }
    }

    @Override
    public boolean removeRoomById(int roomId) {
        String key = "chatRooms";
        List<String> roomsString = redisTemplate.opsForList().range(key, 0, -1);

        List<Chatroom> rooms = new ArrayList<>();
        boolean isRemoved = false;

        for(String r : roomsString) {
            Chatroom room = deserializeChatroom(r);
            if(room.getRoomId() == roomId) {
                isRemoved = true;
            }
            else {
                rooms.add(room);
            }
        }

        if(isRemoved) {
            redisTemplate.delete(key);
            for(Chatroom room : rooms) {
                redisTemplate.opsForList().rightPush(key, serializeChatroom(room));
            }
            return true;
        }
        else {
            System.out.println("해당하는 방이 없습니다.");
            return false;
        }
    }

    @Override
    public void setTitle(int roomId) {
        boolean isSet = false;

        SubjectDTO subject = subjectService.getSubject();

        String key = "chatRooms";
        List<String> roomsString = redisTemplate.opsForList().range(key, 0, -1);

        List<Chatroom> rooms = new ArrayList<>();

        for(String r : roomsString) {
            Chatroom room = deserializeChatroom(r);
            if(room.getRoomId() == roomId) {
                room.setTitle(subject.getSubjectTitle());
                room.setAnswerA(subject.getSubjectAnswerA());
                room.setAnswerB(subject.getSubjectAnswerB());
                isSet = true;
            }
            rooms.add(room);
        }


        if(isSet) {
            redisTemplate.delete(key);
            for(Chatroom room : rooms) {
                redisTemplate.opsForList().rightPush(key, serializeChatroom(room));
            }
        }
        else {
            System.out.println("해당하는 방이 없습니다.");
        }
    }

    @Override
    public Set<AnonymousDTO> startGame(int roomId) {
        setRoomStatusToRun(roomId);
        setTitle(roomId);
        Set<AnonymousDTO> memberSet = connectedUserService.setRole(roomId);

        return memberSet;
    }

    @Override
    public String serializeChatroom(Chatroom room) {
        try {
            return objectMapper.writeValueAsString(room);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Chatroom deserializeChatroom(String json) {
        try {
            return objectMapper.readValue(json, Chatroom.class);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
