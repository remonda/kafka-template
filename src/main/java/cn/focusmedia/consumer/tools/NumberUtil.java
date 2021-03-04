package cn.focusmedia.consumer.tools;

public class NumberUtil {
    /**
     * one 是否小于 two
     * @param one
     * @param two
     * @return
     */
    public static boolean lessThan(Long one, Long two) {
        return one < two ? true : false;
    }

    /**
     * one 是否等于 two
     * @param one
     * @param two
     * @return
     */
    public static boolean equalTo(Long one, Long two) {
        return one ==two ? true : false;
    }
}
