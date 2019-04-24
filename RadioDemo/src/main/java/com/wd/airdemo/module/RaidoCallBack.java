package com.wd.airdemo.module;

import android.util.Log;

import java.util.Arrays;

public class RaidoCallBack extends BaseCallBack {
    private static final String TAG = "RaidoCallBack";

	private static final int FM_FREQ_MIN = 8750;
	private static final int FM_FREQ_MAX = 10800;
	private static final int AM_FREQ_MIN = 522;
	private static final int AM_FREQ_MAX = 1620;

	@Override
	protected void update(int updateCode, int[] ints, String[] strs) {
		if (updateCode >= 0 && updateCode <= FinalRadio.U_CNT_MAX) {
			if (ints != null && ints.length > 0) {
				if (updateCode == FinalRadio.U_CHANNEL_FREQ) {
					int band = DataCarbus.DATA[FinalRadio.U_BAND];
					int[] its = Arrays.copyOfRange(ints, 1, ints.length);	//从第二个开始拷贝，直到结尾。
					if (band == 0) { // fm
						DataCarbus.fmInts = its;
                        Log.d(TAG,"U_CHANNEL_FREQ - FFFFM:" + its.length);
					} else if (band == 1) { // am
						DataCarbus.amInts = its;
                        Log.d(TAG,"U_CHANNEL_FREQ - AAAAM:" + its.length);
					}
                    Log.d(TAG,"bandAll:" + band + "/" + Arrays.toString(ints));
                    Log.d(TAG,"its:" + Arrays.toString(its));
					DataCarbus.NOTIFY[updateCode].onNotify(ints, null, null);
					return;
				} else if (updateCode == FinalRadio.U_FREQ) {
					if (DataCarbus.DATA[updateCode] == ints[0]) {
						return;
					}

					int band = DataCarbus.DATA[FinalRadio.U_BAND];
					int value = ints[0];
					if (band == 0) { // fm
						if(value < FM_FREQ_MIN || value > FM_FREQ_MAX) {
							value = FM_FREQ_MIN;
						}
					} else if (band == 1) { // am
						if(value < AM_FREQ_MIN || value > AM_FREQ_MAX) {
							value = AM_FREQ_MIN;
						}
					}

					DataCarbus.DATA[updateCode] = value;
					DataCarbus.NOTIFY[updateCode].onNotify();
				} else if (DataCarbus.DATA[updateCode] != ints[0]) {
					DataCarbus.DATA[updateCode] = ints[0];
					DataCarbus.NOTIFY[updateCode].onNotify();
				}
			}
		}
	}
}
