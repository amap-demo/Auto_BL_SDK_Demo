package com.amap.gbl.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.guide.observer.ISoundPlayObserver;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.map.utils.GLMapUtil;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceDataPath;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autonavi.gbl.guide.model.SoundInfo;

public class MySoundPlayObserver implements ISoundPlayObserver
{
    private Activity mActivity;
    private TextView mTextView;
    private boolean mPlaying;
    private SoundInfo mSoundInfo;
    public final static String TAG = MySoundPlayObserver.class.getSimpleName().toString()+ " ygz$";

    public MySoundPlayObserver(Activity activity,TextView txtView) {
        mActivity = activity;
        Log.i(TAG, "MySoundPlayObserver");
        mTextView = txtView;
        mPlaying = false;
    }

    /**
     * @brief     TTS语音播报
     * @details   引导过程中播报相关的语音
     * @param[in] pInfo 语音播报信息
     * @return    void 无返回值
     * @note thread mutil
     */
    @Override
    public void onPlayTTS(SoundInfo pInfo) {
        Log.i(TAG, "onPlayTTS");
        mSoundInfo = pInfo;
        if (mSoundInfo != null && mTextView != null && mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(mSoundInfo.text);
                }
            });
        }
    }

    /**
     * @brief     播报叮语音
     * @details   引导过程中播报叮语音
     * @param[in] type 播报叮语音的类型
     * @return    void 无返回值
     * @note thread mutil
     */
    @Override
    public void onPlayRing(int type) {
        Log.i(TAG, "onPlayRing");
    }

    /**
     * @brief     判断TTS是否正在播报
     * @details   引导过程中可以判断TTS是否正在播报
     * @return    true:忙，正在播报，false：空闲，没有播报
     * @note thread mutil
     */
    @Override
    public boolean isPlaying() {
        Log.i(TAG, "isPlaying");
        return mPlaying;
    }

}

