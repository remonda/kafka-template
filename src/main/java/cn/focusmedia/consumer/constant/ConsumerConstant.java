package cn.focusmedia.consumer.constant;

/**
 * 用户自定义 常量
 */
public interface ConsumerConstant {
    //kafka consumer common
    public final static String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
    public final static String CONSUMER_NUMS = "consumer.nums";
    public final static String GROUP_ID = "group.id";
    public final static String AUTO_OFFSET_RESET = "auto.offset.reset";
    public final static String KAFKA_POLL_MS = "kafka.poll.ms";
    public final static String MAX_POLL_RECORDS = "max.poll.records";
    public final static String MAX_PARTITION_FETCH_BYTES="max.partition.fetch.bytes";
    //redis
    public final static String REDIS_SERVERES = "redis.servers";
    public final static String REDIS_SERVER_PORT = "redis.servers.port";
    public final static String REDIS_MAX_TOTAL = "redis.max.total";
    //控制一个pool最多有多少个状态为idle(空闲)的jedis实例，默认值是8
    public final static String REDIS_MAX_IDLE = "redis.max.idle";
    //等待可用连接的最大时间，单位是毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
    public final static String REDIS_MAX_WAIT_MILLIS = "redis.max.wait.millis";
    public final static String REDIS_TIMEOUT = "redis.timeout";
    //在borrow(用)一个jedis实例时，是否提前进行validate(验证)操作；如果为true，则得到的jedis实例均是可用的
    public final static String REDIS_TEST_ON_BORROW = "redis.test.on.borrow";

    //c3p0配置
    public final static String C3P0_DRIVER_CLASS = "c3p0.driver.class";
    public final static String C3P0_JDBC_URL = "c3p0.jdbc.url";
    public final static String C3P0_CHECK_TIMEOUT = "c3p0.check.timeout";
    public final static String C3P0_INITIAL_POOL_SIZE = "c3p0.initial.pool.size";
    public final static String C3P0_MAX_IDLETIME = "c3p0.max.idletime";
    public final static String C3P0_MAX_POOL_SIZE = "c3p0.max.pool.Size";
    public final static String C3P0_MIN_POOL_SIZE = "c3p0.min.pool.Size";
    public final static String C3P0_USER = "c3p0.user";
    public final static String C3P0_PASSWORD = "c3p0.password";
    public final static String C3P0_MAX_STATEMENT = "c3p0.max.statements";
    public final static String C3P0_IDLE_TEST_PERIOD = "c3p0.idle.test.period";

    //topic configuration
    public final static String CONSUMER_TOPIC_DOWNLOAD = "consumer.topic.download";
    public final static String CONSUMER_TOPIC_EVENT = "consumer.topic.event";
    public final static String CONSUMER_TOPIC_HEARTBEAT = "consumer.topic.heartbeat";
    public final static String CONSUMER_TOPIC_IMPORTS = "consumer.topic.imports";
    public final static String CONSUMER_TOPIC_INFO = "consumer.topic.info";
    public final static String CONSUMER_TOPIC_PLAY_LOG = "consumer.topic.play.log";
    public final static String CONSUMER_TOPIC_PUBLISH = "consumer.topic.publish";
    public final static String CONSUMER_TOPIC_STATUS_CHANGE = "consumer.topic.status.change";

}
