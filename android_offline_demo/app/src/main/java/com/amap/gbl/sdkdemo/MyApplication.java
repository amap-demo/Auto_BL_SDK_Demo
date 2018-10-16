package com.amap.gbl.sdkdemo;

import android.app.Application;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.amap.gbl.sdkdemo.platform.CC;

/**
 * Created by youzhang.syz on 2018/1/26.
 */

public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        CC.setApplication(this);
    }


}
