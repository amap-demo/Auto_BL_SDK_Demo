package com.amap.gbl.sdkdemo.presenter;

import com.autonavi.gbl.common.path.model.result.PathResult;


public interface IRouteObserverPresenter {

    void onNewRoute(int mode, int type, PathResult pathResult, Object externData, boolean isLocal);

    void onNewRouteError(int mode, int type, int errorCode, Object externData, boolean isLocal, boolean isChange);

}
