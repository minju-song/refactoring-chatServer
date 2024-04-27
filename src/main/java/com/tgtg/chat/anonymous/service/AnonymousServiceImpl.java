package com.tgtg.chat.anonymous.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgtg.chat.anonymous.domain.AnonymousRepository;
import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnonymousServiceImpl implements AnonymousService{

    @Autowired
    AnonymousRepository anonymousRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public AnonymousDTO getAnonymous(int count) {
        return anonymousRepository.findByAnonymousId(count).toResponseDto();
    }

    @Override
    public AnonymousDTO createAnonymous(int roomId, String memberId) {
        String key = "chatRoom:"+roomId+":anonymousCount";

        int count = redisTemplate.opsForValue().increment(key).intValue();

        int anonyId = Integer.parseInt(String.format("%03d%02d", roomId, count));


        //익명객체 생성
        AnonymousDTO anonymous = getAnonymous(count);
        anonymous.setAnonymousId(anonyId);
        anonymous.setRoomId(roomId);

        String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";
        redisTemplate.opsForHash().put(mappingKey, Integer.toString(anonyId), memberId);

        return anonymous;
    }

    @Override
    public void deleteAnonymous(int roomId, int anonymousId) {
        String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";

        // 익명 ID를 사용해 해당 익명 사용자의 매핑을 삭제
        redisTemplate.opsForHash().delete(mappingKey, Integer.toString(anonymousId));
    }

    @Override
    public void deleteCount(int roomId) {
        String key = "chatRoom:"+roomId+":anonymousCount";

        redisTemplate.delete(key);
    }

    @Override
    public String findMemberId(int roomId, String anonymousId) {
        String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";

        // 익명 ID에 해당하는 회원 ID를 찾아 반환
        return (String) redisTemplate.opsForHash().get(mappingKey, anonymousId);
    }

    @Override
    public String serializeAnonymousDTO(AnonymousDTO anonymousDTO) {
        try {
            return objectMapper.writeValueAsString(anonymousDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AnonymousDTO deserializeToAnonymousDTO(String json) {
        try {
            return objectMapper.readValue(json, AnonymousDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
