package cn.focusmedia.consumer.info;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO <br/>
 * ClassName：GroupInfo <br/>
 * Author：QGX <br/>
 * Date：2019/11/4 21:27 <br/>
 * Version：1.0
 */
public class GroupInfo {
    private List<ConsumerInfoTask> list = new ArrayList<>();
    private GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();

    public GroupInfo(String topic, String groupId, int numConsumers) {
        for (int i = 0; i < numConsumers; i++) {
            list.add(new ConsumerInfoTask(topic, groupId));
        }
    }

    /**
     * 执行Consumer
     */
    public void execute() {
        for (int i = 0; i < list.size(); i++) {
            new Thread(list.get(i),"info-Thread").start();
        }
    }
}
