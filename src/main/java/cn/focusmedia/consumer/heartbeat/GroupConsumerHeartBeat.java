package cn.focusmedia.consumer.heartbeat;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;

import java.util.ArrayList;
import java.util.List;

/**
 * GropConsumerHeartBeat 组的创建
 */
public class GroupConsumerHeartBeat {

    private List<ConsumerHeartBeatTask> list = new ArrayList<>();
    private GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();

    public GroupConsumerHeartBeat(String topic, String groupId, int numConsumers) {
        for (int i = 0; i < numConsumers; i++) {
            list.add(new ConsumerHeartBeatTask(topic, groupId));
        }
    }

    /**
     * 执行Consumer
     */
    public void execute() {
        for (int i = 0; i < list.size(); i++) {
            new Thread(list.get(i),"HeartBeat-Thread").start();
        }
    }
}
