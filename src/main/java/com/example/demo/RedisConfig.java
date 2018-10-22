package com.example.demo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableCaching

public class RedisConfig extends JCacheConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Value("${spring.redis.host}")
    public String host;
    @Value("${spring.redis.password}")
    public String password;
    @Value("${spring.redis.port}")
    public int port;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Bean
    @Override
    public KeyGenerator keyGenerator(){
        return (target,method,params)->{
            StringBuilder sb=new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(":" + String.valueOf(obj));
            }
            String rsToUse = String.valueOf(sb);
//            logger.info("自动生成Redis Key -> [{}]", rsToUse);
            return rsToUse;
        };
    }

    @Bean
    @Override
    public CacheManager cacheManager(){
        RedisCacheManager.RedisCacheManagerBuilder builder=RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory);
        return  builder.build();
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        RedisTemplate<String,Object>redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        RedisSerializer serializer=new StringRedisSerializer() ;
        //key value 序列化
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //hashkey value 序列化
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();



        return redisTemplate;
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler(){
        logger.info("init ->[{}]","Redis cacheError");
        CacheErrorHandler cacheErrorHandler=new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logger.error("redis occur handleCacheGetError:key->[{}]",key,exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                logger.error("redis occur handleCachePutError:key->[{}];value->[{}]",key,value,exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logger.error("redis occur handleCacheEvictError:key->[{}]",key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                logger.error("Redis occur handleCacheClearError：", exception);
            }
        };
        return cacheErrorHandler;
    }
        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            logger.info("Create JedisConnectionFactory successful");
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName("192.168.254.128");
            factory.setPort(6500);
            factory.setTimeout(10000);
            factory.setPassword(password);
            return factory;
        }
    @Bean
    public JedisPool redisPoolFactory() {
        logger.info("JedisPool init successful，host -> [{}]；port -> [{}]", "192.168.254.128", 6500);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(-1);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "192.168.254.128", 6500, 100, password);
        return jedisPool;
    }

}
