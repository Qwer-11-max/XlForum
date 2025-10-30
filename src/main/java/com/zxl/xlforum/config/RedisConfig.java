package com.zxl.xlforum.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching // 开启缓存支持（必须添加，否则缓存注解不生效）
public class RedisConfig {

    /**
     * 自定义RedisTemplate（用于手动操作Redis）
     * 序列化方式：key用String，value用JSON
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 1. 配置JSON序列化器（使用构造函数传入ObjectMapper）
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        // 关键：通过构造函数传入mapper和目标类型
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        // 2. 配置String序列化器（key用String）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 3. 设置序列化方式
        template.setKeySerializer(stringSerializer);       // key：String
        template.setValueSerializer(jsonSerializer);       // value：JSON
        template.setHashKeySerializer(stringSerializer);   // hash key：String
        template.setHashValueSerializer(jsonSerializer);   // hash value：JSON

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器（用于@Cacheable等注解）
     * 支持自定义不同缓存的过期时间
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 默认缓存配置（未指定过期时间的缓存使用此配置）
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认过期时间30分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues(); // 不缓存null值（避免缓存穿透）

        // 自定义缓存配置（针对不同缓存名称设置不同过期时间）
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("accountCache", defaultConfig.entryTtl(Duration.ofHours(1))); // 用户缓存过期时间1小时

        // 创建缓存管理器
        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig) // 默认配置
                .withInitialCacheConfigurations(configMap) // 自定义配置
                .build();
    }
}