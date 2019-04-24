package com.wd.radio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wd.airdemo.MyApp;
import com.wd.airdemo.module.DataCarbus;
import com.wd.airdemo.module.FinalRadio;
import com.wd.airdemo.module.RemoteTools;
import com.wd.airdemo.util.IUiNotify;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    private int[] freqArray = {};

    private Button mBtnActionBarBack;

    private RecyclerView mRecyclerView;
    private ListAdapter mListAdapter;

    private int mWorkMode;

    private CollectFreq mCollectFreq;
    private boolean isCollectFreqChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mCollectFreq = CollectFreq.getInstance(this);

        mBtnActionBarBack = (Button) findViewById(R.id.btn_action_bar_back);
        mBtnActionBarBack.setOnClickListener(this);

        Intent intent = getIntent();
        mWorkMode = intent.getIntExtra(DataUtil.WORK_MODE, DataUtil.WORK_MODE_FM);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mListAdapter = new ListAdapter(freqArray, mWorkMode, this);
        mListAdapter.setOnListItemClickListener(mOnListItemClickListener);
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        MyApp.getOBJ().requestRadioSource();
        if(mWorkMode == DataUtil.WORK_MODE_AM) {
            MyApp.getOBJ().requestAMApp();
        } else {
            MyApp.getOBJ().requestFMApp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerDataListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterDataListener();
    }

    /**
     * 添加数据监听
     * */
    private void registerDataListener(){
        for(int i = 0; i < FinalRadio.U_CNT_MAX; i++) {
            DataCarbus.NOTIFY[i].addNotify(mNotify, 1);
        }
    }

    /**
     * 移除数据监听
     * */
    private void unregisterDataListener(){
        for(int i = 0; i < FinalRadio.U_CNT_MAX; i++) {
            DataCarbus.NOTIFY[i].removeNotify(mNotify);
        }
    }

    private void sendCmd(int i, int id) {
        RemoteTools.cmd(i, id);
    }

    private IUiNotify mNotify = new IUiNotify() {
        @Override
        public void onNotify(int updateCode, int[] ints, float[] flts, String[] strs) {
            int value = DataCarbus.DATA[updateCode];

            switch (updateCode) {
                // 监听当前搜索到的频率列表
                case FinalRadio.U_CHANNEL_FREQ:
                    //***************************  此处与别处不同，别处一般返回的是 int 类型，此处返回的是 int数组。
                    int its[] = null;
                    if(DataCarbus.DATA[FinalRadio.U_BAND] == 1){
                        its = DataCarbus.amInts;
                    }else {
                        its = DataCarbus.fmInts;
                    }

                    if(its != null) {
                        Log.d(TAG, "onNotify U_CHANNEL_FREQ  " + updateCode + ": ints.length = " + its.length);
                        onChannelFreqChange(its);
                    }
                    break;
                    // 监听当前搜台状态，0关 1开
                case FinalRadio.U_SEARCH_STATE:
                    Log.d(TAG, "onNotify: " + updateCode + " - " +value);
                    onSearchStateChange(value);
                    break;
            }
        }
    };

    private void onChannelFreqChange(int[] ints) {
        mListAdapter = new ListAdapter(ints, mWorkMode, this);
        mListAdapter.setOnListItemClickListener(mOnListItemClickListener);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();

        //如果收藏频率为空，则自动存储。
        if(mCollectFreq.isCollectEmpty(mWorkMode)) {
            mCollectFreq.autoSaveToCollect(mWorkMode);
            isCollectFreqChanged = true;
        } else {
            isCollectFreqChanged = false;
        }
    }

    /**
     * 搜台状态
     *
     * 0关 1开
     * */
    private void onSearchStateChange(int data) {
        switch (data) {
            case DataUtil.TRANSFER_VALUE_00:
                Toast.makeText(this, R.string.search_finish, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: enter.");
        switch (v.getId()) {
            case R.id.btn_action_bar_back:
                sendCmd(FinalRadio.C_SEARCH, DataUtil.TRANSFER_VALUE_00);   //停止搜索
                setIntentResult();
                finish();
                break;
        }
    }

    private ListAdapter.OnListItemClickListener mOnListItemClickListener = new ListAdapter.OnListItemClickListener() {
/*        @Override
        public void onCollect(int freq, boolean isCollect) {
            if(isCollect) {
                DataUtil.addCollect(mWorkMode, freq);
            } else {
                DataUtil.removeCollect(mWorkMode, freq);
            }
        }*/

        @Override
        public void onClickFreq(int freq) {
            sendCmd(FinalRadio.C_FREQ, freq);
            setIntentResult();
            finish();
        }
    };

    private void setIntentResult() {
        Intent intent = new Intent();
        intent.putExtra("isCollectFreqChanged", isCollectFreqChanged);
        setResult(RESULT_OK, intent);
    }
}
