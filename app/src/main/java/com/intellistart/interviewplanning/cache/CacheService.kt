package com.intellistart.interviewplanning.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CacheService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    @Value("\${jwt.caching}") private val jwtValidity: Long
) {

    fun getFromCache(key: String): Mono<String> {
        return reactiveRedisTemplate.opsForValue().get(key)
    }

    fun setInCache(key: String, value: String): Mono<Boolean> {
        return reactiveRedisTemplate.opsForValue().set(key, value).flatMap {
            reactiveRedisTemplate.expire(key, Duration.ofSeconds(jwtValidity))
        }
    }
}
