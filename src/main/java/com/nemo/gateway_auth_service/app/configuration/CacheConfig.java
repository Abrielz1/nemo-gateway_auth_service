package com.nemo.gateway_auth_service.app.configuration;

import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJacksonJsonRedisSerializer(objectMapper)))
                .entryTtl(Duration.ofHours(3))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}