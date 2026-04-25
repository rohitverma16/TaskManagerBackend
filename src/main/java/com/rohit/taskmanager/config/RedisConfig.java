package com.rohit.taskmanager.config;

import com.rohit.taskmanager.dto.task.TaskPageResponse;
import com.rohit.taskmanager.dto.user.UserResponseDto;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // ─────────────────────────────────────────────
    // 1. Shared JsonMapper (Jackson 3)
    // ─────────────────────────────────────────────

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    // ─────────────────────────────────────────────
    // 2. One typed serializer per cached DTO
    //    Add more here as your app grows
    // ─────────────────────────────────────────────

    @Bean
    public JacksonJsonRedisSerializer<TaskPageResponse> taskPageSerializer(JsonMapper jsonMapper) {
        return new JacksonJsonRedisSerializer<>(jsonMapper, TaskPageResponse.class);
    }

    @Bean
    public JacksonJsonRedisSerializer<UserResponseDto> userSerializer(JsonMapper jsonMapper) {
        return new JacksonJsonRedisSerializer<>(jsonMapper, UserResponseDto.class);
    }

    // ─────────────────────────────────────────────
    // 3. Reusable helper to build RedisCacheConfiguration
    // ─────────────────────────────────────────────

    private RedisCacheConfiguration cacheConfig(
            Duration ttl,
            JacksonJsonRedisSerializer<?> serializer) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer));
    }

    // ─────────────────────────────────────────────
    // 4. CacheManager — registers all named caches
    //    Add a .withCacheConfiguration() per cache
    // ─────────────────────────────────────────────

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            JacksonJsonRedisSerializer<TaskPageResponse> taskPageSerializer,
            JacksonJsonRedisSerializer<UserResponseDto> userSerializer) {

        return RedisCacheManager.builder(connectionFactory)

                // "tasks" cache → TaskPageResponse, TTL 10 mins
                .withCacheConfiguration("tasks",
                        cacheConfig(Duration.ofMinutes(10), taskPageSerializer))

                // "users" cache → UserResponseDto, TTL 1 hour
                .withCacheConfiguration("users",
                        cacheConfig(Duration.ofHours(1), userSerializer))

                // fallback for any cache not explicitly configured above
                .cacheDefaults(
                        cacheConfig(Duration.ofMinutes(30), taskPageSerializer))

                .build();
    }

    // ─────────────────────────────────────────────
    // 5. RedisTemplate for direct Redis access
    //    (outside of @Cacheable, if ever needed)
    // ─────────────────────────────────────────────

    @Bean
    public RedisTemplate<String, TaskPageResponse> taskRedisTemplate(
            RedisConnectionFactory connectionFactory,
            JacksonJsonRedisSerializer<TaskPageResponse> taskPageSerializer) {

        RedisTemplate<String, TaskPageResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(taskPageSerializer);
        template.setHashValueSerializer(taskPageSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, UserResponseDto> userRedisTemplate(
            RedisConnectionFactory connectionFactory,
            JacksonJsonRedisSerializer<UserResponseDto> userSerializer) {

        RedisTemplate<String, UserResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(userSerializer);
        template.setHashValueSerializer(userSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}