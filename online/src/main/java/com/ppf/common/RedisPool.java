package com.ppf.common;



import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
//redis连接池
public class RedisPool {

  private JedisPool jedisPool;


   public  RedisPool(){}
    public RedisPool(Integer maxTotal,Integer maxIdle,Integer minIdle,
                     boolean testborrow,
                     boolean testreturn,
                     String ip,Integer port,String password,Integer timeout){
       //JedisPoolConfig是jedispool的配置，而Jedis是redis的java版本的客户端实现。
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);//最大连接数
        jedisPoolConfig.setMaxIdle(maxIdle);//最大空闲数
        jedisPoolConfig.setMinIdle(minIdle);//最小空闲连接数
        jedisPoolConfig.setTestOnBorrow(testborrow);//true:当从连接池获取连接时，检测连接是否有效
        jedisPoolConfig.setTestOnReturn(testreturn);//true:当将连接放回到连接池时，检测连接是否有效
        jedisPoolConfig.setBlockWhenExhausted(true);//true:当连接池的连接耗尽时，会等待知道超时。
//GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password
        jedisPool=new JedisPool(jedisPoolConfig,ip,port,timeout,password);

    }

    //获取jedis--就可以对数据库操作了
    public Jedis getJedis(){
       return jedisPool.getResource();
    }


    //还回jedispool时
    public  void  returnJedis(Jedis jedis){
        jedisPool.returnResource(jedis);
    }

    public  void  returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }


}
