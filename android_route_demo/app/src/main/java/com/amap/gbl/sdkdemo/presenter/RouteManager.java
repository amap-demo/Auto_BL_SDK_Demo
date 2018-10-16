package com.amap.gbl.sdkdemo.presenter;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.amap.gbl.sdkdemo.model.NaviConstant;
import com.amap.gbl.sdkdemo.platform.MessengerUtils;
import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.CommonUtil;
import com.amap.gbl.sdkdemo.platform.IOUtils;
import com.autonavi.gbl.common.model.UserConfig;
import com.autonavi.gbl.common.model.WorkPath;
import com.autonavi.gbl.common.path.model.option.RerouteOption;
import com.autonavi.gbl.common.path.model.option.RouteOption;
import com.autonavi.gbl.common.path.model.result.PathResult;
import com.autonavi.gbl.guide.GuideService;
import com.autonavi.gbl.guide.model.AsyncInfo;
import com.autonavi.gbl.guide.model.CrossImageInfo;
import com.autonavi.gbl.guide.model.CruiseCongestionInfo;
import com.autonavi.gbl.guide.model.CruiseFacilityInfo;
import com.autonavi.gbl.guide.model.CruiseInfo;
import com.autonavi.gbl.guide.model.CruiseTimeAndDist;
import com.autonavi.gbl.guide.model.DriveEventTip;
import com.autonavi.gbl.guide.model.ExitDirectionInfo;
import com.autonavi.gbl.guide.model.GuideEnum;
import com.autonavi.gbl.guide.model.LaneInfo;
import com.autonavi.gbl.guide.model.LightBarInfo;
import com.autonavi.gbl.guide.model.LockScreenTip;
import com.autonavi.gbl.guide.model.ManeuverInfo;
import com.autonavi.gbl.guide.model.MixForkInfo;
import com.autonavi.gbl.guide.model.NaviCamera;
import com.autonavi.gbl.guide.model.NaviCongestionInfo;
import com.autonavi.gbl.guide.model.NaviFacility;
import com.autonavi.gbl.guide.model.NaviInfo;
import com.autonavi.gbl.guide.model.NaviIntervalCamera;
import com.autonavi.gbl.guide.model.NaviIntervalCameraDynamicInfo;
import com.autonavi.gbl.guide.model.NaviPath;
import com.autonavi.gbl.guide.model.NaviStatisticsInfo;
import com.autonavi.gbl.guide.model.ObtainInfo;
import com.autonavi.gbl.guide.model.PathTrafficEventInfo;
import com.autonavi.gbl.guide.model.RouteTrafficEventInfo;
import com.autonavi.gbl.guide.model.SoundInfo;
import com.autonavi.gbl.guide.model.SuggestChangePathReason;
import com.autonavi.gbl.guide.model.TMCIncidentReport;
import com.autonavi.gbl.guide.observer.ICruiseObserver;
import com.autonavi.gbl.guide.observer.INaviObserver;
import com.autonavi.gbl.guide.observer.ISoundPlayObserver;
import com.autonavi.gbl.pos.PosService;
import com.autonavi.gbl.pos.model.LocInfo;
import com.autonavi.gbl.pos.model.LocModeType;
import com.autonavi.gbl.pos.model.LocParallelRoadInfo;
import com.autonavi.gbl.pos.model.LocSignData;
import com.autonavi.gbl.pos.model.PosEnum;
import com.autonavi.gbl.pos.observer.IPosLocInfoObserver;
import com.autonavi.gbl.pos.observer.IPosParallelRoadObserver;
import com.autonavi.gbl.pos.observer.IPosSwitchParallelRoadObserver;
import com.autonavi.gbl.pos.replay.PosReplayService;
import com.autonavi.gbl.route.RouteService;
import com.autonavi.gbl.route.model.RouteAosOption;
import com.autonavi.gbl.route.model.RouteServiceParam;
import com.autonavi.gbl.route.observer.IRouteResultObserver;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RouteManager implements IRouteResultObserver {
    public static String TAG = "12345";

    public RouteService mRouteService;

    //route观察者
    private List<IRouteObserverPresenter> mRouteObserverSet;

    public final MessengerUtils mMessenger;
    private long route = -9999;

    private static class RouteManagerHolder {
        private static RouteManager mInstance = new RouteManager();
    }

    private RouteManager() {
        mMessenger = MessengerUtils.getInstance();
    }

    public static RouteManager getInstance() {
        return RouteManagerHolder.mInstance;
    }

    /**
     * 初始化route，在UI线程中初始化。
     */
    public boolean init() {
        initRoute();
        return isInitSuccess();
    }

    private void initRoute() {
        mRouteService = (RouteService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.RouteSingleServiceID);
        Log.i(TAG, "initRouteService: mRouteService = " + mRouteService);
        RouteServiceParam param = new RouteServiceParam();
        String routeRootDir = IOUtils.getAppSDCardFileDir() + File.separator + "route";
        //auto_bl_demo
        CommonUtil.createSubDir(routeRootDir, "cache");
        CommonUtil.createSubDir(routeRootDir, "navi");
        CommonUtil.createSubDir(routeRootDir, "res");
        param.mPath = new WorkPath();
        param.mPath.cache = routeRootDir + File.separator + "cache";
        param.mPath.navi = routeRootDir + File.separator + "navi";
        param.mPath.res = routeRootDir + File.separator + "res";
        param.mConfig = new UserConfig();
        param.mConfig.deviceID = "1498635cd464d9150b27b7486e436a2f";
        param.mConfig.userBatch = "0";
        param.mConfig.userCode = "AN_Amap_IOS_FC";
        param.mRouteResReader = null;
        param.mThreadObserver = null;
        route = mRouteService.init(param);
        Log.i(TAG, "initRouteService: route = " + route);
        mRouteService.addRouteResultObserver(this);  //注册观察者
    }

    public boolean isInitSuccess() {
        if (route == 0 ) {
            Log.i(TAG, "isInitSuccess: route version = " + mRouteService.getRouteVersion());
            return true;
        } else {
            return false;
        }
    }

    public void uninit() {
        //不再回调任何信息
        mMessenger.clearAllMessages();
        if (mRouteService != null) {
            //route观察者
            mRouteObserverSet.clear();
            mRouteObserverSet = null;
        }

        destoryRoute();
        route = -1;
    }

    private void destoryRoute() {
        if (mRouteService != null) {
            mRouteService.removeRouteResultObserver(this);
            ServiceMgr.getServiceMgrInstance()
                    .removeBLService(ServiceManagerEnum.RouteSingleServiceID);
            mRouteService = null;
        }
    }

    /**
     * route开关
     *
     * @param key
     * @param value
     * @return
     */
    public boolean controlRoute(int key, String value) {
        return mRouteService.control(key, value);
    }


    // ============================ 观察者 ================================


    //RouteObserver
    public void registerRouteObserver(IRouteObserverPresenter l) {
        if (mRouteObserverSet == null) {
            mRouteObserverSet = new CopyOnWriteArrayList<>();
        }
        if (!mRouteObserverSet.contains(l)) {
            mRouteObserverSet.add(l);
        }
    }

    public void unregisterRouteObserver(IRouteObserverPresenter l) {
        if (mRouteObserverSet != null) {
            if (mRouteObserverSet.contains(l)) {
                mRouteObserverSet.remove(l);
            }
        }
    }

    public List<IRouteObserverPresenter> getRouteObservers() {
        return mRouteObserverSet;
    }


    // ============================ route 方法 ================================
    /**
     * 算路请求
     * 可能只有一条路线
     *
     * @param routeOption
     * @return
     */
    public int requestRoute(RouteOption routeOption) {
        if (null != mRouteService) {
            int code = mRouteService.requestRoute(routeOption);
            Log.i(TAG, "requestRoute: code = " + code);// 1代表成功
            return code;
        }
        return -1;
    }

    /**
     * 重新算路请求
     *
     * @param rerouteOption
     * @return
     */
    public int requestReroute(RerouteOption rerouteOption) {
        if (null != mRouteService) {
            int code = mRouteService.requestReroute(rerouteOption);
            Log.i(TAG, "requestRoute: code = " + code);// 1代表成功
            return code;
        }
        return -1;
    }

    /**
     * AOS算路
     *
     * @param routeAosOption
     * @return
     */
    public int requestAosRoute(RouteAosOption routeAosOption) {
        if (null != mRouteService) {
            int code = mRouteService.requestAosRoute(routeAosOption);
            Log.i(TAG, "requestRoute: aoscode = " + code);// 1代表成功
            return code;
        }
        return -1;
    }


    /**
     * 取消路线规划
     */
    public void abortRoutePlan() {
        if (null != mRouteService) {
            mRouteService.abortRequest();
        }
    }

    /**
     * 取消AOS路线规划
     */
    public void abortAOSRoutePlan() {
        if (null != mRouteService) {
            mRouteService.abortAllAosRouteRequest();
        }
    }



    public String getAosRequestRouteURL(RouteAosOption option) {
        if (null != mRouteService) {
            return mRouteService.getAosRequestRouteURL(option);
        }

        return "";
    }


    public String getRouteVersion() {
        if (null != mRouteService) {
            return mRouteService.getRouteVersion();
        }

        return "";
    }

    public String getEngineVersion() {
        if (null != mRouteService) {
            return mRouteService.getEngineVersion();
        }

        return "";
    }

    //============================RouteObserver接口实现 start============================//

    /**
     * @param mode          算路模式（enum RouteMode）
     * @param type          算路类型
     * @param pathResult    路径规划结果由客户端自行释放
     * @param externDataPtr 附带的路径数据
     *                      当type = RouteTypeTMC  -> AvoidJamArea
     *                      当type = RouteTypeLimexternDataitLine -> AvoidRestrictArea
     *                      当type = RouteTypeLimitForbid -> ForbiddenInfo
     *                      当type = RouteTypeLimitForbidOffLine -> ForbiddenInfo
     *                      当type = RouteTypeDamagedRoad -> RoadClosedArea
     * @param isLocal       是否本地规划
     * @brief 算路结果通知
     * @details 算路成功时，通知并发送算路结果
     */
    @Override
    public void onNewRoute(int mode, int type, PathResult pathResult, Object externDataPtr, boolean isLocal) {
        Log.i(TAG, "RouteActivity onNewRoute: ");
        Message msg = mMessenger.newMessage(NaviConstant.HANDLER_ON_NEW_ROUTE);
        Object[] objs = new Object[2];
        objs[0] = pathResult;
        objs[1] = externDataPtr;
        msg.obj = objs;
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        bundle.putInt("type", type);
        bundle.putBoolean("isLocal", isLocal);
        msg.setData(bundle);
        mMessenger.sendMessage(msg);
    }

    @Override
    public void onNewRouteError(int mode, int type, int errorCode, Object externDataPtr, boolean isLocal, boolean isChange) {
        Log.i(TAG, "RouteActivity onNewRouteError: " + "mode:" + mode + " type:"+type + " errorCode:" + errorCode);
        Message msg = mMessenger.newMessage(NaviConstant.HANDLER_ON_ERROR_ROUTE);
        msg.obj = externDataPtr;
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        bundle.putInt("type", type);
        bundle.putInt("errorCode", errorCode);
        bundle.putBoolean("isLocal", isLocal);
        bundle.putBoolean("isChange", isChange);
        msg.setData(bundle);
        mMessenger.sendMessage(msg);
    }
}
