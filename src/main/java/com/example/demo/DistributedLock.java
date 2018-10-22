package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.UUID;

/*
   分布式锁
 */
public class DistributedLock {
    private  final JedisPool jedisPool;


    public  DistributedLock(JedisPool jedisPool){
        this.jedisPool=jedisPool;
    }

    /*
    加锁
     */

    public String lockWithTimeOut(String lockName,long acquireTimeout,long timeout){
        Jedis conn=null;
        String retIdentifier=null;
        try{
            //get connection
               conn =jedisPool.getResource();

               String identifier= UUID.randomUUID().toString();
               //build lockKey
               String lockKey="lock"+lockName;
               //超时时间。超过时间
               int lockExpire=(int)(timeout/1000);

               //获得锁的超时时间 超过这个时间则放弃获得锁
             long end =System.currentTimeMillis()+acquireTimeout;
             while(System.currentTimeMillis()<end){
                 //setnx的含义就是SET if Not Exists，其主要有两个参数 setnx(key, value)。
                 //该方法是原子的，如果key不存在，则设置当前key成功，返回1；如果当前key已经存在，则设置当前key失败，返回0
                 if(conn.setnx(lockKey,identifier)==1){
                     conn.expire(lockKey,lockExpire);
                     //返回value值，用于释放锁时间确认
                     retIdentifier=identifier;
                     return retIdentifier;
                 }
                 //返回-1代表key没有设置超时时间，为key设置一个
                 if(conn.ttl(lockKey)==-1){
                     conn.expire(lockKey,lockExpire);
                 }
                 try{
                     Thread.sleep(10);
                 }catch (InterruptedException e){
                     Thread.currentThread().interrupt();
                 }
             }
        }catch (JedisException e){
            e.printStackTrace();
        }
        finally {
            if(conn!=null){
                conn.close();
            }
        }
        return retIdentifier;
    }
//
     /*
     释放锁
      */
     public  boolean releaseLock(String lockName,String identifier){
         Jedis conn=null;
         String lockKey="lock"+lockName;
         boolean retFlag =false;
         try{
             conn=jedisPool.getResource();
             while (true){
                 //监视lock，准备开始事务
                 if(identifier.equals(conn.get(lockKey))){
                     Transaction transaction=conn.multi();
                     transaction.del(lockKey);
                     List<Object> results=transaction.exec();
                     if(results==null){
                         continue;
                     }
                     retFlag=true;
                 }
             }
         }catch (JedisException e){
             e.printStackTrace();
         }
         finally {
             if(conn!=null){
                 conn.close();
             }
         }
      return retFlag;
     }

}
