package me.violinsolo.boman;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import me.violinsolo.boman.util.DateUtil;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_DateUtil_fns() {
        long millsec = DateUtil.getCurrentTimestamp();
        System.out.println(millsec);
        System.out.println(DateUtil.getCurrentTimeFormat());
        System.out.println(DateUtil.formatMillisToGMT(millsec));

        System.out.println(DateUtil.getDay(millsec, null));


        System.out.println(Locale.getDefault());
        System.out.println(Locale.ENGLISH);
        System.out.println(Locale.CHINESE);
        System.out.println(Locale.US);
        System.out.println(Locale.CHINA);

        System.out.println(DateUtil.getDayADV(millsec, null));

        System.out.println(Locale.getDefault().getLanguage());
        System.out.println(Locale.CHINESE.getLanguage());
        System.out.println(Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage()));

        System.out.println("hour minute:"+DateUtil.getHourMinuteADV(millsec, null));
    }

    @Test
    public void test_calendar_fns() {
        long millisecs = DateUtil.getCurrentTimestamp();
        System.out.println(millisecs);
        System.out.println(DateUtil.formatMillisToGMT8(millisecs));

        int[] timeArr = DateUtil.castIntArray2Millis(millisecs, null);
        System.out.println(Arrays.toString(timeArr));

        long millisecs2 = DateUtil.castIntArray2Millis(timeArr, null);
        System.out.println(millisecs2);
        System.out.println(DateUtil.formatMillisToGMT8(millisecs2));
    }
}