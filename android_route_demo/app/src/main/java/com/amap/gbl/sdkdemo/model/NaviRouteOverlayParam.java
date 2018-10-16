package com.amap.gbl.sdkdemo.model;

import android.util.Log;

import com.amap.gbl.sdkdemo.R;
import com.amap.gbl.sdkdemo.model.MarkUtils;
import com.amap.gbl.sdkdemo.platform.CommonUtil;
import com.amap.gbl.sdkdemo.platform.ResUtil;
import com.autonavi.gbl.biz.bizenum.PathLineStyleType;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.gloverlay.GLLineItemType;
import com.autonavi.gbl.map.gloverlay.MapRouteTexture;
import com.autonavi.gbl.map.gloverlay.PolylineCapTextureInfo;
import com.autonavi.gbl.map.gloverlay.PolylineTextureInfo;
import com.autonavi.gbl.map.gloverlay.RouteOverlayParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  on 2017/10/21.
 *
 * @author wenguan.chen
 */

public class NaviRouteOverlayParam implements IRouteOverlayParam {
    public boolean isNightMode;
    private final String TAG = "NaviRouteOverlayParam";
    private final static int WIDTH_DEFAULT = 38;
    /**
     * 2.1渲染引擎是将客户端传入的值乘以4，目前渲染取消这个操作，g目前由客户端计算传入，2.1的小地图宽度为5
     */
    private final static int WIDHT_HAWKEYE = 5 * 4;

    private List<RouteOverlayParam> mOverlayParmList;

    private static class Singleton {
        private static NaviRouteOverlayParam instance = new NaviRouteOverlayParam();
    }

    public static NaviRouteOverlayParam getInstance() {
        return Singleton.instance;
    }

    private NaviRouteOverlayParam() {

    }

    @Override
    public List<RouteOverlayParam> getPropertys(GLMapView mapView, int type, boolean isMultiple) {
        // 是否为小地图
        boolean isEaglLine = false;
        if (type == PathLineStyleType.EaglEyeMapNormal || type == PathLineStyleType.EaglEyeMapHightLight
                || type == PathLineStyleType.EaglEyeMapOffline
                || type == PathLineStyleType.EaglEyeMapOfflineHightLight) {
            isEaglLine = true;
        }

        // 是否为离线style
        boolean isOfflineStyle = false;
        if (type == PathLineStyleType.MainMapOffline || type == PathLineStyleType.EaglEyeMapOffline
                || type == PathLineStyleType.EaglEyeMapOfflineHightLight
                || type == PathLineStyleType.MainMapOfflineHightLight) {
            isOfflineStyle = true;
        }

        // 非高亮路线(包括动态导航的更优路线，和备选路线）
        boolean isHighLight = true;
        //是否为备选路线;
        boolean isAlternative = false;
        if (type == PathLineStyleType.MainMapNormal || type == PathLineStyleType.EaglEyeMapNormal ||
                type == PathLineStyleType.MainMapOffline || type == PathLineStyleType.EaglEyeMapOffline) {
            isHighLight = false;
            if (isMultiple) {
                //若非高亮并且开启了多备选选项时;则即为备选路线;
                isAlternative = true;
            }
        }

        mOverlayParmList = new ArrayList<>();
        for (Texture texture : Texture.values()) {
            int lineWidth = isEaglLine ? WIDHT_HAWKEYE : WIDTH_DEFAULT;
            //是否需要箭头，鹰眼图不需要
            boolean needShowArrow = isEaglLine ? false : getLineNeedShowArrow(texture.lineType);
            RoadParam roadParam = getRoadParam(texture.textureType, isOfflineStyle, isAlternative, isHighLight);
            int tempLineWidth = lineWidth;
            if (texture.lineType == GLLineItemType.TYPE_MARKER_LINE_DOT) {
                tempLineWidth = 24;
            } else if (texture.lineType == GLLineItemType.TYPE_MARKER_LINE_FERRY) {
                tempLineWidth = lineWidth * 2 / 3;
            }

            OverlayParamBoolean paramBoolean = getOverlayParmBoolean(texture.lineType);
            RouteOverlayParam overlayParam = new RouteOverlayParam();
            overlayParam.needColorGradient = true;
            overlayParam.routeTexture = texture.textureType;
            overlayParam.lineTextureInfo = getRouteTextureInfo(texture.lineType);
            overlayParam.lineCapTextureInfo = getRouteCapTextureInfo(texture.lineType);
            overlayParam.lineWidth = tempLineWidth;
            overlayParam.fillColor = roadParam.fillLineColor;
            overlayParam.borderColor = roadParam.borderColor;
            overlayParam.fillMarker = roadParam.fillLineId;
            overlayParam.borderMarker = roadParam.borderLineId;
            overlayParam.lineExtract = paramBoolean.isLineExtract;
            overlayParam.useColor = paramBoolean.isUseColor;
            overlayParam.useCap = paramBoolean.isUseCap;
            overlayParam.canBeCovered = paramBoolean.isCanBeCovered;
            overlayParam.showArrow = needShowArrow;
            overlayParam.borderLineWidth = tempLineWidth;
            overlayParam.texPreMulAlpha = true;
//            overlayParam.selectFillColor = 0xff00ff00;        /**<选中的填充颜色 */
//            overlayParam.unSelectFillColor= 0xff00ff00;      /**<未选中的填充颜色*/
//            overlayParam.selectBorderColor = 0xff0000ff;      /**<选中的边线颜色*/
//            overlayParam.unSelectBorderColor= 0xff0000ff;
            overlayParam.selectFillColor = roadParam.selectFillColor;        /**<选中的填充颜色 */
            overlayParam.unSelectFillColor = roadParam.unSelectFillColor;      /**<未选中的填充颜色*/
            overlayParam.selectBorderColor = roadParam.selectBorderColor;      /**<选中的边线颜色*/
            overlayParam.unSelectBorderColor = roadParam.unSelectBorderColor;   /**<未选中的边线颜色*/
            MarkUtils.createLineTexure(mapView, texture.lineType, roadParam.fillLineId);
            MarkUtils.createLineTexure(mapView, texture.lineType, roadParam.borderLineId);
            mOverlayParmList.add(overlayParam);
        }
        return mOverlayParmList;
    }

    public PolylineTextureInfo getRouteTextureInfo(int lineType) {
        float x1 = 0f;
        float y1 = 0f;
        float x2 = 0f;
        float y2 = 0f;
        float glTexLen = 0f;

        switch (lineType) {
            //id==3000 || id ==3050
            case GLLineItemType.TYPE_MARKER_LINE_COLOR: {
                x1 = 0.05f;
                y1 = 0.5f;
                x2 = 0.95f;
                y2 = 0.5f;
                glTexLen = 16;
                break;
            }
            //id < 3000
            case GLLineItemType.TYPE_MARKER_LINE: {
                x1 = 0.0f;
                y1 = 0.5f;
                x2 = 1.0f;
                y2 = 0.5f;
                glTexLen = 16;
                break;
            }
            //id > 3000 && id < 3003
            case GLLineItemType.TYPE_MARKER_LINE_ARROW: {
                x1 = 0.0f;
                y1 = 1.0f;
                x2 = 1.0f;
                y2 = 0.0f;
                if (true) {
                    glTexLen = 48;
                } else {
                    glTexLen = 32;
                }
                break;
            }
            //id >= 3003 && id< 3010
            case GLLineItemType.TYPE_MARKER_LINE_DOT: {
                x1 = 0.0f;
                y1 = 1.0f;
                x2 = 1.0f;
                y2 = 0.0f;
                glTexLen = 30;
                break;
            }
            //id >= 3003 && id< 3010
            case GLLineItemType.TYPE_MARKER_LINE_FERRY: {
                x1 = 0.0f;
                y1 = 1.0f;
                x2 = 1.0f;
                y2 = 0.0f;
                glTexLen = 16;
                break;
            }
            //id > = 3010
            case GLLineItemType.TYPE_MARKER_LINE_DOT_COLOR: {
                x1 = 0.0f;
                y1 = 1.0f;
                x2 = 1.0f;
                y2 = 0.0f;
                glTexLen = 16;
                break;
            }
            case GLLineItemType.TYPE_MARKER_LINE_RESTRICT: {
                x1 = 0.0f;
                y1 = 1.0f;
                x2 = 0.5f;
                y2 = 0.0f;
                glTexLen = 32;
                break;
            }
            default:
                break;
        }

        PolylineTextureInfo textureInfo = new PolylineTextureInfo();
        textureInfo.x1 = x1;
        textureInfo.y1 = y1;
        textureInfo.x2 = x2;
        textureInfo.y2 = y2;
        textureInfo.textureLen = glTexLen;
        return textureInfo;
    }

    public PolylineCapTextureInfo getRouteCapTextureInfo(int lineType) {

        float x1 = 0f;
        float y1 = 0f;
        float x2 = 0f;
        float y2 = 0f;

        switch (lineType) {
            //id==3000 || id ==3050
            case GLLineItemType.TYPE_MARKER_LINE_COLOR: {
                x1 = 0.05f;
                y1 = 0.5f;
                x2 = 0.95f;
                y2 = 0.75f;
                break;
            }
            //id < 3000
            case GLLineItemType.TYPE_MARKER_LINE: {
                x1 = 0.0f;
                y1 = 0.5f;
                x2 = 1.0f;
                y2 = 0.75f;
                break;
            }
            //id > 3000 && id < 3003
            case GLLineItemType.TYPE_MARKER_LINE_FERRY:
            case GLLineItemType.TYPE_MARKER_LINE_ARROW: {
                x1 = 0.5f;
                y1 = 0.25f;
                x2 = 1.0f;
                y2 = 0.6f;
                break;
            }
            //id >= 3003 && id< 3010
            case GLLineItemType.TYPE_MARKER_LINE_DOT: {
                break;
            }
            //id > = 3010
            case GLLineItemType.TYPE_MARKER_LINE_DOT_COLOR: {
                break;
            }
            case GLLineItemType.TYPE_MARKER_LINE_RESTRICT: {
                x1 = 0.5f;
                y1 = 0.25f;
                x2 = 1.0f;
                y2 = 0.6f;
                break;
            }
            default:
                break;
        }
        PolylineCapTextureInfo textureInfo = new PolylineCapTextureInfo();
        textureInfo.x1 = x1;
        textureInfo.y1 = y1;
        textureInfo.x2 = x2;
        textureInfo.y2 = y2;
        return textureInfo;
    }

    public boolean getLineNeedShowArrow(int lineType) {
        switch (lineType) {
            case GLLineItemType.TYPE_MARKER_LINE_DOT:
                return false;
            case GLLineItemType.TYPE_MARKER_LINE:
                return true;
            case GLLineItemType.TYPE_MARKER_LINE_ARROW:
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取不同道路类型的纹理及颜色
     *
     * @param textureType   道路类型
     * @param isOffline     是否离线
     * @param isAlternative 是否备选
     * @return
     */
    public RoadParam getRoadParam(int textureType, boolean isOffline, boolean isAlternative, boolean isHighLight) {
        int fillLineId;
        int borderLineId;
        int fillLineColor;
        int borderColor;
        int selectFillColor = -1;       /**<选中的填充颜色 */
        int unSelectFillColor = -1;      /**<未选中的填充颜色*/
        int selectBorderColor = -1;     /**<选中的边线颜色*/
        int unSelectBorderColor = -1;    /**<未选中的边线颜色*/

        switch (textureType) {
            // 畅通
            case MapRouteTexture.MapRouteTextureOpen:

                fillLineId = R.drawable.map_lr_road_white_front;
                borderLineId = R.drawable.map_lr_road_white_back;
                if (isNightMode) {
                    Log.i(TAG, "getRoadParam: 夜晚");
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xff24714e;
                        borderColor = 0xff5b5c5e;
                    } else {
                        fillLineColor = 0xff0ee180;
                        borderColor = 0xff073e26;
                    }

                    selectFillColor = 0xff0ee180;
                    selectBorderColor = 0xff073e26;
                    unSelectFillColor = 0xff24714e;
                    unSelectBorderColor = 0xff5b5c5e;
                } else {  //白天模式
                    Log.i(TAG, "getRoadParam: 白天");
                    if (isAlternative || !isHighLight) {  //多备选路线
                        fillLineColor = 0xff83bea9;
                        borderColor = 0xff969798;
                        selectFillColor = 0xff27ffb3;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xff83bea9;
                        unSelectBorderColor = 0xff969798;
                    } else {                           //选中路线
                        fillLineColor = 0xff27ffb3;
                        borderColor = 0xff969798;
                        selectFillColor = 0xff27ffb3;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xff83bea9;
                        unSelectBorderColor = 0xff969798;
                    }
                }
                break;

            // 缓行
            case MapRouteTexture.MapRouteTextureAmble:
                fillLineId = R.drawable.map_lr_road_white_front;
                borderLineId = R.drawable.map_lr_road_white_back;
                if (isNightMode) {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xff5f5723;
                        borderColor = 0xff5b5c5e;
                    } else {
                        fillLineColor = 0xffbaa831;
                        borderColor = 0xff453c04;
                    }
                    selectFillColor = 0xffbaa831;
                    selectBorderColor = 0xff453c04;
                    unSelectFillColor = 0xff5f5723;
                    unSelectBorderColor = 0xff5b5c5e;
                } else {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xffd1c392;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffffd337;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffd1c392;
                        unSelectBorderColor = 0xff969798;
                    } else {
                        fillLineColor = 0xffffd337;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffffd337;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffd1c392;
                        unSelectBorderColor = 0xff969798;
                    }
                }
                break;

            // 拥堵
            case MapRouteTexture.MapRouteTextureJam:

                fillLineId = R.drawable.map_lr_road_white_front;
                borderLineId = R.drawable.map_lr_road_white_back;
                if (isNightMode) {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xff67323d;
                        borderColor = 0xff5b5c5e;
                    } else {
                        fillLineColor = 0xffba314e;
                        borderColor = 0xff4b0918;
                    }
                    selectFillColor = 0xffba314e;
                    selectBorderColor = 0xff4b0918;
                    unSelectFillColor = 0xff67323d;
                    unSelectBorderColor = 0xff5b5c5e;
                } else {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xffd59391;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffed4468;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffd59391;
                        unSelectBorderColor = 0xff969798;
                    } else {
                        fillLineColor = 0xffed4468;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffed4468;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffd59391;
                        unSelectBorderColor = 0xff969798;
                    }
                }
                break;

            // 严重拥堵
            case MapRouteTexture.MapRouteTextureCongested:
                fillLineId = R.drawable.map_lr_road_white_front;
                borderLineId = R.drawable.map_lr_road_white_back;
                if (isNightMode) {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0x41151e;
                        borderColor = 0x5b5c5e;
                    } else {
                        fillLineColor = 0xff680f22;
                        borderColor = 0xff2a0409;
                    }
                    selectFillColor = 0xff680f22;
                    selectBorderColor = 0xff2a0409;
                    unSelectFillColor = 0x41151e;
                    unSelectBorderColor = 0x5b5c5e;
                } else {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xffac7171;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffba2361;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffac7171;
                        unSelectBorderColor = 0xff969798;
                    } else {
                        fillLineColor = 0xffba2361;
                        borderColor = 0xff969798;
                        selectFillColor = 0xffba2361;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xffac7171;
                        unSelectBorderColor = 0xff969798;
                    }
                }
                break;

            // 未知路况
            case MapRouteTexture.MapRouteTextureDefault:
                fillLineId = R.drawable.map_lr_road_white_front;
                borderLineId = R.drawable.map_lr_road_white_back;
                if (isNightMode) {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xff0a4c71;
                        borderColor = 0xff5b5c5e;
                    } else {
                        fillLineColor = 0xff0082ff;
                        borderColor = 0xff0082ff;
                    }
                    selectFillColor = 0xff0082ff;
                    selectBorderColor = 0xff0082ff;
                    unSelectFillColor = 0xff0a4c71;
                    unSelectBorderColor = 0xff5b5c5e;
                } else {
                    if (isAlternative || !isHighLight) {
                        fillLineColor = 0xff88a3be;
                        borderColor = 0xff969798;
                        selectFillColor = 0xff0082ff;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xff88a3be;
                        unSelectBorderColor = 0xff969798;
                    } else {
                        fillLineColor = 0xff0082ff;
                        borderColor = 0xff969798;
                        selectFillColor = 0xff0082ff;
                        selectBorderColor = 0xff969798;
                        unSelectFillColor = 0xff88a3be;
                        unSelectBorderColor = 0xff969798;
                    }
                }
                break;
            // 内部道路
            case MapRouteTexture.MapRouteTextureNavi:
                fillLineId = R.drawable.map_lr_dott_car_fill;
                borderLineId = R.drawable.map_lr_dott_car_border;
                fillLineColor = ResUtil.getColor(R.color.auto_color_ff0096ff);
                borderColor = ResUtil.getColor(R.color.auto_color_ff0096ff);
                selectFillColor = 0xff0096ff;
                selectBorderColor = 0xff0096ff;
                unSelectFillColor = 0xff6696ff;
                unSelectBorderColor = 0xff6696ff;
                break;
            // 内部道路备选状态
            case MapRouteTexture.MapRouteTextureNonavi:
                fillLineId = R.drawable.map_lr_dott_car_light;
                borderLineId = R.drawable.map_lr_dott_car_light;
                fillLineColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                borderColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                selectFillColor = 0xffffffff;
                selectBorderColor = 0xffffffff;
                unSelectFillColor = 0xffffffff;
                unSelectBorderColor = 0xffffffff;
                break;
            // 道路内部箭头
            case MapRouteTexture.MapRouteTextureArrow:
                fillLineId = R.drawable.map_aolr;
                borderLineId = R.drawable.map_aolr;
                fillLineColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                borderColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                selectFillColor = 0xffffffff;
                selectBorderColor = 0xffffffff;
                unSelectFillColor = 0xffffffff;
                unSelectBorderColor = 0xffffffff;
                break;
            // 轮渡线
            case MapRouteTexture.MapRouteTextureFerry:
                fillLineId = R.drawable.map_ferry;
                borderLineId = R.drawable.map_ferry;
                fillLineColor = 0xff0096ff;
                borderColor = 0xff0096ff;
                selectFillColor = 0xff0096ff;
                selectBorderColor = 0xff0096ff;
                unSelectFillColor = 0xff6696ff;
                unSelectBorderColor = 0xff6696ff;
                break;
            // 限行
            case MapRouteTexture.MapRouteTextureLimit:
                if (isNightMode) {
                    if (isAlternative || !isHighLight) {
                        fillLineId = R.drawable.map_traffic_platenum_restrict_light;
                    } else {
                        fillLineId = R.drawable.map_traffic_platenum_restrict_hl;
                    }
                } else {
                    if (isAlternative || !isHighLight) {
                        fillLineId = R.drawable.map_traffic_platenum_restrict_light;
                    } else {
                        fillLineId = R.drawable.map_traffic_platenum_restrict_hl;
                    }
                }
                borderLineId = -1;
                fillLineColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                borderColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                selectFillColor = 0xffffffff;
                selectBorderColor = 0xffffffff;
                unSelectFillColor = 0xffffffff;
                unSelectBorderColor = 0xffffffff;
                break;
            default:
                fillLineId = -1;
                borderLineId = -1;
                fillLineColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                borderColor = ResUtil.getColor(R.color.auto_color_ffffff_ff);
                selectFillColor = 0xffffffff;
                selectBorderColor = 0xffffffff;
                unSelectFillColor = 0xffffffff;
                unSelectBorderColor = 0xffffffff;
                break;
        }
        RoadParam roadParam = new RoadParam();
        roadParam.fillLineId = fillLineId;
        roadParam.borderLineId = borderLineId;
        roadParam.fillLineColor = fillLineColor;
        roadParam.borderColor = borderColor;
        roadParam.selectFillColor = selectFillColor;
        roadParam.selectBorderColor = selectBorderColor;
        roadParam.unSelectFillColor = unSelectFillColor;
        roadParam.unSelectBorderColor = unSelectBorderColor;
        return roadParam;

    }

    /**
     * @param lineType
     * @return
     */

    private OverlayParamBoolean getOverlayParmBoolean(int lineType) {
        boolean isUseCap = false;
        boolean isUseColor = true;
        boolean isLineExtract = false;
        boolean isCanBeCovered = false;

        switch (lineType) {
            //id==3000 || id ==3050
            case GLLineItemType.TYPE_MARKER_LINE_COLOR: {
                isUseCap = true;
                isUseColor = true;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            //id < 3000
            case GLLineItemType.TYPE_MARKER_LINE: {
                isUseCap = true;
                isUseColor = true;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            //id > 3000 && id < 3003
            case GLLineItemType.TYPE_MARKER_LINE_ARROW: {
                isUseCap = false;
                isUseColor = false;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            //id >= 3003 && id< 3010
            case GLLineItemType.TYPE_MARKER_LINE_FERRY:
            case GLLineItemType.TYPE_MARKER_LINE_DOT: {
                isUseCap = false;
                isUseColor = true;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            //id > = 3010
            case GLLineItemType.TYPE_MARKER_LINE_DOT_COLOR: {
                isUseCap = false;
                isUseColor = true;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            case GLLineItemType.TYPE_MARKER_LINE_RESTRICT: {
                isUseCap = true;
                isUseColor = false;
                isLineExtract = false;
                isCanBeCovered = true;
                break;
            }
            default:
                break;
        }
        OverlayParamBoolean paramBoolean = new OverlayParamBoolean();
        paramBoolean.isUseCap = isUseCap;
        paramBoolean.isUseColor = isUseColor;
        paramBoolean.isLineExtract = isLineExtract;
        paramBoolean.isCanBeCovered = isCanBeCovered;
        return paramBoolean;
    }

    class OverlayParamBoolean {
        public boolean isLineExtract;
        public boolean isUseCap;
        public boolean isUseColor;
        public boolean isCanBeCovered;
    }

    class RoadParam {
        /**
         * 道路纹理
         */
        public int fillLineId;
        /**
         * 描边纹理
         */
        public int borderLineId;
        /**
         * 道路颜色
         */
        public int fillLineColor;
        /**
         * 描边颜色
         */
        public int borderColor;

        public int selectFillColor;
        /**
         * <选中的填充颜色
         */
        public int unSelectFillColor;
        /**
         * <未选中的填充颜色
         */
        public int selectBorderColor;
        /**
         * <选中的边线颜色
         */
        public int unSelectBorderColor;    /**<未选中的边线颜色*/
    }

    public enum Texture {
        // 限行
        LIMIT(MapRouteTexture.MapRouteTextureLimit, GLLineItemType.TYPE_MARKER_LINE_RESTRICT),
        // 轮渡线
        FERRY(MapRouteTexture.MapRouteTextureFerry, GLLineItemType.TYPE_MARKER_LINE_FERRY),
        // 道路内部箭头
        ARROW(MapRouteTexture.MapRouteTextureArrow, GLLineItemType.TYPE_MARKER_LINE_ARROW),
        // 内部道路
        NAVIABLE(MapRouteTexture.MapRouteTextureNavi, GLLineItemType.TYPE_MARKER_LINE_DOT),
        // 内部道路备选状态
        NONAVI(MapRouteTexture.MapRouteTextureNonavi, GLLineItemType.TYPE_MARKER_LINE_DOT),
        // 未知路况
        DEFAULT(MapRouteTexture.MapRouteTextureDefault, GLLineItemType.TYPE_MARKER_LINE),
        // 畅通
        OPEN(MapRouteTexture.MapRouteTextureOpen, GLLineItemType.TYPE_MARKER_LINE),
        // 缓行
        AMBLE(MapRouteTexture.MapRouteTextureAmble, GLLineItemType.TYPE_MARKER_LINE),
        // 拥堵
        JAM(MapRouteTexture.MapRouteTextureJam, GLLineItemType.TYPE_MARKER_LINE),
        // 严重拥堵
        CONGESTED(MapRouteTexture.MapRouteTextureCongested, GLLineItemType.TYPE_MARKER_LINE);

        public int textureType, lineType;

        Texture(int textureType, int lineType) {
            this.textureType = textureType;
            this.lineType = lineType;
        }
    }
}
