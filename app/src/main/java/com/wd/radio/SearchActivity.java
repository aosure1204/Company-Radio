package com.wd.radio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mBtnActionBarBack = (Button) findViewById(R.id.btn_action_bar_back);
        mBtnActionBarBack.setOnClickListener(this);

        Intent intent = getIntent();
        mWorkMode = intent.getIntExtra(DataUtil.WORK_MODE, DataUtil.WORK_MODE_FM);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mListAdapter = new ListAdapter(freqArray, mWorkMode);
        mRecyclerView.setAdapter(mListAdapter);
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
            System.out.println("airdemo:onNotify  " + updateCode + ":" + value);

            switch (updateCode) {
                // 监听当前搜索到的频率列表
                case FinalRadio.U_CHANNEL_FREQ:
                    //***************************  此处与别处不同，别处一般返回的是 int 类型，此处返回的是 int数组。
                    if(ints != null) {
                        System.out.println("airdemo:onNotify  " + updateCode + ": ints.length = " + ints.length);
                        onChannelFreqChange(ints);
                    }
                    break;
                    // 监听当前搜台状态，0关 1开
                case FinalRadio.U_SEARCH_STATE:
                    onSearchStateChange(value);
                    break;
            }
        }
    };

    private void onChannelFreqChange(int[] ints) {
        mListAdapter = new ListAdapter(ints, mWorkMode);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * 搜台状态
     *
     * 0关 1开
     * */
    private void onSearchStateChange(int data) {
        switch (data) {
            case DataUtil.TRANSFER_VALUE_00:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: enter.");
        switch (v.getId()) {
            case R.id.btn_action_bar_back:
                finish();
                break;
        }
    }

}
