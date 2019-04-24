package com.wd.radio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import com.wd.airdemo.module.DataCarbus;

public class CollectFreq {
    private static final String TAG = "CollectFreq";

    public static final int FM_AUTO_COLLECT_NUM = 6;
    public static final int AM_AUTO_COLLECT_NUM = 6;

    private static final String FILE_NAME_FM_COLLECT = "fm_collect";
    private static final String FILE_NAME_AM_COLLECT = "am_collect";
    private static final String SPLIT_STR = ",";

    private Context mContext;

    private int[] mFMCollectFreq;
    private int[] mAMCollectFreq;

    private static CollectFreq INSTANCE;
    public static CollectFreq getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CollectFreq(context.getApplicationContext());
        }
        return INSTANCE;
    }
    private CollectFreq(Context context){
        mContext = context;
        mFMCollectFreq = new int[12];
        mAMCollectFreq = new int[6];
    }

    public void saveCollectData() {
        saveCollectData(DataUtil.WORK_MODE_FM);
        saveCollectData(DataUtil.WORK_MODE_AM);
    }

    public void saveCollectData(final int workMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String targetFileName = null;
                int[] sourceCollectFreq = null;
                if(workMode == DataUtil.WORK_MODE_FM) {
                    targetFileName = FILE_NAME_FM_COLLECT;
                    sourceCollectFreq = mFMCollectFreq;
                } else if(workMode == DataUtil.WORK_MODE_AM) {
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

                    fileOutputStream = mContext.openFileOutput(targetFileName, Context.MODE_PRIVATE);
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

    public void loadCollectData(){
        loadCollectData(DataUtil.WORK_MODE_FM);
        loadCollectData(DataUtil.WORK_MODE_AM);
    }

    public void loadCollectData(final int workMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sourceFileName = null;
                int[] targetCollectFreq = null;
                if(workMode == DataUtil.WORK_MODE_FM) {
                    sourceFileName = FILE_NAME_FM_COLLECT;
                    targetCollectFreq = mFMCollectFreq;
                } else if(workMode == DataUtil.WORK_MODE_AM) {
                    sourceFileName = FILE_NAME_AM_COLLECT;
                    targetCollectFreq = mAMCollectFreq;
                }
                if(sourceFileName == null)
                    return;

                FileInputStream fileInputStream = null;
                try {
                    //读取文件中的内容，并保存到StringBuilder中。
                    fileInputStream = mContext.openFileInput(sourceFileName);
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

                    // mLoadFinishCallbackList的实现者为LauncherActivity,用于收藏频率读取加载完更新快捷区域
                    if(mLoadFinishCallbackList!=null && mLoadFinishCallbackList.size() != 0) {
                        for(int i = 0; i < mLoadFinishCallbackList.size(); i++) {
                            mLoadFinishCallbackList.get(i).onLoadFinish(workMode);
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

    public boolean isCollectEmpty(int workMode){
        int[] collectFreq = getCollectFreq(workMode);
        for(int i = 0; i < collectFreq.length; i++) {
            if(collectFreq[i] != 0)
                return false;
        }
        return true;
    }

    public void addCollect(int workMode, int freq, int position) {
        int[] targetCollectFreq;
        if(workMode == DataUtil.WORK_MODE_AM) {
            targetCollectFreq = mAMCollectFreq;
        } else {
            targetCollectFreq = mFMCollectFreq;
        }

        targetCollectFreq[position] = freq;
    }

    public void removeCollect(int workMode, int position) {
        int[] targetCollectFreq;
        if(workMode == DataUtil.WORK_MODE_AM) {
            targetCollectFreq = mAMCollectFreq;
        } else {
            targetCollectFreq = mFMCollectFreq;
        }

        targetCollectFreq[position] = 0;
    }

    public boolean isCollect(int workMode, int freq) {
        int[] targetCollectFreq;
        if(workMode == DataUtil.WORK_MODE_AM) {
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

    public int[] getCollectFreq(int workMode) {
        if(workMode == DataUtil.WORK_MODE_AM) {
            return mAMCollectFreq;
        } else {
            return mFMCollectFreq;
        }
    }

    public void autoSaveToCollect(int workMode) {
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
    }

    private List<LoadFinishCallback> mLoadFinishCallbackList = new ArrayList<LoadFinishCallback>();

    interface LoadFinishCallback {
        void onLoadFinish(int workMode);
    }

    public void setLoadFinishCallback(LoadFinishCallback loadFinishCallback) {
        mLoadFinishCallbackList.add(loadFinishCallback);
    }
}
