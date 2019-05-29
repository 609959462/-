package com.ppf.utils;

import com.ppf.common.RedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

public class RedisApi {


    @Autowired
    RedisPool redisPool;

    /**
     * 字符串
     * */

    public String  set(String key,String value){

        Jedis jedis=redisPool.getJedis();
        String result=null;
        try{
            result=jedis.set(key, value);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
     return result;
    }

    public String  get(String key){

        Jedis jedis=redisPool.getJedis();
        String result=null;
        try{
            result=jedis.get(key);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }

    public String  setex(String key,int timeout,String value){

        Jedis jedis=redisPool.getJedis();
        String result=null;
        try{
            result=jedis.setex(key,timeout, value);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }


    public Long  expire(String key,int timeout){

        Jedis jedis=redisPool.getJedis();
        Long result=null;
        try{
            result=jedis.expire(key,timeout);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }

}
