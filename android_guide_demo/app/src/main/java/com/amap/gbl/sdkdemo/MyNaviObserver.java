package com.amap.gbl.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.biz.TbtUnionUtil;
import com.autonavi.gbl.biz.bizenum.AutoOverlayType;
import com.autonavi.gbl.biz.bizenum.SceneControlType;
import com.autonavi.gbl.biz.model.CarLocation;
import com.autonavi.gbl.common.model.Coord2DDouble;
import com.autonavi.gbl.common.path.drive.model.POIForRequest;
import com.autonavi.gbl.common.path.model.option.RerouteOption;
import com.autonavi.gbl.common.path.model.option.RouteOptionEnum;
import com.autonavi.gbl.guide.GuideService;
import com.autonavi.gbl.guide.model.AsyncInfo;
import com.autonavi.gbl.guide.model.DriveEventTip;
import com.autonavi.gbl.guide.model.ExitDirectionInfo;
import com.autonavi.gbl.guide.model.GuideEnum;
import com.autonavi.gbl.guide.model.LightBarInfo;
import com.autonavi.gbl.guide.model.LockScreenTip;
import com.autonavi.gbl.guide.model.ManeuverConfig;
import com.autonavi.gbl.guide.model.ManeuverInfo;
import com.autonavi.gbl.guide.model.MixForkInfo;
import com.autonavi.gbl.guide.model.NaviCamera;
import com.autonavi.gbl.guide.model.NaviCongestionInfo;
import com.autonavi.gbl.guide.model.NaviFacility;
import com.autonavi.gbl.guide.model.NaviInfo;
import com.autonavi.gbl.guide.model.NaviIntervalCamera;
import com.autonavi.gbl.guide.model.NaviIntervalCameraDynamicInfo;
import com.autonavi.gbl.guide.model.NaviStatisticsInfo;
import com.autonavi.gbl.guide.model.ObtainInfo;
import com.autonavi.gbl.guide.model.PathTrafficEventInfo;
import com.autonavi.gbl.guide.model.RouteTrafficEventInfo;
import com.autonavi.gbl.guide.model.SuggestChangePathReason;
import com.autonavi.gbl.guide.model.TMCIncidentReport;
import com.autonavi.gbl.guide.observer.ISoundPlayObserver;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.map.MapPoint;
import com.autonavi.gbl.map.PreviewParam;
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

import com.autonavi.gbl.guide.observer.INaviObserver;

import static com.autonavi.gbl.common.path.drive.model.POIForRequest.PointTypeEnd;
import static com.autonavi.gbl.common.path.drive.model.POIForRequest.PointTypeStart;

/**
 * @brief 巡航电子眼观察者
 * @details guide引擎使用观察者模式对外通知巡航信息。\n
 * 使用者通过setCruiseObserver接口把观察者添加到guide引擎中 \n
 * @see
 */
public class MyNaviObserver implements INaviObserver {

    public final static String TAG = MyNaviObserver.class.getSimpleName().toString() + " ygz$";
    private TextView mTextView;
    private Handler mHandler;

    public MyNaviObserver(TextView txtView,Handler handler) {
        mTextView = txtView;
        mHandler = handler;
    }

    /**
     * @return void 无返回值
     * @brief 传出当前导航信息
     * @details 导航过程中传出当前导航信息
     * @param[in] naviInfoList 当前导航信息数组
     * @note thread mutil
     * @note BL有内聚, HMI也可以使用这些信息做二次开发
     */
    @Override
    public void onUpdateNaviInfo(List<NaviInfo> naviInfoList) {
        Log.i(TAG, "onUpdateNaviInfo");
        if (naviInfoList != null && naviInfoList.size() > 0) {
            Log.i(TAG, "onUpdateNaviInfo: naviInfoList = " + naviInfoList.size());

            //String naviJson = new Gson().toJson(naviInfoList);
            //CommonUtil.writeTxtToSDCard("etatest", naviJson);
            //Log.d(TAG, "onUpdateNaviInfo: " + naviJson);
            //Log.i(TAG, "当前道路:" + naviInfoList.get(0).curRouteName + "\r\n" + "下条道路:" + naviInfoList.get(0).  +
            // "\r\n" + "主动作:" + naviInfoList.get(0).crossManeuverID);
            //tv_msg2.setText("当前道路:" + naviInfoList.get(0).curRouteName + "\r\n" + "主动作:" + naviInfoList.get(0)
            // .crossManeuverID);
            //Log.i(TAG, "onUpdateNaviInfo: 主动作 = 前方 " + naviInfoList.get(0).routeRemainDist + "米后执行" + naviInfoList
            // .get(0).crossManeuverID + "操作");
            StringBuilder sb = new StringBuilder();
            sb.append("透出引导信息:");
            if (naviInfoList.get(0).nextCrossInfo != null && naviInfoList.get(0).nextCrossInfo.length > 0) {
                Log.i(TAG, "onUpdateNaviInfo: nextCrossInfo.length = " + naviInfoList.get(0).nextCrossInfo.length);
                for (int i = 0; i < naviInfoList.get(0).nextCrossInfo.length; i++) {
                    //0代表无导航动作
                    String action = "第" + i + "次近阶动作透出: 导航动作 = " + naviInfoList.get(
                    0).nextCrossInfo[i].mainAction;
                    sb.append(action);
                    sb.append("\n");
                    Log.d(TAG, "透出引导信息 onUpdateNaviInfo:" +action);
                    //36代表到达目的地的
                    String strManeuverID = "第" + i + "近阶动作透出: 路口转向ID = " + naviInfoList.get(
                        0).nextCrossInfo[i].crossManeuverID;
                    Log.d(TAG, "透出引导信息 onUpdateNaviInfo: "+strManeuverID);
                    String strNextRoadName = "第" + i + "次近阶动作透出: 下条道路名称 = " + naviInfoList.get(
                    0).nextCrossInfo[i].nextRoadName;
                    Log.d(TAG, "透出引导信息 onUpdateNaviInfo: "+ strNextRoadName);
                    sb.append(strManeuverID);
                    sb.append("\n");
                    sb.append(strNextRoadName);
                    sb.append("\n");
                }
                Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_SHOW_SHOW_NAVIINFO);
                msg.obj = sb.toString();
                msg.sendToTarget();
                //tvNaviInfo.setText(sb.toString());
            } else {
                Log.i(TAG, "onUpdateNaviInfo: 无近阶动作透出");
            }
            //if (curSegIdx == naviInfoList.get(0).curSegIdx) {
            //    //什么都不做
            //} else {
            //    curSegIdx = naviInfoList.get(0).curSegIdx;
            //    int[] curSegIdxs = new int[]{naviInfoList.get(0).curSegIdx};
            //    mIBizLayerService.setRouteArrowShowSegment(AutoOverlayType.RouteOverlayGuide, curSegIdxs);
            //    mIBizLayerService.updateRouteArrow(AutoOverlayType.RouteOverlayGuide);//更新箭头
            //}

        } else {
        }
    }

    /**
     * @return void 无返回值
     * @brief 传出出口编号和出口方向信息
     * @details 导航过程中传出出口编号和出口方向信息
     * @param[in] boardInfo 当前导航信息数组
     * @note thread mutil
     */
    @Override
    public void onUpdateExitDirectionInfo(ExitDirectionInfo boardInfo) {
        Log.i(TAG, "onUpdateExitDirectionInfo: 更新出口信息");
        //Log.d(TAG, "透出引导信息 onUpdateExitDirectionInfo: 更新出口信息");
        StringBuffer sb = new StringBuffer();
        sb.append("透出引导信息:");
        if (boardInfo != null) {
            //sb.append("距离:" + boardInfo.disToCurrentPos + "\r\n");
            //sb.append("时间:" + boardInfo.remainTime + "\r\n");
            if (boardInfo.directionInfo != null && boardInfo.directionInfo.size() > 0) {
                for (int i = 0; i < boardInfo.directionInfo.size(); i++) {
                    sb.append("出口:" + boardInfo.directionInfo.get(i) + "\r\n");
                }
            }
            if (boardInfo.exitNameInfo != null && boardInfo.exitNameInfo.size() > 0) {
                for (int i = 0; i < boardInfo.exitNameInfo.size(); i++) {
                    sb.append("出口名称:" + boardInfo.exitNameInfo.get(i) + "\r\n");
                }
            }

            Log.d(TAG, "透出引导信息  onUpdateExitDirectionInfo: " + sb);
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_SHOW_SHOW_NAVIINFO);
            msg.obj = sb.toString();
            msg.sendToTarget();
            //mTextView.setText(sb);
        }
    }

    /**
     * @return void 无返回值
     * @brief 显示路口大图
     * @details 导航过程中传出路口大图数据
     * @param[in] info 路口大图信息
     * @note thread mutil
     */
    @Override
    public void onShowCrossImage(CrossImageInfo info) {
        Log.i(TAG, "onShowCrossImage: 显示路口大图");

        if (null != mHandler){
            Message msg = new Message();
            msg.what = MainActivity.MESSAGE_SHOW_CROSS_IMAGE;
            msg.obj = info;
            mHandler.sendMessage(msg);
        }else {
            Log.i("yyc","onShowCrossImage mNaviCallBack ==null,please check");
        }

    }

    /**
     * @return void 无返回值
     * @brief 输出三维路口放大图TMC数据
     * @details 导航过程中输出三维路口放大图TMC数据
     * @param[in] dataBuf TMC数据流
     * @note thread mutil
     */
    @Override
    public void onShowNaviCrossTMC(byte[] dataBuf) {
        Log.i(TAG, "onShowNaviCrossTMC: ");
    }

    /**
     * @return void 无返回值
     * @brief 隐藏路口大图
     * @details 导航过程中通知隐藏路口大图
     * @param[in] type 路口大图类型
     * @note thread mutil
     */
    @Override
    public void onHideCrossImage(int type) {
        Log.i(TAG, "onHideCrossImage: type = " + type);
        Message msg = new Message();
        msg.what = MainActivity.MESSAGE_HIDE_CROSS_IMAGE;
        msg.arg1 = type;
        mHandler.sendMessage(msg);
    }

    /**
     * @return void 无返回值
     * @brief 三维路口大图通过最后一个导航段
     * @details 导航过程中通知三维路口大图通过最后一个导航段
     * @note thread mutil
     */
    @Override
    public void onPassLast3DSegment() {
        Log.i(TAG, "onPassLast3DSegment: ");
    }

    /**
     * @return void 无返回值
     * @brief 显示车道信息
     * @details 导航过程中传出车道信息
     * @param[in] info 行车引导线信息
     * @note thread mutil
     */
    @Override
    public void onShowNaviLaneInfo(LaneInfo info) {
        if (null != info) {
            StringBuilder sb = new StringBuilder();
            sb.append("透出引导信息:");
            int[] frontLane = info.frontLane;
            sb.append("frontLane:");
            for (int i = 0, len = frontLane.length; i < len; i++) {
                sb.append(" ");
                sb.append(frontLane[i]);
            }
            int[] backLane = info.backLane;
            sb.append(" backLane:");
            for (int i = 0, len = backLane.length; i < len; i++) {
                sb.append(" ");
                sb.append(backLane[i]);
            }
            Log.d(TAG, "透出引导信息  onShowNaviLaneInfo: " + sb.toString());
            Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_SHOW_SHOW_NAVIINFO);
            msg.obj = sb.toString();
            msg.sendToTarget();
        }

    }

    /**
     * @return void 无返回值
     * @brief 隐藏车道信息
     * @details 导航过程中通知隐藏车道信息
     * @note thread mutil
     */
    @Override
    public void onHideNaviLaneInfo() {
        Log.i(TAG, "onHideNaviLaneInfo: ");
    }

    /**
     * @return void 无返回值
     * @brief 显示转向图标
     * @details 导航过程中传出转向图标
     * @param[in] info 转向图标信息
     * @note thread mutil
     */
    @Override
    public void onShowNaviManeuver(ManeuverInfo info) {
        Log.i(TAG, "onShowNaviManeuver: ");
        final AsyncInfo asyncMessage = new AsyncInfo();
        asyncMessage.what = GuideEnum.AITRenderManeuverIcon;
        asyncMessage.arg1 = 1;
        ManeuverConfig config = new ManeuverConfig();
        config.width = 400;
        config.height = 400;
        config.maneuverId = info.maneuverID;
        config.arrowColor = 0xffaabbcc;
        config.backColor = 0xff00bbcc;
        config.roadColor = 0xffaabb00;
        asyncMessage.ptr = config;
        //NaviManager.getInstance().obtainAsyncInfo(asyncMessage);
    }

    /**
     * @return void 无返回值
     * @brief 显示电子眼
     * @details 显示电子眼，当naviCamera为NULL或者count为0时，清除界面展示的电子眼
     * @param[in] naviCameraList 电子眼信息, 为0时，清除界面展示的电子眼
     * @note thread mutil
     * @note BL内聚
     */
    @Override
    public void onShowNaviCamera(List<NaviCamera> naviCameraList) {
        Log.i(TAG, "onShowNaviCamera: naviCameraList = " + naviCameraList);
    }

    private int firstCamera = 0;

    /**
     * @return void 无返回值
     * @brief 显示区间测速电子眼
     * @details 显示区间测速电子眼，当naviIntervalCamera为NULL或者count为0时，清除界面展示的区间测速电子眼
     * @param[in] naviIntervalCameraList 区间测速电子眼信息列表
     * @note thread mutil
     * @note BL内聚
     */
    @Override
    public void onShowNaviIntervalCamera(List<NaviIntervalCamera> naviIntervalCameraList) {
        Log.i(TAG, "onShowNaviIntervalCamera: naviCameraList = " + naviIntervalCameraList);
        //只需要传入纹理就可以，电子眼的显示、清除由BL内聚
        if (naviIntervalCameraList != null) {
            //等于2的时候会一直回调
            if (naviIntervalCameraList.size() == 2) {  //准备进入区间测速，模拟HMI层进行全览操作，同时应该结束动态比例尺
                Log.i(TAG, "onShowNaviIntervalCamera: naviIntervalCameraList.size() = 2");
                if (++firstCamera == 1) {  //只有第一次进入的时候才会全览路线
                    //设置动态比例尺
                    /*
                    mIBizSceneService.setSceneControl(SceneControlType.DynamicLevel, false);
                    PreviewParam intervalPre = new PreviewParam();
                    intervalPre.bUseRect = false;
                    intervalPre.points = new MapPoint[3];
                    intervalPre.points[0] = new MapPoint();
                    intervalPre.points[0].x = mIBizLayerService.getCarLocation().longitude;
                    intervalPre.points[0].y = mIBizLayerService.getCarLocation().latitude;

                    intervalPre.points[1] = new MapPoint();
                    intervalPre.points[1].x = naviIntervalCameraList.get(0).coord2D.lon;
                    intervalPre.points[1].y = naviIntervalCameraList.get(0).coord2D.lat;

                    intervalPre.points[2] = new MapPoint();
                    intervalPre.points[2].x = naviIntervalCameraList.get(1).coord2D.lon;
                    intervalPre.points[2].y = naviIntervalCameraList.get(1).coord2D.lat;

                    mapView.showPreview(mMainEngineID, intervalPre, true, 500, -1);
                    */
                }
            } else if (naviIntervalCameraList.size() == 1) { //经过起点，不做任何操作
                //这时候起点测速眼应该消失
                Log.i(TAG, "onShowNaviIntervalCamera: naviIntervalCameraList.size() = 1");
            } else if (naviIntervalCameraList.size() == 0) {  //经过终点执行操作，结束全览，开启动态比例尺
                Log.i(TAG, "onShowNaviIntervalCamera: naviIntervalCameraList.size() = 0");
                //结束全览
                //mapView.exitPreview(mMainEngineID, true, true);
                //设置动态比例尺
                //mIBizSceneService.setSceneControl(SceneControlType.DynamicLevel, true);
                firstCamera = 0; //重新设置为0
            } else {
                firstCamera = 0; //重新设置为0
                Log.i(TAG, "其他情况: naviIntervalCameraList.size() = " + naviIntervalCameraList.size());
            }
        } else {
            Log.i(TAG, "onShowNaviIntervalCamera: naviIntervalCameraList = null");
            firstCamera = 0; //重新设置为0
            //结束全览
            //mapView.exitPreview(mMainEngineID, true, true);
            //设置动态比例尺
            //mIBizSceneService.setSceneControl(SceneControlType.DynamicLevel, true);
            firstCamera = 0; //重新设置为0
        }
    }

    /**
     * @brief 更新区间测试电子眼动态实时信息
     * @details 更新区间测试电子眼动态实时信息
     * @param[in] cameraDynamicList 区间电子眼动态信息数组
     */
    @Override
    public void onUpdateIntervalCameraDynamicInfo(List<NaviIntervalCameraDynamicInfo> cameraDynamicList) {
        Log.i(TAG, "onUpdateIntervalCameraDynamicInfo: naviCameraList = " + cameraDynamicList);
        if (cameraDynamicList != null) {
            for (int i = 0; i < cameraDynamicList.size(); i++) {

                Log.i(TAG, "onUpdateIntervalCameraDynamicInfo:第" + i + "个摄像头距离:" + cameraDynamicList.get(i).distance);
                Log.i(TAG,
                    "onUpdateIntervalCameraDynamicInfo:第" + i + "个摄像头均速:" + cameraDynamicList.get(i).averageSpeed);
                Log.i(TAG,
                    "onUpdateIntervalCameraDynamicInfo:第" + i + "个摄像头剩余距离:" + cameraDynamicList.get(i).remainDistance);
                Log.i(TAG, "onUpdateIntervalCameraDynamicInfo:第" + i + "个摄像头推荐速度:" + cameraDynamicList
                    .get(i).reasonableSpeedInRemainDist);
                if (cameraDynamicList.get(i).speed != null && cameraDynamicList.get(i).speed.length > 0) {
                    Log.i(TAG,
                        "onUpdateIntervalCameraDynamicInfo: 第" + i + "个摄像头限速容器:" + cameraDynamicList.get(i).speed[0]);
                } else {
                    Log.i(TAG, "onUpdateIntervalCameraDynamicInfo:第" + i + "个摄像头限速容器:null");
                }
            }
        }
    }

    /**
     * @return void 无返回值
     * @brief 更新服务区信息
     * @details 更新服务区信息，当serviceArea为NULL或者count为0时，清除服务区信息
     * @param[in] serviceAreaList 服务区信息数组
     * @note thread mutil
     */
    @Override
    public void onUpdateSAPA(List<NaviFacility> serviceAreaList) {
        Log.i(TAG, "onUpdateSAPA: ");
    }

    /**
     * @return void 无返回值
     * @brief 导航结束
     * @details 到达目的地的时候通知导航结束
     * @param[in] naviType 0:GPS导航, 1:模拟导航
     * @note thread mutil
     */
    @Override
    public void onNaviStop(int naviType) {
        Log.i(TAG, "onNaviStop: ");
        GuideService guideService = (GuideService)ServiceMgr.getServiceMgrInstance().getBLService(
            ServiceManagerEnum.GuideSingleServiceID);
        guideService.stopNavi();
    }

    /**
     * @return void 无返回值
     * @brief 更新经过途经点索引
     * @details 导航过程中经过途经点的时候通知途经点索引
     * @param[in] viaIndex 途经点索引
     * @note thread mutil
     */
    @Override
    public void onUpdateViaPass(long viaIndex) {
        Log.i(TAG, "onUpdateViaPass: ");
    }

    /**
     * @return void 无返回值
     * @brief 锁屏导航提示，锁屏状态导航远距离提示点亮屏幕
     * @details 导航过程中通知锁屏导航提示
     * @param[in] tip 锁屏提示信息
     * @note thread mutil
     */
    @Override
    public void onShowLockScreenTip(LockScreenTip tip) {
        Log.i(TAG, "onShowLockScreenTip: ");
    }

    /**
     * @return void 无返回值
     * @brief 驾驶行为报告
     * @details 导航结束时，传出驾驶行为报告
     * @param[in] driveReport 驾驶行为报告, json格式
     * @param[in] naviStatisticsInfo  统计信息
     * @note thread mutil
     */
    @Override
    public void onDriveReport(String driveReport, NaviStatisticsInfo info) {
        Log.i(TAG, "onDriveReport: ");
    }

    /**
     * @return void 无返回值
     * @brief 驾驶行为事件
     * @details 导航结束时，传出驾驶行为事件
     * @param[in] list 驾驶行为事件
     * @note thread mutil
     */
    @Override
    public void onShowDriveEventTip(List<DriveEventTip> list) {
        Log.i(TAG, "onShowDriveEventTip: ");
    }

    private boolean yaw = false;

    /**
     * @return void 无返回值
     * @brief guide引擎通知重算
     * @details 因偏航，道路限行，tmc路况拥堵等原因，guide引擎会通知外界进行路线重算
     * @param[in] rerouteOption 重算信息
     * @note thread mutil
     */
    @Override
    public void onReroute(RerouteOption rerouteOption) {
        Log.i(TAG, "onReroute: ");
        /*
        Toast.makeText(GuideActivity.this, "偏航重算中...", Toast.LENGTH_SHORT).show();
        yaw = true;  //偏航重算的标志位
        int flag = rerouteOption.getConstrainCode();
        CarLocation carLoc = mIBizLayerService.getCarLocation();
        startInfo.longitude = (float) (carLoc.longitude);
        startInfo.latitude = (float) (carLoc.latitude);
        Log.i(TAG, "onReroute: longitude = " + startInfo.longitude + ", latitude = " + startInfo.latitude);
        mPathPoints.mStartPoints[0].mPos.lat = startInfo.latitude;
        mPathPoints.mStartPoints[0].mPos.lon = startInfo.longitude;

        double v = TbtUnionUtil.calcDistanceBetweenPoints(new Coord2DDouble(startInfo.longitude, startInfo.latitude),
         new Coord2DDouble(endInfo.longitude, endInfo.latitude));
        Log.i(TAG, "重算路 : v = " + v);
        if (vias.size() == 0 && v <= 50000) {  //有途经点或者距离大于50KM就设置为不是多备选
            flag |= 0x04;  //多备选、去重
            multiRouteFlag = true;

        } else {
            multiRouteFlag = false;
            rerouteOption.setRequestRouteType(RouteOptionEnum.RequestRouteTypeBest);  //只算一条路
        }
        rerouteOption.setConstrainCode(flag); //多备选
        if (mPoiForRequest != null) {
            mPoiForRequest.recycle();
            mPoiForRequest = null;
        }
        mPoiForRequest = POIForRequest.obtain();
        mPoiForRequest.addPoint(PointTypeStart, startInfo);
        mPoiForRequest.addPoint(PointTypeEnd, endInfo);
        rerouteOption.setPOIForRequest(mPoiForRequest);
        int i = NaviManager.getInstance().requestReroute(rerouteOption);   //重新算路
        Log.i(TAG, "重算路 onReroute: i = " + i);
        */
    }

    @Override
    public void onCarOnRouteAgain() {
        Log.i(TAG, "onCarOnRouteAgain: ");
    }

    /**
     * @brief 光柱信息数据更新
     * @param[in] lightBarInfo 光柱结构
     * @param[in] passedIdx 为lightBar的索引，表示行驶过路段索引
     * @param[in] dataStatus TMC数据状态，是否有更新TMC路况数据
     */
    @Override
    public void onUpdateTMCLightBar(List<LightBarInfo> ligntInfo, int passedIdx, boolean dataStatus) {
        Log.i(TAG, "onUpdateTMCLightBar: passedIdx = " + passedIdx + ", dataStatus = " + dataStatus);
        if (dataStatus && ligntInfo != null) {
            // mIBizLayerService.updateRouteTmc(AutoOverlayType.RouteOverlayGuide, (ArrayList<LightBarInfo>) ligntInfo);
        }

    }

    /**
     * @return void 无返回值
     * @brief 传出拥堵时长和原因
     * @details 导航过程中传出拥堵时长和原因
     * @param[in] info 传出拥堵时长
     * @note thread mutil
     * @note BL内聚
     */
    @Override
    public void onUpdateTMCCongestionInfo(NaviCongestionInfo info) {
        Log.i(TAG, "onUpdateTMCCongestionInfo: tmc拥堵信息回调了");
        if (info != null) {
            Log.i(TAG, "onUpdateTMCCongestionInfo: 拥堵时长 = " + info.totalTimeOfSeconds);
            Log.i(TAG, "onUpdateTMCCongestionInfo: 当前总剩余拥堵长度 = " + info.totalRemainDist);
        }
    }

    /**
     * @return void 无返回值
     * @brief 传出交通事件信息
     * @details 传出sdk获取的交通事件信息, 以及大数据挖据事件信息，用于终端众验
     * @param[in] pathsTrafficEventInfo 事件数组
     * @param[in] pathCount 事件个数
     * @note thread mutil
     * @note BL内聚
     */
    @Override
    public void onUpdateTREvent(List<PathTrafficEventInfo> infoList, int act) {
        Log.i(TAG, "onUpdateTREvent: ");
    }

    /**
     * @return void 无返回值
     * @brief 传出交通事件信息，用于终端显示
     * @details 路况播报与终端显示统一
     * @param[in] info 事件信息
     * @note thread mutil
     */
    @Override
    public void onUpdateTRPlayView(RouteTrafficEventInfo info) {
        Log.i(TAG, "onUpdateTRPlayView: ");
    }

    /**
     * @return void 无返回值
     * @brief 事件上报回调
     * @details 显示常规拥堵或者非正常拥堵事件位置
     * @param[in] incident 事件信息
     * @note thread mutil
     */
    @Override
    public void onShowTMCIncidentReport(TMCIncidentReport incident) {
        Log.i(TAG, "onShowTMCIncidentReport: ");
    }

    /**
     * @return void 无返回值
     * @brief 隐藏常规拥堵或者非正常拥堵事件
     * @details 导航过程中通知常规拥堵或者非正常拥堵事件
     * @param[in] type 拥堵事件类型
     * @note thread mutil
     */
    @Override
    public void onHideTMCIncidentReport(int type) {
        Log.i(TAG, "onHideTMCIncidentReport: ");
    }

    /**
     * @return void 无返回值
     * @brief 对外输出socol采集时间段
     * @details 导航过程中输出socol采集时间段
     * @param[in] text 对外输出字符串信息
     * @note thread mutil
     */
    @Override
    public void onUpdateSocolText(String text) {
        Log.i(TAG, "onUpdateSocolText: text = " + text);
        mTextView.setText(text);
    }

    /**
     * @return void 无返回值
     * @brief 更新是否支持简易三维导航
     * @details guide根据ILink接口IsSupport3DNavigation，\n
     * 判断自车所在Link是否支持简易三维导航,当状态变换时，通知给客户端
     * @param[in] support false:不支持, true:支持
     * @note thread mutil
     */
    @Override
    public void onUpdateIsSupportSimple3D(boolean support) {
        Log.i(TAG, "onUpdateIsSupportSimple3D: ");
    }

    /**
     * @brief 删除对应id的路线, 如过分歧点，不需要重新设置naviPath给引擎
     * @details guide根据情况，通知删除id为pathID的路线
     * @param[in] pathIDList 路线id
     */
    @Override
    public void onDeletePath(List<Long> pathIDList) {
        Log.i(TAG, "onDeletePath: pathIDList = " + pathIDList);
        //Toast.makeText(GuideActivity.this, "路过分歧点了，删除对应ID的路线", Toast.LENGTH_SHORT).show();
        if (pathIDList != null && pathIDList.size() > 0) {
            for (int i = 0; i < pathIDList.size(); i++) {
                Log.i(TAG, "路线ID： " + pathIDList.get(i));
            }
        } else {
            Log.i(TAG, "onDeletePath: pathIDList为空或者长度为0");
        }
    }

    /**
     * @param pathID 主选路线id
     * @brief 切换主选路线，引擎定位检测到走到备选路线，回调通知切换路线，主动选择不会有此通知
     * @details guide根据情况，通知将id为pathID的备选路线切换为主选路线
     */
    @Override
    public void onChangeNaviPath(long pathID) {
        Log.i(TAG, "onChangeNaviPath: ");
    }

    /**
     * @param pathID 当前主导航路线id
     * @param result 是否成功原因（1:成功，2:失败,PathID无效,3:失败,因为id和当前主选路线一致）
     * @brief 通知用户切换主导航路线状态，客户端主动SelectMainPathID切换的回调状态
     */
    @Override
    public void onSelectMainPathStatus(long pathID, int result) {
        Log.i(TAG, "onSelectMainPathStatus: ");
    }

    /**
     * @brief 建议用户切备选路线，TMC更新，备选路线更优。
     * @param[in] dwNewPathID 新路线athId
     * @param[in] dwOldPathID 旧路线PathId
     * @param[in] reason 建议切换的原因
     */
    @Override
    public void onSuggestChangePath(long newPathID, long oldPathID, SuggestChangePathReason reason) {
        Log.i(TAG, "onSuggestChangePath: ");
        // Toast.makeText(this, "建议用户切备选路线，TMC更新，备选路线更优", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onObtainAsyncInfo(ObtainInfo info) {
        Log.i(TAG, "onObtainAsyncInfo: ");
    }

    @Override
    public void onShowSameDirectionMixForkInfo(List<MixForkInfo> list) {
        Log.i(TAG, "onShowSameDirectionMixForkInfo: ");
    }

}

