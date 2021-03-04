package cn.focusmedia.consumer.base;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;
import cn.focusmedia.consumer.constant.ConsumerConstant;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 任务的抽象 <br/>
 * ClassName：Task <br/>
 * Author：QGX <br/>
 * Date：2019/11/3 20:37 <br/>
 * Version：1.0
 */
public abstract class Task implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(Task.class);
    private KafkaConsumer<String, String> consumer = null;
    private GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();

    public Task(String topic, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                configur.getStringValue(ConsumerConstant.BOOTSTRAP_SERVERS_CONFIG));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                configur.getStringValue(ConsumerConstant.AUTO_OFFSET_RESET));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                configur.getIntegerValue(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG));
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
                configur.getIntegerValue(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG));
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, configur.getIntegerValue(
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG));
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, configur.getIntegerValue(
                ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG));
        this.consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                LOGGER.info("负载均衡前：onPartitionsRevoked");
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                try {
                    LOGGER.info("负载均衡后：onPartitionsAssigned");
                    LOGGER.info(collection.toArray());
//                    Map<TopicPartition, Long> beginningOffset = consumer.beginningOffsets(collection);
//                    //测试环境 读取历史数据 --from-beginning
//                    for (Map.Entry<TopicPartition, Long> entry : beginningOffset.entrySet()) {
//                        //方法一：基于seek方法
//                        TopicPartition tp = entry.getKey();
//                        long offset = entry.getValue();
//                        consumer.seek(tp, offset);
//                        //方法二：基于seekToBeginning方法
//                        //consumer.seekToBeginning(collection);
//                    }
                    //生产环境
                    for (TopicPartition partition : collection) {
                        //获取partition的最新偏移量，实现原理是向协调者发送获取请求
                        OffsetAndMetadata offset = consumer.committed(partition);
                        //设置本地拉取分量，下次拉取消息以这个偏移量为准
                        consumer.seek(partition, offset.offset());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("负载均衡失败：" + e);
                }
            }
        });
    }

    @Override
    public void run() {
        ConsumerRecords<String, String> records = null;
        ConcurrentHashMap<TopicPartition, OffsetAndMetadata> hashMap = new ConcurrentHashMap<>();
        while (true) {
            try {
                records = consumer.poll(Duration.ofSeconds(configur.getIntegerValue(ConsumerConstant.KAFKA_POLL_MS)));
                if (records != null) {
//                    preStart();
                    for (ConsumerRecord<String, String> record : records) {
                        hashMap.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset(), ""));
                        try {
                            doWork(record);
                            consumer.commitSync(hashMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.warn(
                                    "提交Topic：" + record.topic() + ";partition：" + record.partition() +
                                            ";offset：" + record.offset() + ";value：" + record.value().toString() + ";失败：" + e);
                        }
                        hashMap.clear();
                    }
//                    preEnd();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("拉去消息失败(poll)：" + e);
            }
        }
    }

    /**
     * 处理业务逻辑
     *
     * @param record
     */

    public abstract void doWork(ConsumerRecord record);

//    /**
//     * 批次开始前的初始化
//     */
//    public abstract void preStart()throws Exception;
//
//    /**
//     * 批次结束后的任务
//     */
//    public abstract void preEnd() throws Exception;
//
//    /**
//     * 打kafka值 log
//     *
//     * @param record
//     */
//    public abstract void logValue(ConsumerRecord record);

}
