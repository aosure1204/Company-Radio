package com.wd.airdemo;

import android.app.Application;
import android.content.Intent;

import com.wd.ms.tools.MSTools;


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MSTools.getInstance().init(this, new MSTools.IConnectListener() {
            @Override
            public void onSuccess() {
                openSelfServer(true);
            }

            @Override
            public void onFailed() {
                openSelfServer(false);
            }
        });
    }

    private void openSelfServer(boolean on) {
        Intent ii = new Intent(this, MyServer.class);
        if (on)
            startService(ii);
        else
            stopService(ii);
    }

}
