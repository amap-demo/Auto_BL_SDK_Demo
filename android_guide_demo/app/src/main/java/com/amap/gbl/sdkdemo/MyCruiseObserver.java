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
import com.autonavi.gbl.guide.observer.ICruiseObserver;
import com.autonavi.gbl.guide.model.CrossImageInfo;
import com.autonavi.gbl.guide.model.CruiseCongestionInfo;
import com.autonavi.gbl.guide.model.CruiseFacilityInfo;
import com.autonavi.gbl.guide.model.CruiseInfo;
import com.autonavi.gbl.guide.model.CruiseTimeAndDist;
import com.autonavi.gbl.guide.model.LaneInfo;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @brief 巡航电子眼观察者
 * @details guide引擎使用观察者模式对外通知巡航信息。\n
 *          使用者通过setCruiseObserver接口把观察者添加到guide引擎中 \n
 * @see
 */
public class MyCruiseObserver implements ICruiseObserver {

    public final static String TAG = MyCruiseObserver.class.getSimpleName().toString()+ " ygz$";
    private TextView mTextView;
    private Activity mActivity;
    private CruiseTimeAndDist mTimeDist;
    private LaneInfo mLaneInfo;

    public MyCruiseObserver(Activity activity, TextView txtView) {
        Log.i(TAG, "MyCruiseObserver");
        mTextView = txtView;
        mActivity = activity;
    }

    /**
     * @brief     传出巡航探测到的电子眼和交通设施信息
     * @details   巡航过程中传出电子眼和交通设施信息
     * @param[in] facilityInfoList 设施信息数组
     * @return    void 无返回值
     * @note      BL有内聚
     * @note thread mutil
     */
    @Override
    public void onUpdateCruiseFacility(List<CruiseFacilityInfo> facilityInfo) {
        Log.i(TAG, "onUpdateCruiseFacility");

        StringBuffer sb = new StringBuffer();
        if (facilityInfo != null && facilityInfo.size() > 0) {
            sb.append("设施类型:" + facilityInfo.get(0).type + "\r\n");
            sb.append("距离:" + facilityInfo.get(0).distance + "\r\n");
            sb.append("限速:" + facilityInfo.get(0).limitSpeed + "\r\n");
            sb.append("位置:lon=" + facilityInfo.get(0).pos.lon + ",lat=" + facilityInfo.get(0).pos.lat + "\r\n");
            //mTextView.setText(sb);
            /*
            BizPointExtraDataInfo bInfo = new BizPointExtraDataInfo();
            bInfo.bizDataInfo = new BizDataInfo();
            bInfo.bizDataInfo.cruiseFacilityInfo = new CruiseFacilityInfo[facilityInfo.size()];
            for (int i = 0; i < facilityInfo.size(); i++) {
                bInfo.bizDataInfo.cruiseFacilityInfo[i] = facilityInfo.get(i);
            }

            mIBizLayerService.addOverlayItems(AutoOverlayType.CruiseOverlayTypeFacility, bInfo); //巡航道路设施
            */
        } else {
            //mTextView.setText("道路设施为空");
        }
    }

    /**
     * @brief     传出自车前方电子眼信息
     * @details   巡航过程中传出自车前方电子眼信息
     * @param[in] cameraInfoList 电子眼信息
     * @return    void 无返回值
     * @note thread mutil
     */
    @Override
    public void onUpdateElecCameraInfo(List<CruiseFacilityInfo> cameraInfo) {
        Log.i(TAG, "onUpdateElecCameraInfo");
        StringBuffer sb = new StringBuffer();
        if (cameraInfo != null && cameraInfo.size() > 0) {
            sb.append("电子眼类型:" + cameraInfo.get(0).type + "\r\n");
            sb.append("距离:" + cameraInfo.get(0).distance + "\r\n");
            sb.append("限速:" + cameraInfo.get(0).limitSpeed + "\r\n");
            sb.append("位置:lon=" + cameraInfo.get(0).pos.lon + ",lat=" + cameraInfo.get(0).pos.lat + "\r\n");
            //mTextView.setText(sb);
            /*
            BizPointExtraDataInfo bInfo = new BizPointExtraDataInfo();
            bInfo.bizDataInfo = new BizDataInfo();
            bInfo.bizDataInfo.cruiseFacilityInfo = new CruiseFacilityInfo[facilityInfo.size()];
            for (int i = 0; i < facilityInfo.size(); i++) {
                bInfo.bizDataInfo.cruiseFacilityInfo[i] = facilityInfo.get(i);
            }

            mIBizLayerService.addOverlayItems(AutoOverlayType.CruiseOverlayTypeFacility, bInfo); //巡航道路设施
            */
        } else {
            //mTextView.setText("电子眼设施为空");
        }
    }

    /**
     * @brief     传出巡航状态下的信息
     * @details   巡航过程中传出巡航状态下的信息
     * @param[in] info 巡航信息
     * @return    void 无返回值
     * @note thread mutil
     */
    @Override
    public void onUpdateCruiseInfo(CruiseInfo info) {
        Log.i(TAG, "onUpdateCruiseInfo");
        StringBuffer sb = new StringBuffer();
        sb.append("road name:" + info.roadName);
        sb.append("road class:" + info.roadClass);
       // mTextView.setText(sb);
    }

    /**
     * @brief     传出巡航状态连续行驶的时间和距离
     * @details   巡航过程中传出巡航状态连续行驶的时间和距离
     * @param[in] info 连续行驶的时间和距离信息
     * @return    void 无返回值
     * @note thread mutil
     */
    @Override
    public void onUpdateCruiseTimeAndDist(CruiseTimeAndDist info) {
        //Log.i(TAG, "onUpdateCruiseTimeAndDist dist=" + info.driveDist + ",time=" + info.driveTime);
        mTimeDist = info;
        if (mTimeDist != null && mTextView != null && mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuffer sb = new StringBuffer();
                    sb.append("dist=" + mTimeDist.driveDist + ",time=" + mTimeDist.driveTime);
                    mTextView.setText(sb);
                }
            });
        }
    }

    /**
     * @brief     传出巡航状态下的拥堵区域信息
     * @details   巡航过程中传出巡航状态下的拥堵区域信息
     * @param[in] info 拥堵区域信息
     * @return    void 无返回值
     * @note thread mutil
     * @note BL有内聚
     */
    @Override
    public void onUpdateCruiseCongestionInfo(CruiseCongestionInfo info) {
        Log.i(TAG, "onUpdateCruiseCongestionInfo");
        StringBuffer sb = new StringBuffer();
        if (info != null) {
            sb.append("拥堵路:" + info.roadName + "\r\n");
            sb.append("拥堵时间:" + info.etaTime + "\r\n");
            sb.append("拥堵长度:" + info.length + "\r\n");
            sb.append("图片地址:" + info.socolPicUrl + "\r\n");
            if (info.pLinkData != null) {
                sb.append("拥堵link长度:" + info.pLinkData.length + "\r\n");
            }
            sb.append("事件id:" + info.eventID + "\r\n");
            sb.append("事件图层:" + info.layer + "\r\n");
           // mTextView.setText(sb);
            /*
            mRoadInfo = new BizPointExtraDataInfo();
            mRoadInfo.bizDataInfo = new BizDataInfo();
            mRoadInfo.bizDataInfo.congesttion = new CruiseCongestionInfo[1];
            mRoadInfo.bizDataInfo.congesttion[0] = info;
            mIBizLayerService.addOverlayItems(AutoOverlayType.CruiseOverlayTypeCongestion, mRoadInfo);
            */
        } else {
           // mTextView.setText("拥堵信息为空");
        }
    }

    /**
     * @brief     显示巡航车道信息
     * @details   巡航过程中传出巡航车道信息
     * @param[in] info 行车引导线信息
     * @return    void 无返回值
     * @note thread mutil
     * @note BL有内聚
     */
    @Override
    public void onShowCruiseLaneInfo(LaneInfo info) {
        //Log.i(TAG, "onShowCruiseLaneInfo");
        //mTextView.setText("onShowCruiseLaneInfo: ");
        mLaneInfo = info;
        if (mLaneInfo != null && mTextView != null && mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuffer sb = new StringBuffer();
                    sb.append("巡航车道信息: lon=" + mLaneInfo.point.lon + ",lat=" + mLaneInfo.point.lat + "\r\n");
                    int len = mLaneInfo.backLane.length;
                    if(len>0)
                    {
                        sb.append("back lane size="+len +":");
                        for(int i=0;i<len;++i){
                            sb.append(" "+ mLaneInfo.backLane[i]);
                        }
                        sb.append("\r\n");
                    }
                    len = mLaneInfo.frontLane.length;
                    if(len>0)
                    {
                        sb.append("front lane size="+len +":");
                        for(int i=0;i<len;++i){
                            sb.append(" "+ mLaneInfo.frontLane[i]);
                        }
                        sb.append("\r\n");
                    }
                    mTextView.setText(sb);
                }
            });
        }
    }

    /**
     * @brief     隐藏巡航车道信息
     * @details   巡航过程中通知隐藏车道信息
     * @return    void 无返回值
     * @note thread mutil
     * @note BL有内聚
     */
    @Override
    public void onHideCruiseLaneInfo() {
        Log.i(TAG, "onHideCruiseLaneInfo");
        //mTextView.setText("onHideCruiseLaneInfo: ");
        //mIBizLayerService.clearOverlay(AutoOverlayType.CruiseOverlayTypeLaneLinePoint);
    }

}

