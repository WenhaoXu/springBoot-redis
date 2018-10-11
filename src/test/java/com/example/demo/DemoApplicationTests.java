package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.example.demo.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    private  static  final Logger logger=LoggerFactory.getLogger(DemoApplication.class);


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void contextLoads() {
        User user=new User("wahaha",3);
        redisTemplate.opsForValue().set(user.getName(),JSON.toJSONString(user));

    }

    @Test
    public void testMap(){
        Map<String,String> map=new HashMap<String,String>();
        map.put("key1","value1");
        map.put("key2","value2");
        map.put("key3","value3");
        map.put("key4","value4");
        map.put("key5","value5");
        redisTemplate.opsForHash().putAll("map1",map);
        Map<String,String> resultMap = redisTemplate.opsForHash().entries("map1");
        List<String> resultMapList = redisTemplate.opsForHash().values("map1");
        Set<String> resultMapSet = redisTemplate.opsForHash().keys("map1");
        String value = (String)redisTemplate.opsForHash().get("map1","key1");

        logger.info("resultMap:"+resultMap);
        logger.info("resultMapList:"+resultMapList);
        logger.info("resultMapSet:"+resultMapSet);
        logger.info("value:"+value);

        /**
         * 输出结果
         * resultMap:{key2=value2, key5=value5, key4=value4, key1=value1, key3=value3}
         * resultMapList:[value1, value2, value5, value3, value4]
         * resultMapSet:[key1, key2, key5, key3, key4]
         * value:value1
         */

    }


}
