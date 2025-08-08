package com.interestingdomain.hotelrezzervo.hotelservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String lockKey, Long lockValue, long timeout) {
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue.toString(), Duration.ofMillis(timeout));
        return Boolean.TRUE.equals(locked);
    }

    public void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
