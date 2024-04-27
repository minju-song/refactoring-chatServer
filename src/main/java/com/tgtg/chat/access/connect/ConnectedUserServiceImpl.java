package com.tgtg.chat.access.connect;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import com.tgtg.chat.anonymous.service.AnonymousService;
import com.tgtg.chat.proxyserver.ProxyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConnectedUserServiceImpl implements ConnectedUserService{

    @Autowired
    AnonymousService anonymousService;

    @Autowired
    ProxyController proxy;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void userEntered(int roomId, AnonymousDTO anonymous) {
        String key = "chatRoom:" + roomId + ":members";
        String value = anonymousService.serializeAnonymousDTO(anonymous); // AnonymousDTO 객체를 직렬화하는 메소드 필요
        System.out.println("들어옴>>"+value);
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public LocalTime gameStartUser(int roomId) {
        String countKey = "chatRoom:" + roomId + ":gameStartCount";
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        Long count = redisTemplate.opsForValue().increment(countKey);

        // 참가자 수 확인
        String memberKey = "chatRoom:" + roomId + ":members";
        Long memberCount = redisTemplate.opsForSet().size(memberKey);

        if (count >= memberCount) {
            LocalTime now = LocalTime.now();
            String endTimeKey = "chatRoom:" + roomId + ":endTime";

            // 기존에 설정된 종료 시간이 없으면 새로운 종료 시간 설정
            String existingEndTime = valueOps.get(endTimeKey);
            if (existingEndTime == null) {
                LocalTime end = now.plusMinutes(1);
                valueOps.set(endTimeKey, end.format(DateTimeFormatter.ISO_LOCAL_TIME));
                return end;
            } else {
                // 이미 설정된 종료 시간이 있으면 해당 시간 반환
                return LocalTime.parse(existingEndTime, DateTimeFormatter.ISO_LOCAL_TIME);
            }
        }

        return null;
    }

    @Override
    public void gameVoteCount(int roomId, int gameSelect) {
        if(gameSelect == 0) {
            String countKey = "chatRoom:" + roomId + ":voteResultA";
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            String countStr = valueOps.get(countKey);
            int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
            count++; // 횟수 증가
            valueOps.set(countKey, String.valueOf(count));

            System.out.println("A" + count);
        }
        else {
            String countKey = "chatRoom:" + roomId + ":voteResultB";
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            String countStr = valueOps.get(countKey);
            int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
            count++; // 횟수 증가
            valueOps.set(countKey, String.valueOf(count));

            System.out.println("B" + count);
        }
    }

    @Override
    public boolean getZeroCount(int roomId) {
        String countKey = "chatRoom:" + roomId + ":zeroCount";

        int count = redisTemplate.opsForValue().increment(countKey).intValue();

        String key = "chatRoom:" + roomId + ":members";

        if(count==redisTemplate.opsForSet().size(key)) {
            return true;
        }
        else return false;
    }

    @Override
    public String getVoteResult(int roomId) {
        String aKey = "chatRoom:" + roomId + ":voteResultA";
        ValueOperations<String, String> A_valueOps = redisTemplate.opsForValue();
        String aStr = A_valueOps.get(aKey);
        int answerA = (aStr != null) ? Integer.parseInt(aStr) : 0;

        String bKey = "chatRoom:" + roomId + ":voteResultB";
        ValueOperations<String, String> B_valueOps = redisTemplate.opsForValue();
        String bStr = B_valueOps.get(bKey);
        int answerB = (bStr != null) ? Integer.parseInt(bStr) : 0;

        System.out.println("answerA : "+answerA);
        System.out.println("answerB : "+answerB);
        if(answerA > answerB) return "answerA";
        else if (answerA < answerB) return "answerB";
        else return "draw";
    }

    @Override
    public void userLeft(int roomId, AnonymousDTO anonymous) {
        String key = "chatRoom:" + roomId + ":members";
        String value = anonymousService.serializeAnonymousDTO(anonymous); // AnonymousDTO 객체를 직렬화하는 메소드 필요

        // Redis Set에서 회원 정보 제거
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public Long getConnectedUserCount(int roomId) {
        String key = "chatRoom:" + roomId + ":members";
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Set<AnonymousDTO> getAllMembersInRoom(int roomId) {
        String key = "chatRoom:" + roomId + ":members";
        Set<String> members = redisTemplate.opsForSet().members(key); // Redis에서 회원 정보 가져오기

        Set<AnonymousDTO> memberDTOs = new HashSet<>();
        for (String member : members) {
            AnonymousDTO dto = anonymousService.deserializeToAnonymousDTO(member); // 문자열을 AnonymousDTO 객체로 역직렬화
            memberDTOs.add(dto);
        }
        return memberDTOs;
    }

    @Override
    public Set<AnonymousDTO> setRole(int roomId) {
        String key = "chatRoom:" + roomId + ":members";
        Set<String> members = redisTemplate.opsForSet().members(key); // Redis에서 회원 정보 가져오기

        Set<AnonymousDTO> memberDTOs = new HashSet<>();
        for (String member : members) {
            AnonymousDTO dto = anonymousService.deserializeToAnonymousDTO(member); // 문자열을 AnonymousDTO 객체로 역직렬화
            memberDTOs.add(dto);
        }

        List<AnonymousDTO> list = new ArrayList<>(memberDTOs);

        if(list.size() % 3 == 0) {
            for(int i = 0; i < list.size()/3; i++) {
                list.get(i*3).setRole("judge");
                list.get((i*3)+1).setRole("answerA");
                list.get((i*3)+2).setRole("answerB");
            }
        }
        else if(list.size() % 3 == 1) {
            for(int i = 0; i < list.size()/3; i++) {
                list.get(i*3).setRole("judge");
                list.get((i*3)+1).setRole("answerA");
                list.get((i*3)+2).setRole("answerB");
            }
            list.get(list.size()-1).setRole("judge");
        }
        else {
            for(int i = 0; i < list.size()/3; i++) {
                list.get(i*3).setRole("judge");
                list.get((i*3)+1).setRole("answerA");
                list.get((i*3)+2).setRole("answerB");
            }
            list.get(list.size()-2).setRole("answerA");
            list.get(list.size()-1).setRole("answerB");
        }

        redisTemplate.delete(key); // 기존 회원 정보 삭제
        for(AnonymousDTO dto : list) {
            String value = anonymousService.serializeAnonymousDTO(dto); // AnonymousDTO 객체를 직렬화
            redisTemplate.opsForSet().add(key, value); // 새로운 정보 추가
        }

        // 새로운 Set 반환
        return new HashSet<>(list);
    }

    @Override
    public void saveResult(int roomId, String result) {
        // 1서버로 이동해서 저장
        String key = "chatRoom:" + roomId + ":members";
        Set<String> members = redisTemplate.opsForSet().members(key); // Redis에서 회원 정보 가져오기

        Set<AnonymousDTO> memberDTOs = new HashSet<>();
        for (String member : members) {
            AnonymousDTO dto = anonymousService.deserializeToAnonymousDTO(member); // 문자열을 AnonymousDTO 객체로 역직렬화
            memberDTOs.add(dto);
        }

        if(result.equals("answerA")) {
            for (AnonymousDTO user : memberDTOs) {
                if(user.getRole().equals("answerA")) {
                    String memberId = anonymousService.findMemberId(roomId, Integer.toString(user.getAnonymousId()));
                    System.out.println(memberId);
                    proxy.saveResult(memberId,"win");
                }
                else if(user.getRole().equals("answerB")) {
                    String memberId = anonymousService.findMemberId(roomId, Integer.toString(user.getAnonymousId()));
                    System.out.println(memberId);
                    proxy.saveResult(memberId,"lose");
                }
                anonymousService.deleteAnonymous(roomId,user.getAnonymousId());
            }
        }
        else if (result.equals("answerB")) {
            for (AnonymousDTO user : memberDTOs) {
                if(user.getRole().equals("answerB")) {
                    String memberId = anonymousService.findMemberId(roomId, Integer.toString(user.getAnonymousId()));
                    System.out.println(memberId);
                    proxy.saveResult(memberId,"win");
                }
                else if(user.getRole().equals("answerA")) {
                    String memberId = anonymousService.findMemberId(roomId, Integer.toString(user.getAnonymousId()));
                    System.out.println(memberId);
                    proxy.saveResult(memberId,"lose");
                }
                anonymousService.deleteAnonymous(roomId, user.getAnonymousId());
            }
        }
        else {
            for (AnonymousDTO user : memberDTOs) {
                if(user.getRole().equals("answerA") || user.getRole().equals("answerB")) {

                    String memberId = anonymousService.findMemberId(roomId, Integer.toString(user.getAnonymousId()));
                    proxy.saveResult(memberId,"draw");
                }
                anonymousService.deleteAnonymous(roomId, user.getAnonymousId());
            }
        }
    }

    @Override
    public void deleteRoom(int roomId) {
        String pattern = "chatRoom:" + roomId + "*";
        List<String> keysToDelete = new ArrayList<>();

        redisTemplate.execute((connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).build())) {
                cursor.forEachRemaining(key -> keysToDelete.add(new String(key)));
                return null;
            }
        }, true); // 추가적으로, 사용하는 RedisConnection의 타입에 따라 Boolean 값을 설정할 수 있습니다.

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }
}
