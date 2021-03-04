package cn.focusmedia.consumer.kafka;

import cn.focusmedia.consumer.tools.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import static cn.focusmedia.consumer.tools.DateUtil.getTimeInMillis;

public class TestDateUtil {

    @Test
    public void DateUtilTest() {
        System.out.println("start redis util test");

        Long tt = DateUtil.getTimeInMillis("2019-11-16T18:07:50.301Z");

        System.out.println(tt);


    }
}
