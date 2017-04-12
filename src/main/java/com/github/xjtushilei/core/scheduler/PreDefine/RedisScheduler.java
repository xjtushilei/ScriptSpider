package com.github.xjtushilei.core.scheduler.PreDefine;

import com.github.xjtushilei.core.scheduler.Scheduler;
import com.github.xjtushilei.model.UrlSeed;
import com.github.xjtushilei.utils.RedisUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by shilei on 2017/4/12.
 */
public class RedisScheduler implements Scheduler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static JedisPool jedisPool;
    private final static String Prefix_Set = "ScriptSpider.set";
    private final static String Prefix_Queue_high = "ScriptSpider.queue.high";
    private final static String Prefix_Queue_low = "ScriptSpider.queue.low";
    private final static String Prefix_Queue_default = "ScriptSpider.queue.default";

    /**
     * redis调度器进行初始化
     *
     * @param ip
     * @param port
     * @param MaxActive 最多可以同时并发连接！建议设置为5000以上即可！
     */
    public RedisScheduler(String ip, int port, int MaxActive) {
        jedisPool = RedisUtils.getJedisPool(ip, port, MaxActive);
        //测试连接池配置正确与否
        try {
            jedisPool.getResource();
        } catch (Exception e) {
            throw new NullPointerException("redis初始化失败！请检查参数！");
        }
    }

    /**
     * 使用默认ip（127.0.0.1），port（6379） 等的redis调度器
     */
    public RedisScheduler() {
        jedisPool = RedisUtils.getJedisPool();
        //测试连接池配置正确与否
        try {
            jedisPool.getResource();
        } catch (Exception e) {
            throw new NullPointerException("redis初始化失败！请检查参数！");
        }
    }

    /**
     * 写进去url种子
     *
     * @param urlSeed 种子
     */
    @Override
    public void push(UrlSeed urlSeed) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (urlSeed.getUrl() == null
                    || urlSeed.getUrl().trim().equals("")
                    || urlSeed.getUrl().trim().equals("#")
                    || urlSeed.getUrl().trim().toLowerCase().contains("javascript:"))
                return;
            if (jedis.sismember(Prefix_Set, urlSeed.getUrl())) {
                //            logger.info("UrlSeed重复:" + urlSeed.getUrl());
            } else {
                jedis.sadd(Prefix_Set, urlSeed.getUrl());
                Gson gson = new Gson();
                String urlSeedJson = gson.toJson(urlSeed);
                if (urlSeed.getPriority() == 5l) {
                    jedis.rpush(Prefix_Queue_default, urlSeedJson);
                } else if (urlSeed.getPriority() > 5l) {
                    jedis.rpush(Prefix_Queue_high, urlSeedJson);
                } else {
                    jedis.rpush(Prefix_Queue_low, urlSeedJson);
                }
            }
        } catch (Exception e) {
            logger.error("连接获取失败!", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * poll种子
     *
     * @return UrlSeed
     */
    @Override
    public UrlSeed poll() {
        Gson gson = new Gson();
        Jedis jedis = null;
        String urlSeedJson = null;
        try {
            jedis = jedisPool.getResource();
            //依次取三个队列，优先级高的被先取！
            urlSeedJson = jedis.lpop(Prefix_Queue_high);
            if (urlSeedJson != null) {
                return gson.fromJson(urlSeedJson, UrlSeed.class);
            }
            urlSeedJson = jedis.lpop(Prefix_Queue_default);
            if (urlSeedJson != null) {
                return gson.fromJson(urlSeedJson, UrlSeed.class);
            }
            urlSeedJson = jedis.lpop(Prefix_Queue_low);
            if (urlSeedJson != null) {
                return gson.fromJson(urlSeedJson, UrlSeed.class);
            }
        } catch (Exception e) {
            logger.warn("连接获取失败!", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return null;
    }

}
