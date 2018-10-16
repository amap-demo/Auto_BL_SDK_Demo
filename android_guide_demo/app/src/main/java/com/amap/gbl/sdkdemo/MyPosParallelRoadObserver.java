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
import com.autonavi.gbl.pos.observer.IPosParallelRoadObserver;
import com.autonavi.gbl.pos.model.LocParallelRoadInfo;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autonavi.gbl.guide.model.SoundInfo;

/**
 * @brief      主辅路信息观察者
 * @details    定位引擎使用观察者模式对外通知主辅路信息。 \n
 *            使用者通过addParallelRoadObserver接口把观察者添加到定位引擎中 \n
 *            使用者通过removeParallelRoadObserver接口把观察者从定位引擎中删除 \n
 *            - 主辅路定义：
 *            -# 候选道路是可绑定的（enumBINDFLAG）
 *            -# 当前车标在辅路，要获取主路，则候选道路的等级要高于当前车标所在道路等级
 *            -# 当前车标在主路，要获取辅路，则候选道路的等级要低于当前车标所在道路等级
 *            -# 候选道路与当前道路的角度差小于30度
 *            -# 候选道路与当前道路的横向距离(车标到候选道路的投影距离)小于60米
 *            -# 当前车标可以正投影到候选道路上
 *            -# 如果横向距离小于2米，而且候选道路相对于车标的方位不是左边和右边的话需要过滤掉
 *            -# 需要过滤相同道路GOBJECTID和有连接关系的候选道路
 *            -# 最后结果是一个主辅路列表，按跟当前车标的距离从近到远排列
 * @note       道路等级排序（从高到底） : 高速--城快--城主--国道--省道--城市次--城市普通--县道--乡道--乡内部--小路
 * @attention  由于removeParallelRoadObserver接口是异步的，所以外部删除后直接把观察者释放掉可能会导致定位引擎崩溃。应该先销毁定位引擎
 * @see
 * @see
 */
public class MyPosParallelRoadObserver implements IPosParallelRoadObserver
{
    public final static String TAG = MyPosParallelRoadObserver.class.getSimpleName().toString()+ " ygz$";

    private TextView mTextView;

    public MyPosParallelRoadObserver(TextView txtView) {
        Log.i(TAG, "MyPosParallelRoadObserver");
        mTextView = txtView;
    }
    /**
     * @brief      更新主辅路信息
     * @details    启动主辅路信息更新的条件与更新位置信息相同。主辅路信息只有在位置信息更新的时候才会更新 \n
     *             导航时，只输出导航路径的主辅路信息 \n
     * @param
     * @return     void
     * @attention  - 此接口是在引擎线程内触发的，严禁做大规模运算或调用可能导致线程挂起的接口，如IO操作、同步类接口(同步DBUS)等。
     * @attention  - onParallelRoadUpdate接口由定位线程调用，如果有访问临界区需要做保护。但不建议使用过多的锁。
     * @see
     * @see
     * @see
     */
    public void onParallelRoadUpdate(LocParallelRoadInfo locParallelRoadInfo) {
        Log.i(TAG, "onParallelRoadUpdate");
        if (locParallelRoadInfo != null && locParallelRoadInfo.parallelRoadList != null && locParallelRoadInfo.parallelRoadList.size() != 0) {
            //mTextView.setText( "onParallelRoadUpdate: 更新主辅路信息,信息不为空");
        } else {
           // mTextView.setText("onParallelRoadUpdate: 更新主辅路信息,信息为空");
        }

    }

}

