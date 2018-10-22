package com.example.demo.service;

import com.example.demo.DistributedLock;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JredisLockService {

    private static JedisPool pool=null;
    private DistributedLock lock=new DistributedLock(pool);
    int n=500;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "192.168.254.128", 6500, 3000);
    }

    public void seckill(){
        String identifier=lock.lockWithTimeOut("resource",5000,1000);
        System.out.println(Thread.currentThread().getName()+"get lock");
        System.out.println(--n);
        lock.releaseLock("resource",identifier);
    }
}
