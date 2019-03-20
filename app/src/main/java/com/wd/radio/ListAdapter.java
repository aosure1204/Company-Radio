package com.wd.radio;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private int[] mFreqArray;
    private int mWorkMode = DataUtil.WORK_MODE_FM;
    private String mFmAm;
    private String mMhzKhz;

    public ListAdapter(int[] freqArray, int workMode) {
        mFreqArray = freqArray;
        mWorkMode = workMode;
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
        ImageButton btnCollection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFreqValue = (TextView) itemView.findViewById(R.id.item_freq_value);;
            textFmAm = (TextView) itemView.findViewById(R.id.item_fm_am);;
            textMhzKhz = (TextView) itemView.findViewById(R.id.item_mhz_khz);;
            btnCollection = (ImageButton) itemView.findViewById(R.id.item_btn_collection);
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
        viewHolder.btnCollection.setImageResource(R.drawable.ic_collect_unselected);
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mFreqArray.length;
    }

}
