package cn.focusmedia.consumer.statuschange;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO <br/>
 * ClassName：GroupStatusChange <br/>
 * Author：QGX <br/>
 * Date：2019/11/4 21:26 <br/>
 * Version：1.0
 */
public class GroupStatusChange {
    private List<ConsumerStatusChangeTask> list = new ArrayList<>();
    private GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();

    public GroupStatusChange(String topic, String groupId, int numConsumers) {
        for (int i = 0; i < numConsumers; i++) {
            list.add(new ConsumerStatusChangeTask(topic, groupId));
        }
    }

    /**
     * 执行Consumer
     */
    public void execute() {
        for (int i = 0; i < list.size(); i++) {
            new Thread(list.get(i),"statusChange-Thread").start();
        }
    }
}
