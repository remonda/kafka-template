package cn.focusmedia.consumer.config;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 单例 自定义消费组配置类 <br/>
 * ClassName：GroupConsumerConfigur <br/>
 * Author：QGX <br/>
 * Date：2019/11/3 18:24 <br/>
 * Version：1.0
 */
public class GroupConsumerConfigur {
    private static final Logger LOGGER = Logger.getLogger(GroupConsumerConfigur.class);
    private static final GroupConsumerConfigur groupConsumerConfigur = new GroupConsumerConfigur();
    private static Properties properties = null;

    private GroupConsumerConfigur() {
    }

    /**
     * 加载配置文件
     */
    static {
        properties = new Properties();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("consumer.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("加载配置文件失败：consumer.properties");
        }
    }

    /**
     * 获取实例对象
     *
     * @return
     */
    public static GroupConsumerConfigur getInstance() {
        return groupConsumerConfigur;
    }

    /**
     * 获取配置文件中的字符串值
     *
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取配置文件中的布尔值
     *
     * @param key
     * @return
     */
    public boolean getBooleanValue(String key) {
        boolean value = Boolean.valueOf(properties.getProperty(key));
        return value;
    }

    /**
     * 获取配置文件中的整形
     *
     * @param key
     * @return
     */
    public Integer getIntegerValue(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }
}
