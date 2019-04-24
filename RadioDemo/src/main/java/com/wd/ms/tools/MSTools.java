package com.wd.ms.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.wd.airdemo.module.FinalRemoteModule;
import com.wd.ms.IRemoteModule;
import com.wd.ms.ITaskBinder;

public class MSTools {

    public interface IConnectListener {
        void onSuccess();

        void onFailed();
    }

    private IConnectListener mListener;
    private static MSTools INSTANCE = new MSTools();
    private IRemoteModule remote;
    private Context mCtx;
    private m obj;

    public static MSTools getInstance() {
        return INSTANCE;
    }

    public void init(Context c, IConnectListener lis) {
        mCtx = c;
        this.mListener = lis;
        if (obj == null) {
            obj = new m(c);
            obj.setListener(lis);
        }
        obj.run();
    }

    public void disconnect() {
        if (obj != null) {
            obj.disconnect();
        }
    }

    protected void setRemote(IRemoteModule remote) {
        System.out.println("mstool " + remote);
        this.remote = remote;
    }

    public ITaskBinder getModule(int code) {
        try {
            ITaskBinder moduleByCode = remote.getModule(code);
            System.out.println("mstools: getmodule:" + moduleByCode);
            return moduleByCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class m implements Runnable, ServiceConnection {

        private Context mCtx;
        private boolean isConnect = false;
        private boolean isForceDisconnect = false;
        private Handler mH = new Handler();
        private MSTools.IConnectListener listener;

        public m(Context context) {
            this.mCtx = context;
        }

        @Override
        public void run() {
            if (!isConnect && !isForceDisconnect)
                connect();
        }

        public void setListener(MSTools.IConnectListener lis) {
            this.listener = lis;
        }

        private void connect() {
            if (mCtx != null) {
                isForceDisconnect = false;
                Intent ii = new Intent("com.wd.ms");
                ii.setPackage("com.wd.ms");
                mCtx.bindService(ii, this, Context.BIND_AUTO_CREATE);

                Log.i("mstool", "connect ms... ");
            }

            mH.postDelayed(this, 3000); //每隔3000毫秒自动运行一次。
        }

        public void disconnect() {
            isForceDisconnect = true;
            mCtx.unbindService(this);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("mstool", "onServiceConnected ok !");
            isConnect = true;
            IRemoteModule iRemoteModule = IRemoteModule.Stub.asInterface(service);
            MSTools.getInstance().setRemote(iRemoteModule);
            if (listener != null) listener.onSuccess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnect = false;
            MSTools.getInstance().setRemote(null);
            if (isForceDisconnect) return;
            mH.postDelayed(this, 1000); //连接失败，则每隔1秒自动运行一次。
            if (listener != null) listener.onFailed();
        }

    }
}
