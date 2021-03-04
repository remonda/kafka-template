package cn.focusmedia.consumer.tools;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;
import cn.focusmedia.consumer.constant.ConsumerConstant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();
    private static String ADDR = configur.getStringValue(ConsumerConstant.REDIS_SERVERES);//服务器IP
    private static Integer PORT = configur.getIntegerValue(ConsumerConstant.REDIS_SERVER_PORT);//端口
    private static Integer MAX_TOTAL = configur.getIntegerValue(ConsumerConstant.REDIS_MAX_TOTAL);
    private static Integer MAX_IDLE = configur.getIntegerValue(ConsumerConstant.REDIS_MAX_IDLE);
    private static Integer MAX_WAIT_MILLIS = configur.getIntegerValue(ConsumerConstant.REDIS_MAX_WAIT_MILLIS);
    private static Integer TIMEOUT = configur.getIntegerValue(ConsumerConstant.REDIS_TIMEOUT);
    private static Boolean TEST_ON_BORROW = configur.getBooleanValue(ConsumerConstant.REDIS_TEST_ON_BORROW);
    private static JedisPool jedisPool = null;

    /**
     * 静态块，初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis jedis = jedisPool.getResource();
                return jedis;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void returnResource(final Jedis jedis) {
        //方法参数被声明为final，表示它是只读的。
        if (jedis != null) {
            jedisPool.returnResource(jedis);
            //jedis.close()取代jedisPool.returnResource(jedis)方法将3.0版本开始
            //jedis.close();
        }
    }
}
