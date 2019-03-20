package com.wd.airdemo.module;

public class CarBusCallBack extends BaseCallBack {
    @Override
    protected void update(int updateCode, int[] ints, String[] strs) {
        if (updateCode > 0 && updateCode <= FinalRadio.U_CNT_MAX) {
            if (ints != null && ints.length > 0) {
//                System.out.println(getClass().getSimpleName() + ":" + updateCode);
                if (DataCarbus.DATA[updateCode] != ints[0]) {
                    DataCarbus.DATA[updateCode] = ints[0];
                    DataCarbus.NOTIFY[updateCode].onNotify();
                }
            }
        }
    }
}
