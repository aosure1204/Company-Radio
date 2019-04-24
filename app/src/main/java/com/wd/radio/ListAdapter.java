package com.wd.radio;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wd.airdemo.module.DataCarbus;
import com.wd.airdemo.module.FinalRadio;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private static final String TAG = "ListAdapter";

    private int[] mFreqArray;
    private int mWorkMode = DataUtil.WORK_MODE_FM;
    private String mFmAm;
    private String mMhzKhz;

    private Context mContext;

    public ListAdapter(int[] freqArray, int workMode, Context context) {
        mFreqArray = freqArray;
        mWorkMode = workMode;
        mContext = context;
        if (mWorkMode == DataUtil.WORK_MODE_FM) {
            mFmAm = "FM";
            mMhzKhz = "MHz";
        } else if(mWorkMode == DataUtil.WORK_MODE_AM) {
            mFmAm = "AM";
            mMhzKhz = "KHz";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textFreqValue;
        TextView textFmAm;
        TextView textMhzKhz;
//        ImageButton btnCollection;
        ImageView btnCollection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFreqValue = (TextView) itemView.findViewById(R.id.item_freq_value);
            textFmAm = (TextView) itemView.findViewById(R.id.item_fm_am);
            textMhzKhz = (TextView) itemView.findViewById(R.id.item_mhz_khz);
//            btnCollection = (ImageButton) itemView.findViewById(R.id.item_btn_collection);
            btnCollection = (ImageView) itemView.findViewById(R.id.item_btn_collection);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder viewHolder, int i) {
        if (mWorkMode == DataUtil.WORK_MODE_FM) {
            viewHolder.textFreqValue.setText(DataUtil.formatFMFreq(mFreqArray[i]));
        } else if(mWorkMode == DataUtil.WORK_MODE_AM) {
            viewHolder.textFreqValue.setText(String.valueOf(mFreqArray[i]));
        }
        viewHolder.textFmAm.setText(mFmAm);
        viewHolder.textMhzKhz.setText(mMhzKhz);

        Log.d(TAG, "onBindViewHolder: current freq = " + DataCarbus.DATA[FinalRadio.U_FREQ]);
        // 该频率是否为正在播放的频率
        if(DataCarbus.DATA[FinalRadio.U_FREQ] == mFreqArray[i]){
            viewHolder.itemView.setSelected(true);
            viewHolder.textFreqValue.setTextColor(mContext.getColor(R.color.current_freq_color));
            viewHolder.textFmAm.setTextColor(mContext.getColor(R.color.current_freq_color));
            viewHolder.textMhzKhz.setTextColor(mContext.getColor(R.color.current_freq_color));
        }

        // 该频率是否包含在收藏列表中
        boolean isCollect = CollectFreq.getInstance(mContext).isCollect(mWorkMode, mFreqArray[i]);
        if(isCollect) {
            viewHolder.btnCollection.setSelected(true);
        } else {
            viewHolder.btnCollection.setSelected(false);
        }
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mOnListItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnListItemClickListener.onClickFreq(mFreqArray[position]);
                }
            }
        });
/*        viewHolder.btnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnListItemClickListener != null) {
                    boolean isCollect = !v.isSelected();
                    v.setSelected(isCollect);
                    int position = viewHolder.getAdapterPosition();
                    mOnListItemClickListener.onCollect(mFreqArray[position], isCollect);
                }
            }
        });*/  //逻辑流转文档，并没有要求列表界面点击实现收藏/取消收藏功能。
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mFreqArray == null ? 0 : mFreqArray.length;
    }

    OnListItemClickListener mOnListItemClickListener;

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mOnListItemClickListener = listener;
    }

    interface OnListItemClickListener {
        /**
         * @param freq 是频率值
         * @param isCollect true表示加入收藏列表，false表示从收藏列表删除
         * */
//        void onCollect(int freq, boolean isCollect);  //逻辑流转文档，并没有要求列表界面点击实现收藏/取消收藏功能。
        void onClickFreq(int freq);
    }

}
