package cn.focusmedia.consumer;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;
import cn.focusmedia.consumer.constant.ConsumerConstant;
import cn.focusmedia.consumer.heartbeat.GroupConsumerHeartBeat;
import cn.focusmedia.consumer.info.GroupInfo;
import cn.focusmedia.consumer.statuschange.GroupStatusChange;
import org.apache.log4j.Logger;

/**
 * kafka Consumer启动类
 */
public class ConsumerMain {
    private static GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();
    private final static Logger LOGGER = Logger.getLogger(ConsumerMain.class);

    public static void main(String[] args) {
        try {
            GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();
            String groupId = configur.getStringValue(ConsumerConstant.GROUP_ID);

            String heartBeatTopic = configur.getStringValue(ConsumerConstant.CONSUMER_TOPIC_HEARTBEAT);
//            String infoTopic = configur.getStringValue(ConsumerConstant.CONSUMER_TOPIC_INFO);
//            String statusChangeTopic = configur.getStringValue(ConsumerConstant.CONSUMER_TOPIC_STATUS_CHANGE);
            Integer numConsumers = configur.getIntegerValue(ConsumerConstant.CONSUMER_NUMS);

            LOGGER.info("创建消费组：[" + "GroupConsumerHeartBeat,GroupInfo,GroupStatusChange" + "]");
//            GroupInfo groupInfo = new GroupInfo(infoTopic, groupId, numConsumers);
//            GroupStatusChange groupStatusChange = new GroupStatusChange(statusChangeTopic, groupId, numConsumers);
            GroupConsumerHeartBeat groupConsumerHeartBeat = new GroupConsumerHeartBeat(heartBeatTopic, groupId, numConsumers);

            LOGGER.info("开始消费消息");
            groupConsumerHeartBeat.execute();
//            groupInfo.execute();
//            groupStatusChange.execute();

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug(e);
        }

    }
}
