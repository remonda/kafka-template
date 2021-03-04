package cn.focusmedia.consumer.kafka;

import static org.junit.Assert.assertTrue;

import cn.focusmedia.consumer.tools.RedisPool;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;


/**
 * Unit test for simple App.
 */
public class RedisTest {
    private static RedisServer redisServer;
    @BeforeClass
    public static void beforeClass() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
        System.out.println("Embedded redis started with port: 6379");
    }

    @Test
    public void RedisUtilTest() {
        System.out.println("start redis util test");
        String map= "test";
        String key = "test_key";
        String value = "test_value";
        Jedis jedis = RedisPool.getJedis();
        jedis.set(key, value);
        System.out.printf("add key into redis map, map:%s, key: %s, value: %s", map, key, value);
        String getValue = jedis.get(key);
        System.out.printf("get value from redis map, map: %s, key: %s, value: %s", map, key, value);
        Assert.assertEquals(value, getValue);

        String key1 = "new_key";
        String value1 = "new_value";
        String getValue1 = null;
        try {
            getValue1 = jedis.get(key1);
            System.out.printf("get value from redis map, map: %s, key:  %s, value:  %s", map, key1, getValue1);
        } catch (NullPointerException ex) {
            System.out.printf("get key from map failed, map: %s, key:%s", map, key);
        }

        Assert.assertEquals(getValue1, null);


    }

    @AfterClass
    public static void afterClass() {
        redisServer.stop();
        System.out.println("Embedded redis stop!");
    }
}
