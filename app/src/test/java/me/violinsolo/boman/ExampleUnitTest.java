package me.violinsolo.boman;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import me.violinsolo.boman.util.BluetoothUtil;
import me.violinsolo.boman.util.DateUtil;
import me.violinsolo.boman.util.HexUtil;

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

        int[] timeArr = DateUtil.castMillis2IntArray(millisecs, null);
        System.out.println(Arrays.toString(timeArr));

        long millisecs2 = DateUtil.castIntArray2Millis(timeArr, null);
        System.out.println(millisecs2);
        System.out.println(DateUtil.formatMillisToGMT8(millisecs2));
    }

    @Test
    public void test_byte_uint_sint() {
        System.out.println(getType(0x88));
        System.out.println(getType((byte)0x88));

        byte[] arr = new byte[2];
        arr[0] = (byte) 0xFE;
        arr[1] = (byte) 0xC9;

        int m1 = arr[0]&0xFF << 8 + arr[1]&0xFF;
        int m2 = (arr[0]&0xFF << 8) + (arr[1]&0xFF);
        short m3 = (short) ((arr[0]&0xFF << 8) + (arr[1]&0xFF));

        System.out.println(m1);
        System.out.println(m2);
        System.out.println((arr[0]&0xFF << 8));
        System.out.println((arr[1]&0xFF));
        System.out.println(m3);
        System.out.println((0xFF << 24) + (0xFF << 16) + (0xFF << 8) + (0xFF));

        short n = 0b0111111111111111;
        short n2 = (0b0111<<12)|(0xF<<8)|(0xF<<4)|(0xF); // 构建二进制 0111 1111 1111 1111
        short n3 = (0x7<<12)|(0xF<<8)|(0xF<<4)|(0xF); // 构建二进制 0111 1111 1111 1111 如果是32位int，就是个正数，2^16-1
                                                      // 如果是16位short，第一位为符号位，剩下15位表示数，也就是正的 2^16-1
        short n4 = (short) 0b1111111111111111;
        short n5 = -0b1;
//        short n4 = (0b1111<<12)|(0xF<<8)|(0xF<<4)|(0xF); // 构建二进制 1111 1111 1111 1111 如果是32位int，就是个正数，2^17-1
//                                                      // 如果是16位short，第一位为符号位，剩下15位表示数，也就是负的 -（1

        System.out.println("n:"+n);
        System.out.println("n2:"+n2);
        System.out.println("n3:"+n3);
        System.out.println("n4:"+n4);
        System.out.println("n5:"+n5);

        short n6 = (short) (arr[0]&n + arr[1]);
        System.out.println("n6:"+n6);
        short n7 = (short) (arr[0]&n5 + arr[1]);
        System.out.println("n7:"+n7);
        short n8 = (short) ((arr[0]&n5<<8) + arr[1]&n5);
        System.out.println("n8:"+n8);
//        short n82 = arr[0]<<8&n5 | arr[1]&n5;
//        System.out.println("n82:"+n82 );

        System.out.println((arr[0]&n5)<<8);
        System.out.println((arr[0]<<8)&n5);

        short l = 0;
        l |= arr[0] & 0xFF;
        l <<= 8;
        l |= arr[1] & 0xFF;
        System.out.println("n9:"+l);

        short a = -2;
        short b = 1;
        System.out.println(a&b);
        System.out.println(a|b);
        System.out.println(a+b);

        System.out.println(0b1);

        System.out.println(n5);

    }

    public static String getType(Object o){ //获取变量类型方法
        return o.getClass().toString(); //使用int类型的getClass()方法
    }

    @Test
    public void test_byte_short_casting() {

        byte[] arr = new byte[2];
        arr[0] = (byte) 0xFE;
        arr[1] = (byte) 0xC9;

        System.out.println(HexUtil.toShort(arr));

        short m = HexUtil.toShort(arr);
        byte[] n1 = BluetoothUtil.genCurrentTemperatureOffset(m);
        System.out.println(HexUtil.hexStrBigEndian(n1));

        System.out.println(HexUtil.hexStrBigEndian(BluetoothUtil.genCurrentTemperatureOffset((short) -2)));

    }
}