package com.wd.airdemo;

import android.app.Application;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.wd.ms.ITaskBinder;
import com.wd.ms.tools.MSTools;
import com.wd.airdemo.module.FinalMain;
import com.wd.airdemo.module.FinalRemoteModule;
import com.wd.airdemo.module.RadioModule;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;


public class MyApp extends Application {
    private static MyApp OBJ = new MyApp();
    private static boolean isBindMS = false;
    private ITaskBinder mMainModule;

    public static MyApp getOBJ() {
        return OBJ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MSTools.getInstance().init(this, new MSTools.IConnectListener() {
            @Override
            public void onSuccess() {
                isBindMS = true;
                RadioModule.Init();
            }

            @Override
            public void onFailed() {
                isBindMS = false;
            }
        });
    }

    public void getAudioFoucs(){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        AudioManager.OnAudioFocusChangeListener l = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

            }
        };

        am.requestAudioFocus(l , AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);
    }

    public void requestRadioSource() {
        if(isBindMS){
            mMainModule = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
            if (mMainModule != null) {
                try {
                    mMainModule.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.SOURCE_TUNER}, null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("change to SOURCE_TUNER source...");
            }

            //设置音量功能去掉
/*            ITaskBinder module2 = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_SOUND);
            try {
                module2.cmd(FinalSound.C_VOL_SET, new int[]{0, 20}, null, null);
                System.out.println("change to volume 15...");
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/

        }else{
            new HandlerNotRemove().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestRadioSource();
                }
            }, 200);
        }
    }

    public void requestFMApp() {
        if(isBindMS){
            mMainModule = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
            if (mMainModule != null) {
                try {
                    mMainModule.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.APP_FM}, null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("change to requestFMApp ...");
            }
        }else{
            new HandlerNotRemove().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestFMApp();
                }
            }, 200);
        }
    }

    public void requestAMApp() {
        if(isBindMS){
            mMainModule = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
            if (mMainModule != null) {
                try {
                    mMainModule.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.APP_AM}, null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("change to requestAMApp ...");
            }
        }else{
            new HandlerNotRemove().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestAMApp();
                }
            }, 200);
        }
    }

    public void requestNullSource(){
        if(isBindMS){
            mMainModule = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
            if (mMainModule != null) {
                try {
                    mMainModule.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.SOURCE_CORE}, null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("change to SOURCE_TUNER source...");
            }
        }
    }

    public void requestNullApp(){
        if(isBindMS){
            mMainModule = MSTools.getInstance().getModule(FinalRemoteModule.MODULE_MAIN);
            if (mMainModule != null) {
                try {
                    mMainModule.cmd(FinalMain.C_APP_ID, new int[]{FinalMain.APP_NULL}, null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("change to APP_NULL ...");
            }
        }
    }

    class HandlerNotRemove extends Handler {
        private Handler mHandler;
        private HandlerThread mHandlerThread;

        HandlerNotRemove(){
            mHandlerThread = new HandlerThread("Not Removable");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }
}
