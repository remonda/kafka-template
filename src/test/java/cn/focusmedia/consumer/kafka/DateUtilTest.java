package cn.focusmedia.consumer.kafka;

import cn.focusmedia.consumer.tools.DateUtil;
import org.junit.Test;

public class DateUtilTest {
    @Test
    public static void formatDateTest(){
        System.out.println(DateUtil.getTimeInMillis("2017-11-27T03:16:03.944Z"));
    }
}
