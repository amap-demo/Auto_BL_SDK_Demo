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
import com.autonavi.gbl.pos.model.LocInfo;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;
import com.autonavi.gbl.pos.observer.IPosLocInfoObserver;
import com.autonavi.gbl.pos.observer.IPosSwitchParallelRoadObserver;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autonavi.gbl.guide.model.SoundInfo;

/**
 * @brief      切换主辅路观察者
 * @details    主辅路切换完成通知 \n
 *             使用者通过addSwitchParallelRoadObserver接口把观察者添加到定位引擎中 \n
 *             使用者通过removeSwitchParallelRoadObserver接口把观察者从定位引擎中删除 \n
 * @attention  由于removeSwitchParallelRoadObserver接口是异步的，所以外部删除后直接把观察者释放掉可能会导致定位引擎崩溃。应该先销毁定位引擎
 * @see
 * @see
 */
public class MyPosSwitchParallelRoadObserver implements IPosSwitchParallelRoadObserver
{
    public final static String TAG = MyPosSwitchParallelRoadObserver.class.getSimpleName().toString()+ " ygz$";

    public MyPosSwitchParallelRoadObserver(TextView txtView) {
        Log.i(TAG, "MyPosSwitchParallelRoadObserver");
        mTextView = txtView;
    }
    /**
     * @brief      更新位置信息
     * @details    启动位置信息更新的条件：
     *            - 收到位置信号，GPS或者前端融合位置信息
     *            - 后端融合模式下，收到脉冲 \n
     * @details    引擎根据特定的模式对外更新位置信息，位置信息输出模式：
     *            - 模式一：2HZ固定频率输出，但是在位置信息没有变化时中断输出。
     * @param
     * @return void
     * @attention  - 此接口是在引擎线程内触发的，严禁做大规模运算或调用可能导致线程挂起的接口，如IO操作、同步类接口(同步DBUS)等。
     * @attention  - onLocInfoUpdate接口由定位线程调用，如果有访问临界区需要做保护。但不建议使用过多的锁。
     * @note       位置信息指的是LocInfo中这些字段：\n LocInfo::stPos::nLon \n LocInfo::stPos::nLat \n LocInfo::fCourse \n
     *            LocInfo::fSpeed \n LocInfo::uIsOnGuideRoad \n LocInfo::nSourType \n LocInfo::stDoorInPos.nLon \n
     *            LocInfo::stDoorInPos.nLat \n LocInfo::strFloor \n LocInfo::strPoiid \n
     * @see
     * @see
     */
    public void onSwitchParallelRoadFinished() {
        Log.i(TAG, "onSwitchParallelRoadFinished");
        //mTextView.setText("OnSwitchParallelRoadFinished");
    }

    private TextView mTextView;
}

