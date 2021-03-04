package cn.focusmedia.consumer.kafka;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public static void StringUtilsTest(){
        //empy 认为空格也是有内容的，所以空格不为空返回false，blank反之
        System.out.println(StringUtils.isBlank(null));//true
        System.out.println(StringUtils.isBlank(""));//true
        System.out.println(StringUtils.isBlank(" "));//true
        System.out.println(StringUtils.isNotBlank(""));//false
        System.out.println(StringUtils.isNotBlank(null));//false

        System.out.println(StringUtils.isEmpty(null));//true
        System.out.println(StringUtils.isEmpty(""));//true
        System.out.println(StringUtils.isEmpty(" "));//false
        System.out.println(StringUtils.isNotEmpty(""));//false
    }
}
