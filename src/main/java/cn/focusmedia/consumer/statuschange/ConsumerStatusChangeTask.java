package cn.focusmedia.consumer.statuschange;

import cn.focusmedia.consumer.base.Task;
import cn.focusmedia.consumer.tools.ConnectionUtil;
import cn.focusmedia.consumer.tools.DateUtil;
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

/**
 * TODO <br/>
 * ClassName：ConsumerStatusChangeTask <br/>
 * Author：QGX <br/>
 * Date：2019/11/4 21:21 <br/>
 * Version：1.0
 */
public class ConsumerStatusChangeTask extends Task {
    private final static Logger LOGGER = Logger.getLogger(ConsumerStatusChangeTask.class);
    private final static Logger STATUSCHANGE = Logger.getLogger("statusChange");
    private Jedis jedis = RedisPool.getJedis();
    private DataSource dataSource = ConnectionUtil.getDataSource();
    private Connection connection = null;
    private PreparedStatement pstm = null;
    private Long redisStatusNums = 0l;
    private Long updateStatusChangeNums = 0l;

    public ConsumerStatusChangeTask(String topic, String groupId) {
        super(topic, groupId);
    }

    @Override
    public void doWork(ConsumerRecord record) {
        if (record != null) {

//            logValue(record);
            JSONObject jsonObject = JSON.parseObject(record.value().toString());
            String deviceName = jsonObject.getString("deviceName");
            if (deviceName != null) {
                statusChange(jsonObject);
            }
        }
    }

//    /**
//     * 批量业务执行前
//     *
//     * @throws Exception
//     */
//    @Override
//    public void preStart() throws Exception {
//        this.connection = dataSource.getConnection();
//        connection.setAutoCommit(false);
//        pstm = connection.prepareStatement("" +
//                "IF EXISTS (SELECT devid FROM dbo.device_status_table WHERE devid = ? and iccid=? and last_inner_sn=?)" +
//                "UPDATE dbo.device_status_table SET last_sync_mode=? ,last_status_time=? ,last_status=? ," +
//                "last_sync_ver=? ,last_login_time=?  where devid=? and iccid=? and last_inner_sn=?" +
//                "ElSE " +
//                "INSERT INTO dbo.device_status_table (devid,iccid,last_inner_sn ,last_sync_mode,last_status_time," +
//                "last_staatus,last_sync_ver,last_login_time)values(?,?,?,?,?,?,?,?)");
//    }
//
//    /**
//     * 批量业务执行后
//     *
//     * @throws Exception
//     */
//    @Override
//    public void preEnd() throws Exception {
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

    private void statusChange(JSONObject jsonObject) {
        String deviceName = jsonObject.getString("deviceName");//last_inner_SN
        jedis.select(2);
        String flag = jedis.get(deviceName);
        if (flag == null) {//表示第一次存储
            ++redisStatusNums;
            LOGGER.info("插入redis(2)库deviceName：" + deviceName + ";已插入：" + redisStatusNums);
            jedis.select(2);
            jedis.set(deviceName, jsonObject.toString());
            checkUpdateByStatusChaneg(deviceName, jsonObject);
        } else {
            //先取出新的 进行比较
            ++updateStatusChangeNums;
            LOGGER.info("更新statusChange,当前deviceName：" + deviceName +
                    ";已跟新(插入或更新、含本次)：" + updateStatusChangeNums);
            jedis.select(2);
            JSONObject jsonValue = JSON.parseObject(jedis.get(deviceName));//取出redis中的版本
            String oldUtcSendTime = jsonValue.getString("utcSendTime");
            String newUtcSendTime = jsonObject.getString("utcSendTime");
            if (StringUtils.isNotBlank(newUtcSendTime)) {
                if (DateUtil.getTimeInMillis(oldUtcSendTime) < DateUtil.getTimeInMillis(newUtcSendTime)) {
                    jedis.select(2);
                    jedis.set(deviceName, jsonObject.toString());//更新
                    checkUpdateByStatusChaneg(deviceName, jsonObject);
                }
            }
        }

    }

    /**
     * @param deviceName
     * @param jsonObject
     */
    private void checkUpdateByStatusChaneg(String deviceName, JSONObject jsonObject) {
        jedis.select(0);//切换到heartbeat上
        JSONObject heartBeatJson = JSON.parseObject(jedis.get(deviceName));
        //更新数据库
        if (heartBeatJson != null) {
            updateData(deviceName, jsonObject);
        } else {
            JSONObject json = JSON.parseObject("{}");
            json.put("title", "statusChange");
            json.put("logSystemTime", System.currentTimeMillis());
            json.put("value", "接受到statusChange事件插入或更新redis库，需要更新sqlserver redis(0)上未找到对应的deviceName：" +
                    "" + deviceName);
            LOGGER.warn(json.toString());
        }
    }

//    @Override
//    public void logValue(ConsumerRecord record) {
//        JSONObject json = JSON.parseObject("{}");
//        json.put("logSystemTime", System.currentTimeMillis());
//        json.put("Topic", record.topic());
//        json.put("partition", record.partition());
//        json.put("offset", record.offset());
//        json.put("value", record.value().toString());
//        STATUSCHANGE.info(json.toString());
//    }

    private void updateData(String deviceName, JSONObject jsonObject) {
        //这里执行 插入数据库
        //首先查询出 heartbeat、info、status change中的数据
        jedis.select(0);//查询
        JSONObject heartBeatJson = JSON.parseObject(jedis.get(deviceName));
        jedis.select(1);
        JSONObject infoJson = JSON.parseObject(jedis.get(deviceName));
        jedis.select(2);
        JSONObject statusChangeJson = JSON.parseObject(jedis.get(deviceName));
        String serialNo = heartBeatJson.getString("serialNo") == null ? jsonObject.getString("serialNo") :
                heartBeatJson.getString("serialNo");//设备电子序列号 devid
        String iccid = heartBeatJson.getString("iccid") == null ? jsonObject.getString("iccid") :
                heartBeatJson.getString("iccid");//Sim卡的iccid号
        if (StringUtils.isBlank(serialNo) || StringUtils.isBlank(iccid)) {
            LOGGER.warn("deviceName：" + deviceName + "的serialNo=" + serialNo + ";或者iccid=" + iccid + ";为空");
        } else {
            Integer syncMode = null;
            if (StringUtils.isNotBlank(jsonObject.getString("syncMode"))) {
                syncMode = Integer.valueOf(jsonObject.getString("syncMode"));//设备同步模式 last_sync_mode
            }
            //这里换成 utcSendTime的时间
            Long eventTime = null;
            if (StringUtils.isNotBlank(jsonObject.getString("utcSendTime"))) {
                LOGGER.info("utcSendTime: " + jsonObject.getString("utcSendTime"));
                eventTime = DateUtil.getTimeInMillis(jsonObject.getString("utcSendTime")) / 1000;
                LOGGER.info("event_time: " + eventTime);
            }
            Integer playStatus = null;
            if (StringUtils.isNotBlank(jsonObject.getString("playStatus"))) {
                Integer.valueOf(jsonObject.getString("playStatus"));//last_status
            }
            String syncVersion = null;
            if (infoJson != null) {
                syncVersion = infoJson.getString("syncVersion");//last_sync_ver
            }
            Long lastLoginLime = null;

            if (statusChangeJson != null) {
                String utcLastCommunicatonTime = statusChangeJson.getString("utcLastCommunicatonTime");
                if (StringUtils.isNotBlank(utcLastCommunicatonTime)) {
                    //last_login_time
                    LOGGER.info("utcLastCommunicatonTime: " + utcLastCommunicatonTime);
                    lastLoginLime = Long.valueOf(DateUtil.getTimeInMillis(utcLastCommunicatonTime) + "") / 1000;
                }
            }
            try {
                int y=0;
                pstm.setObject(++y,serialNo);
                pstm.setObject(++y,iccid);
                pstm.setObject(++y,deviceName);

                pstm.setObject(++y,syncMode);
                pstm.setObject(++y,eventTime);
                pstm.setObject(++y,playStatus);
                pstm.setObject(++y,syncVersion);
                pstm.setObject(++y,lastLoginLime);

                pstm.setObject(++y,serialNo);
                pstm.setObject(++y,iccid);
                pstm.setObject(++y,deviceName);

                pstm.setObject(++y,serialNo);
                pstm.setObject(++y,iccid);
                pstm.setObject(++y,deviceName);

                pstm.setObject(++y,syncMode);
                pstm.setObject(++y,eventTime);
                pstm.setObject(++y,playStatus);
                pstm.setObject(++y,syncVersion);
                pstm.setObject(++y,lastLoginLime);
                pstm.addBatch();
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("执行SQL异常：" + e);
                LOGGER.error("执行的SQL：" + pstm.toString());
            } finally {
            }
        }
    }
}
