package com.wd.airdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.wd.airdemo.module.BaseCallBack;
import com.wd.airdemo.module.CarBusCallBack;
import com.wd.airdemo.module.FinalMain;
import com.wd.airdemo.module.FinalRadio;
import com.wd.airdemo.module.FinalRemoteModule;
import com.wd.airdemo.module.FinalSound;
import com.wd.airdemo.module.RemoteTools;
import com.wd.ms.ITaskBinder;
import com.wd.ms.tools.MSTools;

public class MyServer extends Service {
    BaseCallBack callBack;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("airserver create");

        ITaskBinder module1 = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
        try {
            module1.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.SOURCE_TUNER}, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ITaskBinder module2 = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_SOUND);
        try {
            module2.cmd(FinalSound.C_VOL_SET, new int[]{0, 15}, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        ITaskBinder module = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_RADIO);
        RemoteTools.setTaskBinder(module);
        callBack = new CarBusCallBack();
        regFlags();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("airserver destroy");
        unregFlags();
        RemoteTools.setTaskBinder(null);
    }

    private void regFlags() {
        for (int i = 0; i < FinalRadio.U_CNT_MAX; i++)
            RemoteTools.register(callBack, i, 1);
    }

    private void unregFlags() {
        for (int i = 0; i < FinalRadio.U_CNT_MAX; i++)
            RemoteTools.unregister(callBack, i);
    }
}
