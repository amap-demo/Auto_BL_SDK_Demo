package com.amap.gbl.sdkdemo.GuideUtils;

import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.gloverlay.RouteOverlayParam;

import java.util.List;

/**
 * Created on 2017/10/21.
 * @author wenguan.chen
 */
public interface IRouteOverlayParam {
    List<RouteOverlayParam> getPropertys(GLMapView mapView, int type, boolean isMultiple);
}
