package com.wd.radio;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wd.airdemo.module.DataCarbus;
import com.wd.airdemo.module.FinalMain;
import com.wd.airdemo.module.FinalRadio;
import com.wd.airdemo.module.FinalSound;
import com.wd.airdemo.module.RemoteTools;
import com.wd.airdemo.util.IUiNotify;

import java.text.DecimalFormat;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "LauncherActivity";

    private int mWorkMode;

    private View mBtnActionBarBack;
    private View mGroupActionBarFM;
    private View mGroupActionBarAM;
    private ImageView mImgActionBarFM;
    private TextView mTextActionBarFM;
    private ImageView mImgActionBarAM;
    private TextView mTextActionBarAM;

    private View mContentContainer;
    private View mFmCollectContainer;
    private View mAmCollectContainer;
    private View mFmCollectEventArea;
    private View mAmCollectEventArea;

    private View mGroupCurrentFreqContainer;

    private Button mBtnSearch;
    private Button mBtnScan;
    private Button mBtnList;

    private ImageButton mBtnSeekUp;
    private ImageButton mBtnStepUp;
    private ImageButton mBtnStepDown;
    private ImageButton mBtnSeekDown;

    private ViewGroup[] collectEventAreaViews = new ViewGroup[6];
    private float[] collectEventAreaX = new float[7];
    private float collectEventAreaTop;
    private float collectEventAreaBottom;
    private TextView mTextShowFreqValue;
    private TextView mTextShowFreqType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

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

        mContentContainer = findViewById(R.id.content_container);
        mFmCollectContainer = findViewById(R.id.fm_collect_container);
        mAmCollectContainer = findViewById(R.id.am_collect_container);
        mFmCollectEventArea = findViewById(R.id.fm_collect_event_area);
        mAmCollectEventArea = findViewById(R.id.am_collect_event_area);

        mBtnSearch = (Button)findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mBtnScan = (Button)findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        mBtnList = (Button)findViewById(R.id.btn_list);
        mBtnList.setOnClickListener(this);

        mBtnSeekUp = (ImageButton) findViewById(R.id.btn_seek_up);
        mBtnSeekUp.setOnClickListener(this);
        mBtnStepUp = (ImageButton) findViewById(R.id.btn_step_up);
        mBtnStepUp.setOnClickListener(this);
        mBtnStepDown = (ImageButton) findViewById(R.id.btn_step_down);
        mBtnStepDown.setOnClickListener(this);
        mBtnSeekDown = (ImageButton) findViewById(R.id.btn_seek_down);
        mBtnSeekDown.setOnClickListener(this);

        mTextShowFreqValue = (TextView) findViewById(R.id.text_show_freq_value);
        mTextShowFreqType = (TextView) findViewById(R.id.text_show_freq_type);

        mGroupCurrentFreqContainer = findViewById(R.id.current_freq_container);
//        mGroupCurrentFreqContainer.setOnTouchListener(this);  //暂时去掉拖拽动画

        collectEventAreaViews[0] = (ViewGroup) findViewById(R.id.am_1);
        collectEventAreaViews[1] = (ViewGroup) findViewById(R.id.am_2);
        collectEventAreaViews[2] = (ViewGroup) findViewById(R.id.am_3);
        collectEventAreaViews[3] = (ViewGroup) findViewById(R.id.am_4);
        collectEventAreaViews[4] = (ViewGroup) findViewById(R.id.am_5);
        collectEventAreaViews[5] = (ViewGroup) findViewById(R.id.am_6);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            //获取收藏区域在窗口Y轴上位置范围
            int[] location = new int[2];
            mAmCollectEventArea.getLocationInWindow(location);
            collectEventAreaTop = location[1];
            collectEventAreaBottom = collectEventAreaTop + mAmCollectEventArea.getHeight();

            //获取收藏区域的6个位置，在窗口X轴上位置
            collectEventAreaViews[0].getLocationInWindow(location);
            collectEventAreaX[0] = location[0];
            collectEventAreaViews[1].getLocationInWindow(location);
            collectEventAreaX[1] = location[0];
            collectEventAreaViews[2].getLocationInWindow(location);
            collectEventAreaX[2] = location[0];
            collectEventAreaViews[3].getLocationInWindow(location);
            collectEventAreaX[3] = location[0];
            collectEventAreaViews[4].getLocationInWindow(location);
            collectEventAreaX[4] = location[0];
            collectEventAreaViews[5].getLocationInWindow(location);
            collectEventAreaX[5] = location[0];
            collectEventAreaX[6] = location[0] + collectEventAreaViews[5].getWidth();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerDataListener();

        //仅用于测试，正式版本应该去掉这行代码 //***********************************
        onWorkModeChange(DataUtil.TRANSFER_VALUE_00);
        //***********************************
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
                case FinalRadio.U_BAND:
                    onWorkModeChange(value);
                    break;
                case FinalRadio.U_FREQ:
                    onFreqChange(value);
                    break;
                case FinalRadio.U_SEARCH_STATE:
                    onSearchStateChange(value);
                    break;
            }
        }
    };

    /**
     * FM 或 AM 模式
     *
     * 0x00：FM 模式
     * 0x01: AM 模式
     * */
    private void onWorkModeChange(int data) {
        switch (data) {
            case DataUtil.TRANSFER_VALUE_00:
                mWorkMode = DataUtil.WORK_MODE_FM;
                mImgActionBarFM.setVisibility(View.VISIBLE);
                mImgActionBarAM.setVisibility(View.INVISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_selected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_unselected));
                mContentContainer.setBackgroundResource(R.drawable.bg_fm);
                mFmCollectContainer.setVisibility(View.VISIBLE);
                mAmCollectContainer.setVisibility(View.GONE);
                mTextShowFreqType.setText(R.string.mhz);
                //... unfinished
                break;
            case DataUtil.TRANSFER_VALUE_01:
                mWorkMode = DataUtil.WORK_MODE_AM;
                mImgActionBarFM.setVisibility(View.INVISIBLE);
                mImgActionBarAM.setVisibility(View.VISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_unselected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_selected));
                mContentContainer.setBackgroundResource(R.drawable.bg_am);
                mFmCollectContainer.setVisibility(View.GONE);
                mAmCollectContainer.setVisibility(View.VISIBLE);
                mTextShowFreqType.setText(R.string.khz);
                //... unfinished
                break;
        }
    }

    /**
     * 当前频率
     *
     * 整型，FM需/100，AM正常显示即可。
     * */
    private void onFreqChange(int data) {
        switch (mWorkMode) {
            case DataUtil.WORK_MODE_FM:
                mTextShowFreqValue.setText(DataUtil.formatFMFreq(data));
                mTextShowFreqType.setText(R.string.mhz);
                break;
            case DataUtil.WORK_MODE_AM:
                mTextShowFreqValue.setText(String.valueOf(data));
                mTextShowFreqType.setText(R.string.khz);
                break;
        }
    }

    /**
     * 当前搜台状态
     *
     * 0关，1开
     * */
    private void onSearchStateChange(int data) {
        if(data == DataUtil.TRANSFER_VALUE_01) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(DataUtil.WORK_MODE, mWorkMode);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: enter.");
        switch (v.getId()) {
            case R.id.btn_action_bar_back:
                finish();
                break;
            case R.id.action_bar_fm:
                onWorkModeChange(DataUtil.TRANSFER_VALUE_00);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.action_bar_am:
                onWorkModeChange(DataUtil.TRANSFER_VALUE_01);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_01);
                break;
            case R.id.btn_search:
                sendCmd(FinalRadio.C_SEARCH, DataUtil.TRANSFER_VALUE_01);
                onSearchStateChange(1); //*********** 仅用于调试，发布软件时去掉此项
                break;
            case R.id.btn_scan:
                sendCmd(FinalRadio.C_SCAN, DataUtil.TRANSFER_VALUE_01);
                break;
            case R.id.btn_list:
                Intent intent = new Intent(this, ListActivity.class);
                intent.putExtra(DataUtil.WORK_MODE, mWorkMode);
                startActivity(intent);
                break;
            case R.id.btn_seek_up:
                sendCmd(FinalRadio.C_SEEK_UP, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.btn_step_up:
                sendCmd(FinalRadio.C_FREQ_UP, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.btn_step_down:
                sendCmd(FinalRadio.C_FREQ_DOWN, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.btn_seek_down:
                sendCmd(FinalRadio.C_SEEK_DOWN, DataUtil.TRANSFER_VALUE_00);
                break;
        }
    }

    private float mDownX;
    private float mDownY;

    private static final int DRAG_CRITICA_INSTANCE = 10;
    private boolean enterDragMode;
    int currentPosition = -2;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() != R.id.current_freq_container) {
            return false;
        }
        
        float x = event.getX();
        float y = event.getY();
        Log.d(TAG, "onTouch:event.action = " + event.getAction() + ", event.x = " + x + ", event.y = " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                enterDragMode = false;
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentPosition = -2;

                //未进入拖拽模式时，进行拖拽条件判断
                if (!enterDragMode) {
                    float dragX = Math.abs(x - mDownX);
                    float dragY = Math.abs(y - mDownY);

                    if (dragX > DRAG_CRITICA_INSTANCE || dragY > DRAG_CRITICA_INSTANCE) {
                        enterDragMode = true;
                    }
                } else { //已经进入拖拽模式时，处理拖拽逻辑
                    Log.d(TAG, "onTouch: enterDragMode");
/*                    v.setTranslationX(v.getTranslationX() + (x - mDownX));
                    v.setTranslationY(v.getTranslationY() + (y - mDownY));*/

                    int left = (int)(v.getLeft() + (x - mDownX));
                    int top = (int) (v.getTop() + (y - mDownY));
                    int right = left + v.getWidth();
                    int bottom = top + v.getHeight();
                    v.layout(left, top, right, bottom);

                    //当前播放频率
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    float currentCenterX = (location[0] * 2 + v.getWidth()) / 2.0f;
                    float currentCenterY = (location[1] * 2 + v.getHeight()) / 2.0f;

                    //确定播放频率是否落在收藏区域，及落在收藏区域的6个位置中的哪一个。
                    //当前播放频率的Y轴中心点，在收藏区域范围内
                    if(currentCenterY > collectEventAreaTop && currentCenterY < collectEventAreaBottom) {
                        for(int i = 0; i < collectEventAreaX.length; i++) {
                            //收藏区域有6个位置，判断当前播放频率的X轴中心点，落在哪个位置
                            if(currentCenterX < collectEventAreaX[i]) {
                                currentPosition = i - 1;
                                break;
                            }
                        }
                    }
                    for(int i = 0; i < collectEventAreaViews.length; i++) {
                        collectEventAreaViews[i].setVisibility(View.INVISIBLE);
                    }
                    //如果currentPosition < 0，说明当前播放频率的X轴中心点，没有在收藏区域范围内
                    if(currentPosition >= 0) {
                        collectEventAreaViews[currentPosition].setVisibility(View.VISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentPosition >= 0) {
                    //将当前播放频率设置到收藏列表
                    TextView textCollectItemFreq = (TextView)collectEventAreaViews[currentPosition].findViewById(R.id.text_collect_item_freq);
//                    textCollectItemFreq.setText("98MHz");

                    //获取目标位置、宽度、高度
                    int toWidth = textCollectItemFreq.getWidth();
                    int toHeight = textCollectItemFreq.getHeight();
                    int[] location = new int[2];
                    textCollectItemFreq.getLocationInWindow(location);
                    float toLocationInWindowX = location[0];
                    float toLocationInWindowY = location[1];

                    //获取当前位置、宽度、高度
                    int fromWidth = v.getWidth();
                    int fromHeight = v.getHeight();
                    v.getLocationInWindow(location);
                    float fromLocationInWindowX = location[0];
                    float fromLocationInWindowY = location[1];

                    int diffLeft = (int) (toLocationInWindowX - fromLocationInWindowX);
                    int diffTop = (int) (toLocationInWindowY - fromLocationInWindowY);
                    int diffRight = (int) (toLocationInWindowX + toWidth - fromLocationInWindowX - fromWidth);
                    int diffBottom = (int) (toLocationInWindowY + toHeight - fromLocationInWindowY - fromHeight);
/*                    Log.d(TAG, "onTouch: diffLeft = " + diffLeft + ", diffTop = " + diffTop + ", diffRight = " + diffRight + ", diffBottom = " + diffBottom);

                    Log.d(TAG, "onTouch: getLeft = " + v.getLeft() + ", getTop = " + v.getTop() + ", getRight = " + v.getRight() + ", getBottom = " + v.getBottom());
                    v.layout(v.getLeft() + diffLeft, v.getTop() + diffTop, v.getRight() + diffRight, v.getBottom() + diffBottom);*/

                    ObjectAnimator.ofInt(v, "left", diffLeft)
                            .ofInt(v, "top", diffTop)
                            .ofInt(v, "right", diffRight)
                            .ofInt(v, "bottom", diffBottom)
                            .start();
                }
                break;
        }

        return true;
    }
}
