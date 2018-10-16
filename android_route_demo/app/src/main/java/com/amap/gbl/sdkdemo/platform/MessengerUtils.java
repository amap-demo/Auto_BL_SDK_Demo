package com.amap.gbl.sdkdemo.platform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.amap.gbl.sdkdemo.model.NaviConstant;
import com.amap.gbl.sdkdemo.presenter.IRouteObserverPresenter;
import com.amap.gbl.sdkdemo.presenter.RouteManager;
import com.autonavi.gbl.common.path.model.option.RerouteOption;
import com.autonavi.gbl.common.path.model.result.PathResult;
import com.autonavi.gbl.guide.model.CrossImageInfo;
import com.autonavi.gbl.guide.model.CruiseCongestionInfo;
import com.autonavi.gbl.guide.model.CruiseFacilityInfo;
import com.autonavi.gbl.guide.model.ExitDirectionInfo;
import com.autonavi.gbl.guide.model.LightBarInfo;
import com.autonavi.gbl.guide.model.ManeuverInfo;
import com.autonavi.gbl.guide.model.MixForkInfo;
import com.autonavi.gbl.guide.model.NaviCamera;
import com.autonavi.gbl.guide.model.NaviCongestionInfo;
import com.autonavi.gbl.guide.model.NaviInfo;
import com.autonavi.gbl.guide.model.NaviIntervalCamera;
import com.autonavi.gbl.guide.model.NaviIntervalCameraDynamicInfo;
import com.autonavi.gbl.guide.model.PathTrafficEventInfo;
import com.autonavi.gbl.guide.model.SoundInfo;
import com.autonavi.gbl.guide.observer.ICruiseObserver;
import com.autonavi.gbl.guide.observer.INaviObserver;
import com.autonavi.gbl.guide.observer.ISoundPlayObserver;
import com.autonavi.gbl.pos.model.LocInfo;
import com.autonavi.gbl.pos.model.LocParallelRoadInfo;
import com.autonavi.gbl.pos.model.LocSignData;
import com.autonavi.gbl.pos.observer.IPosLocInfoObserver;
import com.autonavi.gbl.pos.observer.IPosParallelRoadObserver;
import com.autonavi.gbl.pos.observer.IPosSwitchParallelRoadObserver;
import com.autonavi.gbl.route.observer.IRouteResultObserver;
import com.autonavi.gbl.search.model.SearchAlongWayResult;
import com.autonavi.gbl.search.model.SearchAroundRecommendResult;
import com.autonavi.gbl.search.model.SearchChargingLiveStatusResult;
import com.autonavi.gbl.search.model.SearchDeepInfoResult;
import com.autonavi.gbl.search.model.SearchDetailInfoResult;
import com.autonavi.gbl.search.model.SearchKeywordResult;
import com.autonavi.gbl.search.model.SearchNaviInfoResult;
import com.autonavi.gbl.search.model.SearchNearestResult;
import com.autonavi.gbl.search.model.SearchSuggestResult;
import com.autonavi.gbl.search.observer.intfc.IGSearchAlongWayObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchAroundRecommendObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchChargingLiveStatusObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchDeepInfoObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchDetailInfoObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchKeyWordObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchNaviInfoObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchNearestObserver;
import com.autonavi.gbl.search.observer.intfc.IGSearchSuggestionObserver;

import java.util.List;

/**
 * Created by zed.qzq on 2017/10/19.
 * 子线程转换到UI线程工具类
 */
public class MessengerUtils {
    public static String TAG = MessengerUtils.class.getSimpleName();


    private static class MessengerUtilsHolder {
        private static MessengerUtils mInstance = new MessengerUtils();
    }

    private MessengerUtils() {

    }

    public static MessengerUtils getInstance() {
        return MessengerUtils.MessengerUtilsHolder.mInstance;
    }

    public void sendMessage(Message msg) {
        mMessenger.sendMessage(msg);
    }

    public void sendMessage(int what) {
        mMessenger.sendMessage(newMessage(what));
    }

    public void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.what = what;
        mMessenger.sendMessage(msg);
    }

    public Message newMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        return msg;
    }

    //清除所有的线程消息
    public void clearAllMessages() {
        mMessenger.removeCallbacksAndMessages(null);
    }

    /**
     * 分发消息的Hanlder,将线程传递过来的消息，进行逻辑处理，并传递到Ui线程中，并通知相关的观察者
     */
    private final Handler mMessenger = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            int what = msg.what;
            switch (what) {
                case NaviConstant.HANDLER_ON_NEW_ROUTE:  //算路成功
                    if (RouteManager.getInstance().getRouteObservers() != null) {
                        for (IRouteObserverPresenter l : RouteManager.getInstance().getRouteObservers()) {
                            final Object[] objs = msg.obj != null ? (Object[]) msg.obj : null;
                            Bundle bundle = msg.getData();
                            l.onNewRoute(bundle.getInt("mode"), bundle.getInt("type"), (PathResult) objs[0], objs[1], bundle.getBoolean("isLocal"));
                        }
                    }
                    break;

                case NaviConstant.HANDLER_ON_ERROR_ROUTE: //算路失败
                    if (RouteManager.getInstance().getRouteObservers() != null) {
                        for (IRouteObserverPresenter l : RouteManager.getInstance().getRouteObservers()) {
                            Bundle bundle = msg.getData();
                            l.onNewRouteError(bundle.getInt("mode"), bundle.getInt("type"), bundle.getInt("errorCode"), msg.obj, bundle.getBoolean("isLocal"), bundle.getBoolean("isChange"));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}