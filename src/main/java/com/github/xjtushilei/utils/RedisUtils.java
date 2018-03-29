package com.github.xjtushilei.utils;


/**
 * Created by shilei on 2017/4/10.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);


    //Redis服务器IP
    private static String ADDR = "127.0.0.1";
    //Redis的端口号
    private static int PORT = 6379;
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 2048;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    /**
     * 初始化Redis连接池
     * @param ip    redis ip
     * @param port  port
     * @param Max_Active    最大连接数
     * @return jedis连接池
     */
    public static JedisPool getJedisPool(String ip, int port, int Max_Active) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Max_Active);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            JedisPool jedisPool = new JedisPool(config, ip, port);
            return jedisPool;
        } catch (Exception e) {
            logger.error("redis初始化失败！请检查参数！", e);
            return null;
        }
    }

    /**
     * 默认配置redis pool
     * @return JedisPool
     */
    public static JedisPool getJedisPool() {
        return getJedisPool(ADDR, PORT, MAX_ACTIVE);
    }

}