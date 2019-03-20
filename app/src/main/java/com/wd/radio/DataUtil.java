package com.wd.radio;

import java.text.DecimalFormat;

public class DataUtil {
    public static final int TRANSFER_VALUE_00 = 0x00;
    public static final int TRANSFER_VALUE_01 = 0x01;
    public static final int TRANSFER_VALUE_02 = 0x02;
    public static final int TRANSFER_VALUE_03 = 0x03;
    public static final int TRANSFER_VALUE_04 = 0x04;
    public static final int TRANSFER_VALUE_05 = 0x05;
    public static final int TRANSFER_VALUE_06 = 0x06;
    public static final int TRANSFER_VALUE_07 = 0x07;
    public static final int TRANSFER_VALUE_08 = 0x08;
    public static final int TRANSFER_VALUE_09 = 0x09;
    public static final int TRANSFER_VALUE_10 = 0x10;

    public static final String WORK_MODE = "work_mode";

    //收音机工作模式
    public static final int WORK_MODE_FM = 1;
    public static final int WORK_MODE_AM = 2;


    /**
     * 格式化FM频率值
     * FM频率值需要除于100，并保留一个小数点
     *
     * @param freq FM的频率，整数
     * @return 频率值需要除于100，并保留一个小数点
     * */
    public static String formatFMFreq(int freq) {
        float value = freq / 100.0f;
        DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String valueStr = decimalFormat.format(value);//format 返回的是字符串
        return valueStr;
    }
}
