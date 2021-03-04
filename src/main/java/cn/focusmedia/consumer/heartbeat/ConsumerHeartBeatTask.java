package cn.focusmedia.consumer.heartbeat;

import cn.focusmedia.consumer.base.Task;
import cn.focusmedia.consumer.tools.ConnectionUtil;
import cn.focusmedia.consumer.tools.DateUtil;
import cn.focusmedia.consumer.tools.NumberUtil;
import cn.focusmedia.consumer.tools.RedisPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsumerHeartBeatTask extends Task {
    private final static Logger HEARTBEAT = Logger.getLogger("heartbeat");
    private final static Logger LOGGER = Logger.getLogger(ConsumerHeartBeatTask.class);
    private Jedis jedis = RedisPool.getJedis();
    private DataSource dataSource = ConnectionUtil.getDataSource();
    private Long consumerHeartBeatNums = 0l;//消费个数
    private Long redisHeartBeatNums = 0l;//插入redis个数
    private Long updateHeartBeatNums = 0l;//跟新SQLserver次数(插入和更新)
    private Connection connection = null;
    private PreparedStatement pstm = null;
    public ConsumerHeartBeatTask(String topic, String groupId) {
        super(topic, groupId);
    }

    @Override
    public void doWork(ConsumerRecord record) {
        if (record != null) {
//            logValue(record);
            JSONObject jsonObject = JSON.parseObject(record.value().toString());
            System.out.println(jsonObject.toString());
//            String eventName = jsonObject.getString("eventName");
//            if (StringUtils.isNotBlank(eventName) && StringUtils.equals(eventName, "heartbeat")) {
//                //处理动态属性
//                heartBeat(jsonObject);
//            }
        }
    }

//    /**
//     * 批次开始前的任务
//     */
//    @Override
//    public void preStart() throws Exception{
//        this.connection = dataSource.getConnection();
//        connection.setAutoCommit(false);
//        pstm = connection.prepareStatement("" +
//                "IF EXISTS (SELECT devid FROM dbo.device_status_table WHERE devid = ? and iccid=? and last_inner_sn=?)" +
//                "UPDATE dbo.device_status_table SET last_sync_mode=? ,last_status_time=? ,last_status=? ," +
//                "last_sync_ver=?  where devid=? and iccid=? and last_inner_sn=? " +
//                "ElSE " +
//                "INSERT INTO dbo.device_status_table (devid,iccid,last_inner_sn,last_sync_mode , last_status_time , " +
//                "last_status , last_sync_ver)values(?,?,?,?,?,?,?)");
//    }
//
//    /**
//     * 批次结束后的任务
//     */
//    @Override
//    public void preEnd() throws Exception{
//        try {
//            pstm.executeBatch();
//            connection.commit();
//        } catch (SQLException e) {
//            LOGGER.error("批量提交异常：" + e);
//            e.printStackTrace();
//            try {
//                connection.rollback();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }finally {
//            if (pstm != null) {
//                pstm.close();
//            }
//            if (connection != null) {
//                connection.close();
//            }
//        }
//    }

//    /**
//     * 打印log
//     *
//     * @param record
//     */
//    @Override
//    public void logValue(ConsumerRecord record) {
//        JSONObject json = JSON.parseObject("{}");
//        json.put("logSystemTime", System.currentTimeMillis());
//        json.put("Topic", record.topic());
//        json.put("partition", record.partition());
//        json.put("offset", record.offset());
//        json.put("value", record.value().toString());
//        HEARTBEAT.info(json.toString());
//    }
//
//    /**
//     * 处理设备心眺
//     *
//     * @param jsonObject
//     */
//    private void heartBeat(JSONObject jsonObject) {
//        String deviceName = jsonObject.getString("deviceName");//last_inner_SN
//        jedis.select(0);
//        String flag = jedis.get(deviceName);
//        if (StringUtils.isBlank(flag)) {//表示第一次存储
//            ++redisHeartBeatNums;
//            LOGGER.info("插入redis(0)库deviceName：" + deviceName + ";已插入：" + redisHeartBeatNums);
//            jedis.select(0);
//            jedis.set(deviceName, jsonObject.toString());//更新redis
//            //处理online or offline情况
//            onLineAndOffLine(jsonObject);
//        } else {
//            //先取出新的 进行比较
//            ++updateHeartBeatNums;
//            LOGGER.info("更新HeartBeat,当前deviceName：" + deviceName +
//                ";已更新(插入或更新、含本次)：" + updateHeartBeatNums);
//            jedis.select(0);
//            JSONObject jsonValue = JSON.parseObject(jedis.get(deviceName));
//            Long oldEventTime = Long.valueOf(jsonValue.getString("eventTime"));
//            Long newEventTime = Long.valueOf(jsonObject.getString("eventTime"));
//            if (NumberUtil.lessThan(oldEventTime, newEventTime)) {
//                jedis.select(0);
//                jedis.set(deviceName, jsonObject.toString());//更新
//                onLineAndOffLine(jsonObject);
//                //updateData(deviceName, jsonObject);
//
//            }
//        }
//    }
//
//    /**
//     * 处理 online与offline事件
//     *
//     * @param jsonObject
//     */
//    private void onLineAndOffLine(JSONObject jsonObject) {
//        String deviceName = jsonObject.getString("deviceName");
//        jedis.select(2);
//        JSONObject redisStatusChangeJSON = JSON.parseObject(jedis.get(deviceName));
//        if (redisStatusChangeJSON != null) {
//            String redisUtcSendTime = redisStatusChangeJSON.getString("utcSendTime");
//            String heartBeatEventTime = jsonObject.getString("eventTime");
//            String status = redisStatusChangeJSON.getString("status");
//            if (StringUtils.isNotBlank(status)) {
//                if (StringUtils.equals(status, "online")) {
//                    updateData(deviceName, jsonObject);//正常现象 跟新库
//                } else if (StringUtils.equals(status, "offline")) {
//                    updateData(deviceName, jsonObject);//跟新库
//                    if (StringUtils.isNotBlank(redisUtcSendTime)) {
//                        if (Long.valueOf(heartBeatEventTime) > DateUtil.getTimeInMillis(redisUtcSendTime)) {
//                            //说明 阿里云异常
//                            JSONObject json = JSON.parseObject("{}");
//                            json.put("title", "aliyunException");
//                            json.put("logSystemTime", System.currentTimeMillis());
//                            json.put("heartBeatValue", jsonObject);
//                            json.put("statusChangeValue", redisStatusChangeJSON);
//                            LOGGER.warn(json.toString());
//                        }
//                    }
//                }
//            } else {
//                JSONObject json = JSON.parseObject("{}");
//                json.put("title", "statusChange");
//                json.put("logSystemTime", System.currentTimeMillis());
//                json.put("message", "alikafka-iot-device-status-change事件的status为空");
//                LOGGER.warn(json.toString());
//            }
//        }
//    }
//
//    /**
//     * 更新数据库
//     *
//     * @param deviceName
//     * @param jsonObject
//     */
//    private void updateData(String deviceName, JSONObject jsonObject) {
//        //这里执行 插入数据库
//        //首先查询出 heartbeat、info、status change中的数据
//        jedis.select(0);//查询
//        JSONObject heartBeatJson = JSON.parseObject(jedis.get(deviceName));
//        jedis.select(1);
//        JSONObject infoJson = JSON.parseObject(jedis.get(deviceName));
//        jedis.select(2);
//        JSONObject statusChangeJson = JSON.parseObject(jedis.get(deviceName));
//        String serialNo = heartBeatJson.getString("serialNo") == null ? jsonObject.getString("serialNo"):
//                heartBeatJson.getString("serialNo");//设备电子序列号 devid
//        String iccid = heartBeatJson.getString("iccid") == null ? jsonObject.getString("iccid"):
//                heartBeatJson.getString("iccid");//Sim卡的iccid号
//        if (StringUtils.isBlank(serialNo) || StringUtils.isBlank(iccid)) {
//            LOGGER.warn("deviceName：" + deviceName + "的serialNo=" + serialNo + ";或者iccid=" + iccid + ";为空；");
//        } else {
//            Integer syncMode = null;
//            if (StringUtils.isNotBlank(jsonObject.getString("syncMode"))) {
//                syncMode = Integer.valueOf(jsonObject.getString("syncMode"));//设备同步模式 last_sync_mode
//            }
//            Long eventTime = null;//kafka收到时间 last_status_time
//            if (StringUtils.isNotBlank(jsonObject.getString("eventTime"))) {
//                eventTime = Long.valueOf(jsonObject.getString("eventTime"))/1000;
//            }
//            Integer playStatus = null;//last_status
//            if (StringUtils.isNotBlank(jsonObject.getString("playStatus"))) {
//                playStatus = Integer.valueOf(jsonObject.getString("playStatus"));
//            }
//            String syncVersion = null;
//            if (infoJson != null) {
//                syncVersion = infoJson.getString("syncVersion");//last_sync_ver
//            }
//            //存储
//            try {
//                int y=0;
//                pstm.setObject(++y,serialNo);
//                pstm.setObject(++y,iccid);
//                pstm.setObject(++y,deviceName);
//
//                pstm.setObject(++y,syncMode);
//                pstm.setObject(++y,eventTime);
//                pstm.setObject(++y,playStatus);
//                pstm.setObject(++y,syncVersion);
//
//                pstm.setObject(++y,serialNo);
//                pstm.setObject(++y,iccid);
//                pstm.setObject(++y,deviceName);
//
//                pstm.setObject(++y,serialNo);
//                pstm.setObject(++y,iccid);
//                pstm.setObject(++y,deviceName);
//
//                pstm.setObject(++y,syncMode);
//                pstm.setObject(++y,eventTime);
//                pstm.setObject(++y,playStatus);
//                pstm.setObject(++y,syncVersion);
//                pstm.addBatch();
//            } catch (SQLException e) {
//                e.printStackTrace();
//                LOGGER.error("执行SQL异常：" + e);
//                LOGGER.error("ERROR SQL: " + pstm.toString());
//            } finally {
//
//            }
//        }
//    }
}
