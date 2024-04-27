package com.tgtg.chat.access.ready;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReadyUserServiceImpl implements ReadyUserService{

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Map<Integer, Integer> readyUser = new HashMap<>();

    @Override
    public void readyUser(int roomId) {
        String key = "chatRoom:" + roomId + ":readyUser";
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void unreadyUser(int roomId) {
        String key = "chatRoom:" + roomId + ":readyUser";
        redisTemplate.opsForValue().decrement(key);
    }

    @Override
    public int getReady(int roomId) {
        String key = "chatRoom:" + roomId + ":readyUser";
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    @Override
    public void deleteReadyUser(int roomId) {
        String key = "chatRoom:" + roomId + ":readyUser";
        redisTemplate.delete(key);
    }
}
