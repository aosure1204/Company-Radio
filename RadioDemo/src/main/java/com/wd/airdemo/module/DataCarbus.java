package com.wd.airdemo.module;

import com.wd.airdemo.util.UiNotifyEvent;

public class DataCarbus {
    public static int[] DATA = new int[FinalRadio.U_CNT_MAX];
    public static UiNotifyEvent[] NOTIFY = new UiNotifyEvent[FinalRadio.U_CNT_MAX];

    static {
        for (int i = 0; i < NOTIFY.length; i++) {
            NOTIFY[i] = new UiNotifyEvent();
            NOTIFY[i].setUpdateCode(i);
        }
    }
    public static String sFactorySn = "";

    public static int[] fmInts ;
    public static int[] amInts;
}
