package com.wd.radio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ListActivity";

    private int[] freqFMArray = {8820, 8870, 9000, 9150, 9450, 9560, 9740, 1006, 1025, 1039, 1044,
            1077, 9840, 9230, 9430, 9870, 9920, 8820, 8870, 9000, 9150, 9450, 9560, 9740, 1006,
            1025, 1039, 1044, 1077, 9840, 9230, 9430, 9870, 9920};

    private int[] freqAMArray = {560, 580,780,670,620,640,670,710,720,730,740,760,830,840,850,860,880,930,920,940,980,970,990,870,850,870,860,880};

    private View mBtnActionBarBack;
    private View mGroupActionBarFM;
    private View mGroupActionBarAM;
    private ImageView mImgActionBarFM;
    private TextView mTextActionBarFM;
    private ImageView mImgActionBarAM;
    private TextView mTextActionBarAM;

    private RecyclerView mRecyclerView;
    private ListAdapter mListAdapter;

    private int mWorkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mBtnActionBarBack = findViewById(R.id.btn_action_bar_back);
        mBtnActionBarBack.setOnClickListener(this);
        mGroupActionBarFM = findViewById(R.id.action_bar_fm);
        mGroupActionBarFM.setOnClickListener(this);
        mGroupActionBarAM = findViewById(R.id.action_bar_am);
        mGroupActionBarAM.setOnClickListener(this);
        mImgActionBarFM = (ImageView)findViewById(R.id.img_action_bar_fm);
        mTextActionBarFM = (TextView)findViewById(R.id.text_action_bar_fm);
        mImgActionBarAM = (ImageView)findViewById(R.id.img_action_bar_am);
        mTextActionBarAM = (TextView)findViewById(R.id.text_action_bar_am);

        Intent intent = getIntent();
        mWorkMode = intent.getIntExtra(DataUtil.WORK_MODE, DataUtil.WORK_MODE_FM);
        updateActionBar(mWorkMode);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        if(mWorkMode == DataUtil.WORK_MODE_FM) {
            mListAdapter = new ListAdapter(freqFMArray, mWorkMode);
        } else if(mWorkMode == DataUtil.WORK_MODE_AM) {
            mListAdapter = new ListAdapter(freqAMArray, mWorkMode);
        }
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: enter.");
        switch (v.getId()) {
            case R.id.btn_action_bar_back:
                finish();
                break;
            case R.id.action_bar_fm:
                mWorkMode = DataUtil.WORK_MODE_FM;
                updateActionBar(DataUtil.WORK_MODE_FM);
                mListAdapter = new ListAdapter(freqFMArray, mWorkMode);
                mRecyclerView.setAdapter(mListAdapter);
                mListAdapter.notifyDataSetChanged();
                break;
            case R.id.action_bar_am:
                mWorkMode = DataUtil.WORK_MODE_AM;
                updateActionBar(DataUtil.WORK_MODE_AM);
                mListAdapter = new ListAdapter(freqAMArray, mWorkMode);
                mRecyclerView.setAdapter(mListAdapter);
                mListAdapter.notifyDataSetChanged();
                break;
        }
    }


    /**
     * FM 或 AM 模式
     *
     * 0x00：FM 模式
     * 0x01: AM 模式
     * */
    private void updateActionBar(int data) {
        switch (data) {
            case DataUtil.WORK_MODE_FM:
                mImgActionBarFM.setVisibility(View.VISIBLE);
                mImgActionBarAM.setVisibility(View.INVISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_selected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_unselected));
                break;
            case DataUtil.WORK_MODE_AM:
                mImgActionBarFM.setVisibility(View.INVISIBLE);
                mImgActionBarAM.setVisibility(View.VISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_unselected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_selected));
                break;
        }
    }

}
