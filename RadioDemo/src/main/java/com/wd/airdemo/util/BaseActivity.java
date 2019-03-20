package com.wd.airdemo.util;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void in();

    protected abstract void out();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        in();
    }

    @Override
    protected void onPause() {
        super.onPause();
        out();
    }

}
