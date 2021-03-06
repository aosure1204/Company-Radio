package com.wd.radio;

import android.content.Context;
import android.util.Log;

import com.wd.airdemo.module.DataCarbus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    public static final int WORK_MODE_FM = 0;
    public static final int WORK_MODE_AM = 1;


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

    /*
    private static final String TAG = "DataUtil";

    public static final int FM_AUTO_COLLECT_NUM = 6;
    public static final int AM_AUTO_COLLECT_NUM = 6;

    private static final String FILE_NAME_FM_COLLECT = "fm_collect";
    private static final String FILE_NAME_AM_COLLECT = "am_collect";
    private static final String SPLIT_STR = ",";

    private static int[] mFMCollectFreq = new int[12];
    private static int[] mAMCollectFreq = new int[6];

    public static void saveCollectData(final Context context) {
        saveCollectData(context, WORK_MODE_FM);
        saveCollectData(context, WORK_MODE_AM);
    }

    public static void saveCollectData(final Context context, final int workMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String targetFileName = null;
                int[] sourceCollectFreq = null;
                if(workMode == WORK_MODE_FM) {
                    targetFileName = FILE_NAME_FM_COLLECT;
                    sourceCollectFreq = mFMCollectFreq;
                } else if(workMode == WORK_MODE_AM) {
                    targetFileName = FILE_NAME_AM_COLLECT;
                    sourceCollectFreq = mAMCollectFreq;
                }

                FileOutputStream fileOutputStream = null;
                try {
                    //将 mFMCollectFreqs 或 mAMCollectFreqs中的数据转换到String中。
                    StringBuilder resultStr = new StringBuilder();
                    for(int i = 0; i < sourceCollectFreq.length; i++) {
                        resultStr.append(sourceCollectFreq[i]);
                        resultStr.append(SPLIT_STR);
                    }

                    fileOutputStream = context.openFileOutput(targetFileName, Context.MODE_PRIVATE);
                    fileOutputStream.write(resultStr.toString().getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void loadCollectData(final Context context){
        loadCollectData(context, WORK_MODE_FM);
        loadCollectData(context, WORK_MODE_AM);
    }

    public static void loadCollectData(final Context context, final int workMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sourceFileName = null;
                int[] targetCollectFreq = null;
                if(workMode == WORK_MODE_FM) {
                    sourceFileName = FILE_NAME_FM_COLLECT;
                    targetCollectFreq = mFMCollectFreq;
                } else if(workMode == WORK_MODE_AM) {
                    sourceFileName = FILE_NAME_AM_COLLECT;
                    targetCollectFreq = mAMCollectFreq;
                }
                if(sourceFileName == null)
                    return;

                FileInputStream fileInputStream = null;
                try {
                    //读取文件中的内容，并保存到StringBuilder中。
                    fileInputStream = context.openFileInput(sourceFileName);
                    BufferedReader buffReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    StringBuilder result = new StringBuilder();
                    String line;   //分行读取
                    while (( line = buffReader.readLine()) != null) {
                        result.append(line+"\n");
                    }

                    //将 mFMCollectFreq 或 mFMCollectFreq 中的内容清空。
                    for(int i = 0; i < targetCollectFreq.length; i++) {
                        targetCollectFreq[i] = 0;
                    }
                    //将读取的内容按英文字符逗号拆分，并保存到 mFMCollectFreq 或 mFMCollectFreq 中。
                    String[] dataArray = result.toString().split(SPLIT_STR);
                    for (int i = 0; i < dataArray.length && i < targetCollectFreq.length; i++) {
                        if(dataArray[i] != null && dataArray[i] != "") {
                            targetCollectFreq[i] = Integer.parseInt(dataArray[i]);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static boolean isCollectEmpty(int workMode){
        int[] collectFreq = getCollectFreq(workMode);
        for(int i = 0; i < collectFreq.length; i++) {
            if(collectFreq[i] != 0)
                return false;
        }
        return true;
    }

    public static void addCollect(int workMode, int freq, int position) {
        int[] targetCollectFreq;
        if(workMode == WORK_MODE_AM) {
            targetCollectFreq = mAMCollectFreq;
        } else {
            targetCollectFreq = mFMCollectFreq;
        }

        targetCollectFreq[position] = freq;
    }

    public static void removeCollect(int workMode, int position) {
        int[] targetCollectFreq;
        if(workMode == WORK_MODE_AM) {
            targetCollectFreq = mAMCollectFreq;
        } else {
            targetCollectFreq = mFMCollectFreq;
        }

        targetCollectFreq[position] = 0;
    }

    public static boolean isCollect(int workMode, int freq) {
        int[] targetCollectFreq;
        if(workMode == WORK_MODE_AM) {
            targetCollectFreq = mAMCollectFreq;
        } else {
            targetCollectFreq = mFMCollectFreq;
        }

        for(int i = 0; i < targetCollectFreq.length; i++) {
            if(targetCollectFreq[i] == freq) {
                return true;
            }
        }
        return false;
    }

    public static int[] getCollectFreq(int workMode) {
        if(workMode == WORK_MODE_AM) {
            return mAMCollectFreq;
        } else {
            return mFMCollectFreq;
        }
    }

    public static void autoSaveToCollect(int workMode) {
        int[] collectFreq;
        int[] searchFreqs;
        int autoCollectNum;
        if(workMode == DataUtil.WORK_MODE_AM) {
            collectFreq = mAMCollectFreq;
            searchFreqs = DataCarbus.amInts;
            autoCollectNum = AM_AUTO_COLLECT_NUM;
        } else {
            collectFreq = mFMCollectFreq;
            searchFreqs = DataCarbus.fmInts;
            autoCollectNum = FM_AUTO_COLLECT_NUM;
        }

        for(int i = 0; i < searchFreqs.length && i < autoCollectNum; i++) {
            collectFreq[i] = searchFreqs[i];
        }
    }*/

}
