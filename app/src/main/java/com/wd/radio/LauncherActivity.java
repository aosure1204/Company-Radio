package com.wd.radio;

import android.content.Intent;
import android.support.annotation.Nullable;
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

import com.wd.airdemo.MyApp;
import com.wd.airdemo.module.DataCarbus;
import com.wd.airdemo.module.FinalRadio;
import com.wd.airdemo.module.RemoteTools;
import com.wd.airdemo.util.IUiNotify;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, CollectFreq.LoadFinishCallback {
    private static final String TAG = "LauncherActivity";

    private CollectFreq mCollectFreq;   //保存、操作收藏频率数据

    private int mWorkMode = DataUtil.WORK_MODE_FM;
    private int mCurrentPlayFreq;

    private boolean isShouldEnterSearchAcitivty = false;

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

    private Button mBtnSearch;
    private Button mBtnScan;
    private Button mBtnList;

    private ImageButton mBtnSeekUp;
    private ImageButton mBtnStepUp;
    private ImageButton mBtnStepDown;
    private ImageButton mBtnSeekDown;

    private TextView mTextShowFreqValue;
    private TextView mTextShowFreqType;
    private View mGroupCurrentFreqContainer;

    private ViewGroup mFmCollectEventArea;
    private ViewGroup mAmCollectEventArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // 从文件中加载收藏的频率
        mCollectFreq = CollectFreq.getInstance(this);
        mCollectFreq.setLoadFinishCallback(this);
        mCollectFreq.loadCollectData();

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

        mBtnSearch = (Button)findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mBtnScan = (Button)findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        mBtnList = (Button)findViewById(R.id.btn_list);
        mBtnList.setOnClickListener(this);

        mBtnSeekUp = (ImageButton) findViewById(R.id.btn_seek_up);
        mBtnSeekUp.setOnClickListener(this);
        mBtnSeekDown = (ImageButton) findViewById(R.id.btn_seek_down);
        mBtnSeekDown.setOnClickListener(this);
        mBtnStepUp = (ImageButton) findViewById(R.id.btn_step_up);
        mBtnStepUp.setOnClickListener(this);
        mBtnStepDown = (ImageButton) findViewById(R.id.btn_step_down);
        mBtnStepDown.setOnClickListener(this);

        mTextShowFreqValue = (TextView) findViewById(R.id.text_show_freq_value);
        mTextShowFreqType = (TextView) findViewById(R.id.text_show_freq_type);

        mGroupCurrentFreqContainer = findViewById(R.id.current_freq_container);
        mGroupCurrentFreqContainer.setOnTouchListener(this);  //暂时去掉拖拽动画

        mFmCollectEventArea = findViewById(R.id.fm_collect_event_area);
        mAmCollectEventArea = findViewById(R.id.am_collect_event_area);

        // 从外部指定跳转到 FM / AM 界面
        Intent intent = getIntent();
        if (intent != null) {
            int workMode = intent.getIntExtra(DataUtil.WORK_MODE, -1);
            if(workMode == DataUtil.WORK_MODE_FM) {
                onWorkModeChange(DataUtil.TRANSFER_VALUE_00);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_00);
            } else if(workMode == DataUtil.WORK_MODE_AM) {
                onWorkModeChange(DataUtil.TRANSFER_VALUE_01);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_01);
            }
        }

        // 初始化收藏控件
        initCollectViews();
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

        //保存收藏数据到文件
        mCollectFreq.saveCollectData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApp.getOBJ().requestNullSource();
        MyApp.getOBJ().requestNullApp();
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
            Log.d(TAG, "onNotify: onNotify  " + updateCode + ":" + value);
            switch (updateCode) {
                case FinalRadio.U_BAND:
                    Log.d(TAG, "U_BAND  " + value);
                    onWorkModeChange(value);
                    break;
				case FinalRadio.U_FREQ:
                    Log.d(TAG,"U_FREQ  " + value);
                    onFreqChange(value);
                    break;
				case FinalRadio.U_CHANNEL:
                    Log.d(TAG,"U_CHANNEL  " + value);
					break;
				case FinalRadio.U_SCAN: // 浏览模式
                    Log.d(TAG,"U_SCAN  " + value);
					break;
				case FinalRadio.U_SEARCH_STATE: // 扫台存储模式
                    Log.d(TAG,"U_SEARCH_STATE  " + value);
					break;
				case FinalRadio.U_CHANNEL_FREQ:
					if(DataCarbus.DATA[FinalRadio.U_SEARCH_STATE] == 1)// 搜台且有台才In
					    if(isShouldEnterSearchAcitivty) {   //一定要点击按钮触发了搜索，才跳转界面
                            onSearchStateChange(DataUtil.TRANSFER_VALUE_01);
                        }
					break;
                case FinalRadio.U_CURRENT_FREQ: // 当前播放的频率
                    mCurrentPlayFreq = DataCarbus.DATA[FinalRadio.U_CURRENT_FREQ];
                    refreshCollectViews();
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
                MyApp.getOBJ().requestFMApp();
                mImgActionBarFM.setVisibility(View.VISIBLE);
                mImgActionBarAM.setVisibility(View.INVISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_selected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_unselected));
                mContentContainer.setBackgroundResource(R.drawable.bg_fm);
                mFmCollectContainer.setVisibility(View.VISIBLE);
                mAmCollectContainer.setVisibility(View.GONE);
                mTextShowFreqType.setText(R.string.mhz);
                switchCollectionViews(DataUtil.WORK_MODE_FM);
                //... unfinished
                break;
            case DataUtil.TRANSFER_VALUE_01:
                mWorkMode = DataUtil.WORK_MODE_AM;
                MyApp.getOBJ().requestAMApp();
                mImgActionBarFM.setVisibility(View.INVISIBLE);
                mImgActionBarAM.setVisibility(View.VISIBLE);
                mTextActionBarFM.setTextColor(getColor(R.color.action_title_unselected));
                mTextActionBarAM.setTextColor(getColor(R.color.action_title_selected));
                mContentContainer.setBackgroundResource(R.drawable.bg_am);
                mFmCollectContainer.setVisibility(View.GONE);
                mAmCollectContainer.setVisibility(View.VISIBLE);
                mTextShowFreqType.setText(R.string.khz);
                switchCollectionViews(DataUtil.WORK_MODE_AM);
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
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                isShouldEnterSearchAcitivty = false;

                Intent intent = getIntent();
                boolean isCollectFreqChanged = intent.getBooleanExtra("isCollectFreqChanged", false);
                if(isCollectFreqChanged) {
                    refreshCollectViews();
                }
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
            case R.id.action_bar_fm:
                onWorkModeChange(DataUtil.TRANSFER_VALUE_00);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_00);
                completeCollectPosition();
                break;
            case R.id.action_bar_am:
                onWorkModeChange(DataUtil.TRANSFER_VALUE_01);
                sendCmd(FinalRadio.C_BAND, DataUtil.TRANSFER_VALUE_01);
                completeCollectPosition();
                break;
            case R.id.btn_search:
                sendCmd(FinalRadio.C_SEARCH, DataUtil.TRANSFER_VALUE_01);
                isShouldEnterSearchAcitivty = true;
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
            case R.id.btn_seek_down:
                sendCmd(FinalRadio.C_SEEK_DOWN, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.btn_step_up:
                sendCmd(FinalRadio.C_FREQ_UP, DataUtil.TRANSFER_VALUE_00);
                break;
            case R.id.btn_step_down:
                sendCmd(FinalRadio.C_FREQ_DOWN, DataUtil.TRANSFER_VALUE_00);
                break;
        }
    }

    //******************************* 收藏区域功能
    private static final int FM_COLLECT_NUM = 12;
    private static final int AM_COLLECT_NUM = 6;

    private boolean initViewFinish;

    private boolean isFMDeleteState;
    private boolean isAMDeleteState;

    private ViewGroup[] mFmCollectItemContainer;
    private ViewGroup[] mAmCollectItemContainer;
    private ImageButton[] mFmBtnCollectItemCurrent;
    private Button[] mFmBtnCollectItemFreq;
    private ImageButton[] mAmBtnCollectItemCurrent;
    private Button[] mAmBtnCollectItemFreq;
    private ImageView[] mFmImgCollectItemAdd;
    private ImageView[] mAmImgCollectItemAdd;
    private ImageButton[] mFmBtnCollectItemDelete;
    private ImageButton[] mAmBtnCollectItemDelete;
    private ViewGroup mFmCollectDeleteContainer;
    private ViewGroup mAmCollectDeleteContainer;

    private ViewGroup[] mCollectItemContainer;
    private int[] mCollectItemBgResId;
    private ImageButton[] mBtnCollectItemCurrent;
    private Button[] mBtnCollectItemFreq;
    private ImageView[] mImgCollectItemAdd;
    private ImageButton[] mBtnCollectItemDelete;

    private int[] collectFreq;

    private int[] mFmCollectItemBgResId;
    private int[] mAmCollectItemBgResId;

    private void initCollectViews() {
        mFmCollectDeleteContainer = (ViewGroup) findViewById(R.id.fm_collect_delete_container);
        mAmCollectDeleteContainer = (ViewGroup) findViewById(R.id.am_collect_delete_container);

        mFmCollectItemContainer = new ViewGroup[FM_COLLECT_NUM];
        mFmBtnCollectItemCurrent = new ImageButton[FM_COLLECT_NUM];
        mFmBtnCollectItemFreq = new Button[FM_COLLECT_NUM];
        mFmImgCollectItemAdd = new ImageView[FM_COLLECT_NUM];
        for(int i = 0; i < FM_COLLECT_NUM; i++) {
            mFmCollectItemContainer[i] = (ViewGroup) mFmCollectEventArea.getChildAt(i);
            mFmBtnCollectItemCurrent[i] = (ImageButton) mFmCollectItemContainer[i].findViewById(R.id.btn_collect_item_current);
            mFmBtnCollectItemCurrent[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFMDeleteState) {
                        int freqValue = (Integer) v.getTag();
                        playFreq(freqValue);
                    } else {
                        exitDeleteState(mWorkMode);
                    }
                }
            });
            mFmBtnCollectItemCurrent[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterDeleteState(mWorkMode);
                    return true;
                }
            });
            mFmBtnCollectItemFreq[i] = (Button) mFmCollectItemContainer[i].findViewById(R.id.btn_collect_item_freq);
            mFmBtnCollectItemFreq[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFMDeleteState) {
                        int freqValue = (Integer) v.getTag();
                        playFreq(freqValue);
                    } else {
                        exitDeleteState(mWorkMode);
                    }
                }
            });
            mFmBtnCollectItemFreq[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterDeleteState(mWorkMode);
                    return true;
                }
            });
            mFmImgCollectItemAdd[i] = (ImageView) mFmCollectItemContainer[i].findViewById(R.id.img_collect_item_add);
        }

        mAmCollectItemContainer = new ViewGroup[AM_COLLECT_NUM];
        mAmBtnCollectItemCurrent = new ImageButton[AM_COLLECT_NUM];
        mAmBtnCollectItemFreq = new Button[AM_COLLECT_NUM];
        mAmImgCollectItemAdd = new ImageView[AM_COLLECT_NUM];
        for(int i = 0; i < AM_COLLECT_NUM; i++) {
            mAmCollectItemContainer[i] = (ViewGroup) mAmCollectEventArea.getChildAt(i);
            mAmBtnCollectItemCurrent[i] = (ImageButton) mAmCollectItemContainer[i].findViewById(R.id.btn_collect_item_current);
            mAmBtnCollectItemCurrent[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isAMDeleteState) {
                        int freqValue = (Integer) v.getTag();
                        playFreq(freqValue);
                    } else {
                        exitDeleteState(mWorkMode);
                    }
                }
            });
            mAmBtnCollectItemCurrent[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterDeleteState(mWorkMode);
                    return true;
                }
            });
            mAmBtnCollectItemFreq[i] = (Button) mAmCollectItemContainer[i].findViewById(R.id.btn_collect_item_freq);
            mAmBtnCollectItemFreq[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isAMDeleteState) {
                        int freqValue = (Integer) v.getTag();
                        playFreq(freqValue);
                    } else {
                        exitDeleteState(mWorkMode);
                    }
                }
            });
            mAmBtnCollectItemFreq[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterDeleteState(mWorkMode);
                    return true;
                }
            });
            mAmImgCollectItemAdd[i] = (ImageView) mAmCollectItemContainer[i].findViewById(R.id.img_collect_item_add);
        }

        mFmBtnCollectItemDelete = new ImageButton[FM_COLLECT_NUM];
        for(int i = 0; i < FM_COLLECT_NUM; i++) {
            mFmBtnCollectItemDelete[i] = (ImageButton) mFmCollectDeleteContainer.getChildAt(i);
            mFmBtnCollectItemDelete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int freqIndex = (Integer) v.getTag();
                    deleteCollectFreq(mWorkMode, freqIndex);
                }
            });
        }

        mAmBtnCollectItemDelete = new ImageButton[AM_COLLECT_NUM];
        for(int i = 0; i < AM_COLLECT_NUM; i++) {
            mAmBtnCollectItemDelete[i] = (ImageButton) mAmCollectDeleteContainer.getChildAt(i);
            mAmBtnCollectItemDelete[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int freqIndex = (Integer) v.getTag();
                    deleteCollectFreq(mWorkMode, freqIndex);
                }
            });
        }

        mFmCollectItemBgResId = new int[]{R.drawable.bg_fm_1, R.drawable.bg_fm_2, R.drawable.bg_fm_3, R.drawable.bg_fm_4,
                R.drawable.bg_fm_5, R.drawable.bg_fm_6, R.drawable.bg_fm_7, R.drawable.bg_fm_8, R.drawable.bg_fm_9,
                R.drawable.bg_fm_10, R.drawable.bg_fm_11, R.drawable.bg_fm_12};

        mAmCollectItemBgResId = new int[]{R.drawable.bg_am_1, R.drawable.bg_am_2, R.drawable.bg_am_3, R.drawable.bg_am_4,
                R.drawable.bg_am_5, R.drawable.bg_am_6};

        mFmCollectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDeleteState(DataUtil.WORK_MODE_FM);
            }
        });
        mFmCollectContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                exitDeleteState(DataUtil.WORK_MODE_FM);
                return true;
            }
        });

        mAmCollectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDeleteState(DataUtil.WORK_MODE_AM);
            }
        });
        mAmCollectContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                exitDeleteState(DataUtil.WORK_MODE_AM);
                return true;
            }
        });

        // 初始化完后，更新收藏区域。
        initViewFinish = true;
        switchCollectionViews(mWorkMode);
        refreshCollectViews();
    }

    private void switchCollectionViews(int workMode) {
        collectFreq = mCollectFreq.getCollectFreq(workMode);
        if(workMode == DataUtil.WORK_MODE_AM) {
            mCollectItemContainer = mAmCollectItemContainer;
            mCollectItemBgResId = mAmCollectItemBgResId;
            mBtnCollectItemCurrent = mAmBtnCollectItemCurrent;
            mBtnCollectItemFreq = mAmBtnCollectItemFreq;
            mImgCollectItemAdd = mAmImgCollectItemAdd;
            mBtnCollectItemDelete = mAmBtnCollectItemDelete;
        } else {
            mCollectItemContainer = mFmCollectItemContainer;
            mCollectItemBgResId = mFmCollectItemBgResId;
            mBtnCollectItemCurrent = mFmBtnCollectItemCurrent;
            mBtnCollectItemFreq = mFmBtnCollectItemFreq;
            mImgCollectItemAdd = mFmImgCollectItemAdd;
            mBtnCollectItemDelete = mFmBtnCollectItemDelete;
        }
    }

    private void refreshCollectViews() {
        //控件还未初始化完成
        if(!initViewFinish) {
            return;
        }

        for(int i = 0; i < collectFreq.length; i++) {
            int freq = collectFreq[i];

            if(freq == 0) {
                //如果收藏频率为空，则显示+号；
                setCollectViewNullFreq(i);
            } else if(freq == mCurrentPlayFreq) {
                //如果收藏的频率为当前频率,则高亮显示
                setCollectViewHighlightFreq(i, mCurrentPlayFreq);
            } else {
                setCollectViewNormalFreq(i, freq);
            }
        }
    }

    private void setCollectViewNullFreq(int freqIndex) {
        Log.d(TAG, "mWorkMode = " + mWorkMode + ", freqIndex = " + freqIndex);
        //空频率显示+号
        mCollectItemContainer[freqIndex].setBackgroundResource(0);
        mBtnCollectItemCurrent[freqIndex].setVisibility(View.GONE);
        mBtnCollectItemFreq[freqIndex].setVisibility(View.INVISIBLE);
        mImgCollectItemAdd[freqIndex].setVisibility(View.VISIBLE);
        mBtnCollectItemDelete[freqIndex].setVisibility(View.INVISIBLE);
    }

    private void deleteCollectFreq(int workMode, int freqIndex) {
        //将频率从 DataUtil.mFMCollectFreq / mAMCollectFreq 删除
        collectFreq[freqIndex] = 0;

        //更新控件: 删除收藏的频率，则显示+号；
        setCollectViewNullFreq(freqIndex);
    }

    private void setCollectViewHighlightFreq(int freqIndex, int freqValue) {
        //如果收藏的频率为当前频率,则高亮显示
        mCollectItemContainer[freqIndex].setBackgroundResource(mCollectItemBgResId[freqIndex]);
        mBtnCollectItemCurrent[freqIndex].setVisibility(View.VISIBLE);
        mBtnCollectItemFreq[freqIndex].setVisibility(View.VISIBLE);
        if(mWorkMode == DataUtil.WORK_MODE_AM) {
            mBtnCollectItemFreq[freqIndex].setText(String.valueOf(freqValue) + getResources().getString(R.string.khz));
        } else {
            mBtnCollectItemFreq[freqIndex].setText(DataUtil.formatFMFreq(freqValue) + getResources().getString(R.string.mhz));
        }
        mBtnCollectItemFreq[freqIndex].setTextColor(getColor(R.color.current_freq_color));
        mImgCollectItemAdd[freqIndex].setVisibility(View.INVISIBLE);
        mBtnCollectItemDelete[freqIndex].setVisibility(View.VISIBLE);

        mBtnCollectItemCurrent[freqIndex].setTag(freqValue);
        mBtnCollectItemFreq[freqIndex].setTag(freqValue);
        mBtnCollectItemDelete[freqIndex].setTag(freqIndex);
    }

    private void addCollectFreq(int workMode, int freqIndex, int freqValue) {
        //将频率添加到 DataUtil.mFMCollectFreq / mAMCollectFreq。
        collectFreq[freqIndex] = freqValue;

        setCollectViewHighlightFreq(freqIndex, freqValue);
    }

    private void setCollectViewNormalFreq(int freqIndex, int freqValue) {
        //一般情况：收藏的频率既不是当前播放的频率，也不为空。
        mCollectItemContainer[freqIndex].setBackgroundResource(0);
        mBtnCollectItemCurrent[freqIndex].setVisibility(View.GONE);
        mBtnCollectItemFreq[freqIndex].setVisibility(View.VISIBLE);
        if(mWorkMode == DataUtil.WORK_MODE_AM) {
            mBtnCollectItemFreq[freqIndex].setText(String.valueOf(freqValue) + getResources().getString(R.string.khz));
        } else {
            mBtnCollectItemFreq[freqIndex].setText(DataUtil.formatFMFreq(freqValue) + getResources().getString(R.string.mhz));
        }
        mBtnCollectItemFreq[freqIndex].setTextColor(getColor(R.color.other_freq_color));
        mImgCollectItemAdd[freqIndex].setVisibility(View.INVISIBLE);
        mBtnCollectItemDelete[freqIndex].setVisibility(View.VISIBLE);

        mBtnCollectItemCurrent[freqIndex].setTag(freqValue);
        mBtnCollectItemFreq[freqIndex].setTag(freqValue);
        mBtnCollectItemDelete[freqIndex].setTag(freqIndex);
    }

    private void playFreq(int freqValue) {
        sendCmd(FinalRadio.C_FREQ, freqValue);
    }

    private void enterDeleteState(int workMode) {
        if(workMode == DataUtil.WORK_MODE_AM) {
            isAMDeleteState = true;
            mAmCollectDeleteContainer.setVisibility(View.VISIBLE);
        } else {
            isFMDeleteState = true;
            mFmCollectDeleteContainer.setVisibility(View.VISIBLE);
        }
    }

    private void exitDeleteState(int workMode) {
        if(workMode == DataUtil.WORK_MODE_AM) {
            isAMDeleteState = false;
            mAmCollectDeleteContainer.setVisibility(View.INVISIBLE);
        } else {
            isFMDeleteState = false;
            mFmCollectDeleteContainer.setVisibility(View.INVISIBLE);
        }
    }

    // 当从文件读取收藏频率完成时回调，用于更新UI。
    @Override
    public void onLoadFinish(int workMode) {
        if(workMode == mWorkMode) {
            refreshCollectViews();
        }
    }

    //******************************* 当前频率拖拽动画
    private float mDownX;
    private float mDownY;

    private static final int DRAG_CRITICA_INSTANCE = 10;
    private boolean enterDragMode;
    int currentPosition = -2;

    private boolean isAmCollectPositionCompleted = false;
    private boolean isFmCollectPositionCompleted = false;

    private int[] amCollectEventAreaX = new int[7];
    private int amCollectEventAreaTop;
    private int amCollectEventAreaBottom;

    private int[][] fmCollectEventAreaX = new int[2][7];
    private int fmCollectEventAreaTopFirst;
    private int fmCollectEventAreaTopSecond;
    private int fmCollectEventAreaBottom;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            completeCollectPosition();
        }
    }

    private void completeCollectPosition() {
        int[] location = new int[2];
        if(mWorkMode == DataUtil.WORK_MODE_AM && !isAmCollectPositionCompleted) {
            isAmCollectPositionCompleted = true;
            //获取AM收藏区域在窗口Y轴上位置范围
            mAmCollectEventArea.getLocationInWindow(location);
            amCollectEventAreaTop = location[1];
            amCollectEventAreaBottom = amCollectEventAreaTop + mAmCollectEventArea.getHeight();

            //获取AM收藏区域的6个位置，在窗口X轴上位置
            for (int i = 0; i < 6; i++) {
                mAmCollectItemContainer[i].getLocationInWindow(location);
                amCollectEventAreaX[i] = location[0];
            }
            amCollectEventAreaX[6] = location[0] + mAmCollectItemContainer[5].getWidth();
        } else if(mWorkMode == DataUtil.WORK_MODE_FM && !isFmCollectPositionCompleted) {
            isFmCollectPositionCompleted = true;
            //获取FM收藏区域在窗口Y轴上位置范围
            mFmCollectEventArea.getLocationInWindow(location);
            fmCollectEventAreaTopFirst = location[1];
            fmCollectEventAreaTopSecond = fmCollectEventAreaTopFirst + mFmCollectEventArea.getHeight() / 2;
            fmCollectEventAreaBottom = fmCollectEventAreaTopFirst + mFmCollectEventArea.getHeight();

            //获取FM收藏区域的6个位置，在窗口X轴上位置
            for(int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    mFmCollectItemContainer[i * 6 + j].getLocationInWindow(location);
                    fmCollectEventAreaX[i][j] = location[0];
                }
                fmCollectEventAreaX[i][6] = location[0] + mFmCollectItemContainer[i * 6 + 5].getWidth();
            }
        }
    }

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
                        allowCollectViewsInvisible();
                    }
                } else { //已经进入拖拽模式时，处理拖拽逻辑
                    Log.d(TAG, "onTouch: enterDragMode");

                    int left = (int)(v.getLeft() + (x - mDownX));
                    int top = (int) (v.getTop() + (y - mDownY));
                    int right = left + v.getWidth();
                    int bottom = top + v.getHeight();
                    v.layout(left, top, right, bottom);

                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    float currentCenterX = (location[0] * 2 + v.getWidth()) / 2.0f;
                    float currentCenterY = (location[1] * 2 + v.getHeight()) / 2.0f;

                    if(mWorkMode == DataUtil.WORK_MODE_AM) {    //AM
                        //确定当前播放频率是否落在收藏区域，及落在收藏区域的6个位置中的哪一个。
                        if (currentCenterY > amCollectEventAreaTop && currentCenterY < amCollectEventAreaBottom) {
                            for (int i = 0; i < amCollectEventAreaX.length; i++) {
                                //收藏区域有6个位置，判断当前播放频率的X轴中心点，落在哪个位置
                                if (currentCenterX < amCollectEventAreaX[i]) {
                                    //只有空的快捷频率区域，才能添加频率
                                    if (i > 0 && collectFreq[i - 1] == 0) {
                                        currentPosition = i - 1;
                                    }
                                    break;
                                }
                            }
                        }
                    } else {    //FM
                        //确定当前播放频率是否落在收藏区域，及落在收藏区域的6个位置中的哪一个。
                        int row = -1;
                        if (currentCenterY > fmCollectEventAreaTopFirst && currentCenterY < fmCollectEventAreaTopSecond) {
                            row = 0;
                        } else if(currentCenterY >= fmCollectEventAreaTopSecond && currentCenterY < fmCollectEventAreaBottom) {
                            row = 1;
                        }

                        if(row >= 0) {
                            for (int i = 0; i < fmCollectEventAreaX[row].length; i++) {
                                //收藏区域有6个位置，判断当前播放频率的X轴中心点，落在哪个位置
                                if (currentCenterX < fmCollectEventAreaX[row][i]) {
                                    //只有空的快捷频率区域，才能添加频率
                                    if (i > 0 && collectFreq[row * 6 + i - 1] == 0) {
                                        currentPosition = row * 6 + i - 1;
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    allowCollectViewsInvisible();
                    //如果currentPosition < 0，说明当前播放频率的X轴中心点，没有在收藏区域范围内；或者是所在收藏区域已经有收藏频率了。
                    if (currentPosition >= 0) {
                        mCollectItemContainer[currentPosition].setVisibility(View.VISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouch ACTION_UP: currentPosition = " + currentPosition);
                if(currentPosition >= 0) {
                    addCollectFreq(mWorkMode, currentPosition, mCurrentPlayFreq);

                    //将当前播放频率设置到收藏列表
/*                    Button btnCollectItemFreq = (Button)mAmCollectItemContainer[currentPosition].findViewById(R.id.btn_collect_item_freq);

                   int[] location = new int[2];

                    //获取目标位置、宽度、高度
                    int toWidth = btnCollectItemFreq.getWidth();
                    int toHeight = btnCollectItemFreq.getHeight();
                    btnCollectItemFreq.getLocationInWindow(location);
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
                    int diffBottom = (int) (toLocationInWindowY + toHeight - fromLocationInWindowY - fromHeight);*/
/*                    Log.d(TAG, "onTouch: diffLeft = " + diffLeft + ", diffTop = " + diffTop + ", diffRight = " + diffRight + ", diffBottom = " + diffBottom);

                    Log.d(TAG, "onTouch: getLeft = " + v.getLeft() + ", getTop = " + v.getTop() + ", getRight = " + v.getRight() + ", getBottom = " + v.getBottom());
                    v.layout(v.getLeft() + diffLeft, v.getTop() + diffTop, v.getRight() + diffRight, v.getBottom() + diffBottom);*/

                    /*ObjectAnimator.ofInt(v, "left", diffLeft)
                            .ofInt(v, "top", diffTop)
                            .ofInt(v, "right", diffRight)
                            .ofInt(v, "bottom", diffBottom)
                            .start();*/
                } else {
                    v.requestLayout();
                }

                //在 ACTION_MOVE 过程中，mCollectItemContainer中部分可能被隐藏，现在该让它们都呈现出来。
                for (int i = 0; i < mCollectItemContainer.length; i++) {
                    mCollectItemContainer[i].setVisibility(View.VISIBLE);
                }
                break;
        }

        return true;
    }

    private void allowCollectViewsInvisible() {
        for (int i = 0; i < mCollectItemContainer.length; i++) {
            //只有空的快捷频率区域，才能添加频率。故非空频率区域UI不应改变。
            if (collectFreq[i] == 0) {
                mCollectItemContainer[i].setVisibility(View.INVISIBLE);
            }
        }
    }
}
