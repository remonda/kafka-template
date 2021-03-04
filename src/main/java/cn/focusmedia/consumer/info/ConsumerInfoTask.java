package cn.focusmedia.consumer.info;

import cn.focusmedia.consumer.base.Task;
import cn.focusmedia.consumer.tools.ConnectionUtil;
import cn.focusmedia.consumer.tools.RedisPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;

/**
 * ClassName：ConsumerInfoTask <br/>
 * Author：QGX <br/>
 * Date：2019/11/4 21:25 <br/>
 * Version：1.0
 */
public class ConsumerInfoTask extends Task {
    private final static Logger LOGGER = Logger.getLogger(ConsumerInfoTask.class);
    private final static Logger STATICINFO = Logger.getLogger("staticInfo");
    private Jedis jedis = RedisPool.getJedis();
    private DataSource dataSource = ConnectionUtil.getDataSource();

    public ConsumerInfoTask(String topic, String groupId) {
        super(topic, groupId);
    }

    private Long redisInfoBeatNums = 0l;
    private Long updateInfoNums = 0l;

    @Override
    public void doWork(ConsumerRecord record) {
        if (record != null) {
//            logValue(record);
            JSONObject jsonObject = JSON.parseObject(record.value().toString());
            String eventName = jsonObject.getString("eventName");
            if (eventName != null && eventName.equals("deviceInfo")) {
                //处理动态属性
                Info(jsonObject);
            }
        }
    }

//    @Override
//    public void preStart() throws Exception {
//
//    }
//
//    @Override
//    public void preEnd() throws Exception {
//
//    }
//
//    @Override
//    public void logValue(ConsumerRecord record) {
//        JSONObject json = JSON.parseObject("{}");
//        json.put("logSystemTime", System.currentTimeMillis());
//        json.put("Topic", record.topic());
//        json.put("partition", record.partition());
//        json.put("offset", record.offset());
//        json.put("value", record.value().toString());
//        STATICINFO.info(json.toString());
//    }

    private void Info(JSONObject jsonObject) {
        String deviceName = jsonObject.getString("deviceName");//last_inner_SN
        jedis.select(1);
        String flag = jedis.get(deviceName);
        if (flag == null) {//表示第一次存储
            ++redisInfoBeatNums;
            LOGGER.info("插入redis(1)库deviceName：" + deviceName + ";已插入：" + redisInfoBeatNums);
            jedis.select(1);
            jedis.set(deviceName, jsonObject.toString());
        } else {
            //先取出新的 进行比较
            ++updateInfoNums;
            LOGGER.info("更新Info,当前deviceName：" + deviceName + ";已跟新(插入或更新、含本次)：" + updateInfoNums);
            jedis.select(1);
            JSONObject jsonValue = JSON.parseObject(jedis.get(deviceName));
            Long oldEventTime = Long.valueOf(jsonValue.getString("eventTime"));
            Long newEventTime = Long.valueOf(jsonObject.getString("eventTime"));
            if (oldEventTime < newEventTime) {
                jedis.select(1);
                jedis.set(deviceName, jsonObject.toString());//更新
            }
        }
    }
}
