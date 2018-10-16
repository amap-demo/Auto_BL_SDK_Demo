package com.amap.gbl.sdkdemo;

import android.app.Application;
import android.content.Context;

import com.amap.gbl.sdkdemo.platform.CC;

/**
 * Created by youzhang.syz on 2018/1/26.
 */

public class MyApplication extends Application {
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CC.setApplication(this);
    }
}
