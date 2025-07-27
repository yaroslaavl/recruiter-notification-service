package org.yaroslaavl.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.yaroslaavl.notificationservice.service.RedisService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void setToken(String key, String value, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @Override
    public String hasToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteToken(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }
}
