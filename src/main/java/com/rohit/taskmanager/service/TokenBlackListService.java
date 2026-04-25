package com.rohit.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlackListService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX="blacklist:";

    public void blacklist(String token,long expirationTime){
        String key=TOKEN_PREFIX+token;
        redisTemplate.opsForValue().set(key,"Blacklisted", Duration.ofMillis(expirationTime));
    }

    public boolean isBlacklisted(String token){
        String key=TOKEN_PREFIX+token;
        return redisTemplate.hasKey(key);
    }

}
