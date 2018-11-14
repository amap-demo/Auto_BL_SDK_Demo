package com.amap.gbl.sdkdemo.GuideUtils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.amap.gbl.sdkdemo.CommonUtil;
import com.autonavi.gbl.biz.bizenum.AutoCarStyleParam;
import com.autonavi.gbl.biz.bizenum.AutoOverlayType;
import com.autonavi.gbl.biz.bizenum.LineItemType;
import com.autonavi.gbl.biz.bizenum.PathLineStyleType;
import com.autonavi.gbl.biz.bizenum.RouteCompareTipsType;
import com.autonavi.gbl.biz.bizenum.RouteOverlayElem;
import com.autonavi.gbl.biz.bizenum.BizPointExtraDataType;
import com.autonavi.gbl.biz.model.BizBundle;
import com.autonavi.gbl.biz.model.BizBundleTag;
import com.autonavi.gbl.biz.model.BizCallbackData;
import com.autonavi.gbl.biz.model.BizLineMarker;
import com.autonavi.gbl.biz.model.BizPointBaseData;
import com.autonavi.gbl.biz.model.BizPointExMarker;
import com.autonavi.gbl.biz.model.BizPointExtraDataInfo;
import com.autonavi.gbl.biz.model.BizPointMarker;
import com.autonavi.gbl.biz.model.BizPolygonMarker;
import com.autonavi.gbl.biz.model.CarAnimationStyle;
import com.autonavi.gbl.biz.model.CarMarkStyle;
import com.autonavi.gbl.biz.model.CrossVectorMarker;
import com.autonavi.gbl.biz.model.RouteArrowStyle;
import com.autonavi.gbl.biz.observer.IMapStyleReader;
import com.autonavi.gbl.common.model.RectInt32;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.gloverlay.GLLineItemType;
import com.autonavi.gbl.map.gloverlay.MapRGBA;
import com.autonavi.gbl.map.gloverlay.RealCityFlyLineParam;
import com.autonavi.gbl.map.gloverlay.RealCityNaviTextures;
import com.autonavi.gbl.map.gloverlay.RouteOverlayParam;
import com.autonavi.gbl.map.gloverlay.VectorCrossAttr;

import java.util.ArrayList;

import static com.autonavi.gbl.biz.bizenum.AutoOverlayType.RouteOverlayRestrict;
import static com.autonavi.gbl.biz.model.BizBundleTag.GUIDE_ALTER_PATH_LABEL_TIME_DIFF;


/**
 * Created by zed.qzq on 2017/10/23.
 */

public class MapStyleReader implements IMapStyleReader {
    private final static String TAG = "MapStyleReader";
    private Context mContext;
    private GLMapView mMapView;
    private int mEngineId;
    private boolean nightMode;
    /*
   SearchOverlayRoot //父点
   SearchOverlayChildren //子节点
   SearchOverlayCentralPos //POI中心点
   SearchOverlayExitEntrance //POI出入口
   SearchOverlayBeginEnd //POI起终点
   GuideOverlayTypeAlongSearch 沿途搜点
   GuideOverlayTypeParking 停车场
 */
    private int lineType = -999;

    /**
     * 连接终点线的颜色
     */
    private final static int ENDLINECOLOR = 0xddfc3e39;
    /**
     * 连接终点线的宽度
     */
    private final static int ENDLINEWIDTH = 4;

    /**
     * @param mContext Activity
     * @param mMapView 地图
     * @param engineId 地图的Id
     */
    public MapStyleReader(Context mContext, GLMapView mMapView, int engineId) {
        MarkUtils.createRouteMarker(mContext, mMapView);//加载纹理
        this.mContext = mContext;
        this.mMapView = mMapView;
        this.mEngineId = engineId;
    }

    public MapStyleReader(Context mContext, GLMapView mMapView) {
        MarkUtils.createRouteMarker(mContext, mMapView);//加载纹理
        this.mContext = mContext;
        this.mMapView = mMapView;
        this.mEngineId = 1;
    }


    public void setLineType(int lineType) {
        this.lineType = lineType;
    }

    /**
     * 实景图车标
     *
     * @param flylineParam
     * @param flyNaviTextures
     */
    @Override
    public void get3DCrossAttrMark(RealCityFlyLineParam flylineParam, RealCityNaviTextures flyNaviTextures) {
        flylineParam.flylineTexInfo.x1 = 0.0f;
        flylineParam.flylineTexInfo.y1 = 0.25f;
        flylineParam.flylineTexInfo.x2 = 1.0f;
        flylineParam.flylineTexInfo.y2 = 0.25f;
        flylineParam.flylineTexInfo.textureLen = 256;

        flylineParam.flylineArrowTexInfo.x1 = 1.0f;
        flylineParam.flylineArrowTexInfo.y1 = 1.0f;
        flylineParam.flylineArrowTexInfo.x2 = 0.0f;
        flylineParam.flylineArrowTexInfo.y2 = 0.0f;
        flylineParam.flylineArrowTexInfo.textureLen = 256;
        flylineParam.sideWidth = 2;//ResUtil.dipToPixel(CC.getApplication(), 9);
        flylineParam.width = 7;//ResUtil.dipToPixel(CC.getApplication(), 22);
        flyNaviTextures.carResID = MarkUtils.MARKER_ID_CAR; //车标
        flyNaviTextures.compassResID = MarkUtils.MARKER_ID_CAR_DIRECTION; //罗盘
        flyNaviTextures.shineResID = MarkUtils.MARKER_ID_CAR_DIRECTION; //闪烁
    }

    NaviRouteOverlayParam naviRouteOverlayParam = NaviRouteOverlayParam.getInstance();

    /**
     * 函数名            SetNightMode
     *
     * @return void
     * @brief 设置夜间模式
     * @param[in] isNightMode  Gtrue 夜间， Gfalse:白天
     */
    @Override
    public void setNightMode(boolean isNightMode) {
        Log.i(TAG, "setNightMode: isNightMode = " + isNightMode);
        nightMode = isNightMode;
        naviRouteOverlayParam.isNightMode = isNightMode;
    }

    /**
     * @return true HMI填充纹理成功; false HMI填充纹理失败。
     * @brief 获取点的纹理资源
     * @param[in] param 需要给HMI识别的参数
     * @param[in] extraParam 一些复杂类型的扩展数据，根据业务场景进行获取, 其中key参见：MarkerExtraBizBundleName
     * @param[out] baseMarker， HMI填充后的基本纹理
     * @param[out] exMarker， HMI填充后的扩展纹理，根据业务场景需要.基本没用到
     * @note 当param.type == CruiseOverlayTypeLaneLinePoint, 传入是巡航车道线扎点图层,无扩展参数
     * 当param.type == GuideOverlayTypeCamera时，传入的是导航电子狗图层,directionStyle有效,会有扩展参数:Speed、CameraType
     * 当param.type == GuideOverlayTypePathBoard时，传入的是导航的路径路牌图层,directionStyle有效,会有扩展参数:RouteBoardName
     * 当param.type == GuideOverlayTypeCongestion时，传入的是导航的拥堵路牌图层,directionStyle有效,会有扩展参数:CongestionStatus、LayerTag
     * 当param.type == GuideOverlayTypeMixForkInfo, 传入是导航分歧路口图层,directionStyle参数有效,无扩展参数
     * 当param.type == , 传入是导航路径路牌图层,无扩展参数
     * 当param.type == RouteOverlayPlan,RouteOverlayCruise, RouteOverlayGuide,,传入是路线上的相关元素图层,index下标有效,包含扩展参数
     * 扩展参数包含:RouteOverlayElem, 可选值:RouteOverlayElemStartPoint,RouteOverlayElemEndPoint,RouteOverlayElemViasPoint,RouteOverlayElemCameras,RouteOverlayElemTrafficLights,RouteOverlayElemTrafficEvent
     * 扩展参数包含:RouteOverlayViasSize, 可选值: 途径点个数
     * 当param.type == RouteOverlayCompare, 传入是对比路线对比信息图层,(原路线,更快路线),有扩展参数:RouteOverlayElem,值为RouteOverlayElemCompareTips; RouteCompareTipsType(int32_t,参见枚举:RouteCompareTipsType), RouteCompareTipsName(String)
     * 当param.type == GuideOverlayTypeBuilding时，传入的地标建筑图层,directionStyle有效,会有扩展参数:RouteBoardName
     * 当param.type == GuideOverlayTypeTrafficEvent时，传入的是导航上的交通事件,directionStyle有效,会有扩展参数:LayerTag、Lane、isPreView
     */
    @Override
    public boolean getBizPointOverlayMarker(BizCallbackData param, BizBundle extraParam, BizPointMarker baseMarker, BizPointExMarker exMarker) {
        Log.i(TAG, "getBizPointOverlayMarker: param = " + param.type);
        switch (param.type) {
            case AutoOverlayType.SearchOverlayStart:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;
            case AutoOverlayType.SearchOverlayRoot:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;
            case AutoOverlayType.SearchOverlayChildren:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_2_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_2_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;
            case AutoOverlayType.SearchOverlayCentralPos:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_3_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_3_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;
            case AutoOverlayType.SearchOverlayExitEntrance:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_4_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_4_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;
            case AutoOverlayType.SearchOverlayBeginEnd:
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_5_USUAL; //普通状态下点纹理,-1表示无效值
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_5_FOCUS; //高亮状态下点纹理,-1表示无效值
                return true;

            case AutoOverlayType.GuideOverlayTypeCamera:  //电子狗
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_EDOG;
            //todo zjz
                //路径上电子眼大图标 左右飘6种电子眼
                return true;

            case AutoOverlayType.GuideOverlayTypeBywayName://旁路
            case AutoOverlayType.GuideOverlayTypePathBoard: //路径路牌图层
                String routeName = "路径路牌";
                MarkUtils.createMakerByText(mContext, mMapView, routeName, 0, MarkUtils.MARKER_ID_ROADNAME_LEFT_DAY);
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_ROADNAME_LEFT_DAY;  //路牌
                return true;

            case AutoOverlayType.GuideOverlayTypeMixForkInfo://分歧路
                Log.i(TAG, "GuideOverlayTypeMixForkInfo: 分歧路");
                StringBuffer mix = new StringBuffer();
                if (param.directionStyle == 1) {
                    mix.append("左飘,");
                } else if (param.directionStyle == 2) {
                    mix.append("右飘,");
                } else {
                    mix.append("未知方向,");
                }
                mix.append("index=" + param.index);
                MarkUtils.createMakerByText(mContext, mMapView, mix.toString(), 0, MarkUtils.MARKER_ID_MIX_ROAD);
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_MIX_ROAD;  //路牌
                return true;

            //如果是路径图层
            case AutoOverlayType.RouteOverlayCruise:
            case AutoOverlayType.RouteOverlayGuide:
            case AutoOverlayType.RouteOverlayPlan:
                if (extraParam != null) {
                    int value = extraParam.getInt32(BizBundleTag.ROUTE_OVERLY_ELEM_TYPE_TAG, 0);
                    //取得路径元素的类型
                    switch (value) {
                        //路经相关
                        case RouteOverlayElem.RouteOverlayElemStartPoint:
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_BUBBLE_START;  //起点
                            break;

                        case RouteOverlayElem.RouteOverlayElemEndPoint:  //终点
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_BUBBLE_END;
                            break;

                        case RouteOverlayElem.RouteOverlayElemVias:
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_VIA; //途经点
                            break;

                        case RouteOverlayElem.RouteOverlayElemCameras:
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_CAMERA; //电子眼
                            //todo zjz
                            //导航路径上纹理
                            break;

                        case RouteOverlayElem.RouteOverlayElemTrafficLights:
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_EDOG_TRAFFIC; //交通灯
                            //todo zjz
                            //导航路径上纹理
                            break;

                        case RouteOverlayElem.RouteOverlayElemCompareTips://路线上的tipsverlay
                            int tipsType = extraParam.getInt32(BizBundleTag.ROUTE_COMPARE_TPIS_TYPE_TAG, -1);
                            if (tipsType == RouteCompareTipsType.RouteTipsTypeOldRoute) {
                                MarkUtils.createMakerByText(mContext, mMapView, "旧路线", 0, MarkUtils.MARKER_ID_OLD_ROAD);
                                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_OLD_ROAD;  //路牌
                            } else if (tipsType == RouteCompareTipsType.RouteTipsTypeNewRoute) {
                                MarkUtils.createMakerByText(mContext, mMapView, "新路线", 0, MarkUtils.MARKER_ID_NEW_ROAD);
                                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_NEW_ROAD;  //路牌
                            }
                            break;
                        case RouteOverlayElem.RouteOverlayElemTrafficEvent: //路线上交通事件的overlay
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_TRAFFIC_EVENT;
                            break;

                        case RouteOverlayElem.RouteOverlayElemTrafficBlock: //路径上/外的封路事件
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_BLOCK_EVENT;
                            break;
                        case RouteOverlayElem.RouteOverlayElemAvoidJamPoint:  //躲避掉的拥堵路线overlay
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_JAM_EVENT;
                            break;

                        default:
                            break;

                    }
                    return true;


                } else {
                    return false;
                }

            case AutoOverlayType.GuideOverlayTypeBuilding://地标建筑图层
                //建筑物扎点，此处HMI需要根据建筑名动态合成纹理
                if (extraParam != null) {
                    String build = extraParam.getString16(BizBundleTag.GUIDE_ROUTE_BOARD_NAME, "");
                    MarkUtils.createMakerByText(mContext, mMapView, build, 0, MarkUtils.MARKER_ID_BUILDING_MARKER);
                    baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_BUILDING_MARKER;  //地标建筑
                }

                return true;

            case AutoOverlayType.GuideOverlayTypeParking://停车场
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_PARKING;
                return true;

            case AutoOverlayType.EndareaOverlayPolygon://终点的主点、子点  //TODO
                if (extraParam != null) {
                    int value = extraParam.getInt32(BizBundleTag.POLYGON_POINT_TYPE, 0);
                    switch (value) {
                        case 0:  //主点
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_USUAL;  //红点
                            break;
                        case 1:  //子点
                            baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_SEARCH_POI_1_FOCUS; //蓝点
                            break;
                    }
                }
                return true;

            case AutoOverlayType.GuideOverlayTypeIntervalCamera:  //区间限速摄像头
                if (extraParam != null) {
                    int type = extraParam.getInt32(BizBundleTag.GUIDE_CAMERA_TYPE, 0);  //起点终点类型
                    if (type == 8) { //起点
                        baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_TEST_V;
                    } else if (type == 9) {  //终点
                        baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_TEST_V;
                    }
                }
                return true;

            case AutoOverlayType.GuideOverlayTypeAlternativePathLabel:  //多备选路径图层
                Log.i(TAG, "getBizPointOverlayMarker: 多备选标签");
                String value1 = extraParam.getString16(GUIDE_ALTER_PATH_LABEL_TIME_DIFF, "");
                //收费信息
                String value2 = extraParam.getString16(BizBundleTag.GUIDE_ALTER_PATH_LABEL_COST, "");
                int labelIndex = extraParam.getInt32(BizBundleTag.GUIDE_ALTER_PATH_LABEL_INDEX, -1);
                Log.i(TAG, "getBizPointOverlayMarker: value1 = " + value1);
                Log.i(TAG, "getBizPointOverlayMarker: value2 = " + value2);
                Log.i(TAG, "getBizPointOverlayMarker: labelIndex = " + labelIndex);

                MarkUtils.createMakerByText(mContext, mMapView, value1, 0, MarkUtils.MARKER_ID_TIE);
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_TIE;  //多备选标签
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_TIE;  //多备选标签
                return true;

            case AutoOverlayType.GuideOverlayTypeTrafficEvent://导航中的交通事件点 1
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_GUIDE_EVENT;  //多备选标签
                return true;
            case AutoOverlayType.GuideOverlayTypeCongestion://拥堵时间气泡 1
                String name = extraParam.getString16(BizBundleTag.GUIDE_ROUTE_BOARD_NAME, "");
                int congestionStatus = extraParam.getInt32(BizBundleTag.GUIDE_CONGESTION_STATUS, -1);
                switch (congestionStatus) {
                    case 0:
                        name = name + "未知";
                        break;

                    case 1:
                        name = name + "通畅";
                        break;


                    case 2:
                        name = name + "缓行";
                        break;

                    case 3:
                        name = name + "阻塞";
                        break;

                    case 4:
                        name = name + "严重阻塞";
                        break;

                    case 5:
                        name = name + "结尾状态";
                        break;
                }

                MarkUtils.createMakerByText(mContext, mMapView, name, 0, MarkUtils.MARKER_ID_YONG_DU);
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_YONG_DU;

                return true;

            case AutoOverlayType.GuideOverlayTypeAlongSearch:  //沿途搜
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_NAVI_ALONG;  //沿途搜
                return true;

            case AutoOverlayType.PopOverlayRouteTrafficEvent:  //路径上弹出交通事件
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_POP_EVENT;  //路径上弹出交通事件
                return true;


            case AutoOverlayType.CruiseOverlayTypeLaneLinePoint:  //车道线扎点图标
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_CRUISE_LANE;  //车道线扎点图标
                return true;

            case AutoOverlayType.RouteOverlayCompare:
                int tipsType = extraParam.getInt32(BizBundleTag.ROUTE_COMPARE_TPIS_TYPE_TAG, -1);
                switch (tipsType) {
                    case RouteCompareTipsType.RouteTipsTypeOldRoute:
                        MarkUtils.createMakerByText(mContext, mMapView, "原路线", 0, MarkUtils.MARKER_ID_NORMAL_ROAD);
                        baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_NORMAL_ROAD;
                        return true;
                    case RouteCompareTipsType.RouteTipsTypeNewRoute:
                        MarkUtils.createMakerByText(mContext, mMapView, "更快路线", 0, MarkUtils.MARKER_ID_QUICK_ROAD);
                        baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_QUICK_ROAD;
                        return true;
                    default:
                        break;
                }

                return true;


            //20180316新增
            case AutoOverlayType.GuideOverlayTypeETAEvent://ETA事件
                MarkUtils.createMakerByText(mContext, mMapView, "ETA事件", 0, MarkUtils.MARKER_ID_ETA_EVENT);
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_ETA_EVENT;
                return true;

            case AutoOverlayType.AutoOverlayTypeDest://目的地图层
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_BUBBLE_END;
                baseMarker.mBgMarkerID = MarkUtils.MARKER_ID_BUBBLE_END;
                return true;
            case AutoOverlayType.OverlayTypeMotorCade://组队图层
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_POI_11_USUAL;
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_POI_11_FOCUS;
                baseMarker.mBgMarkerID = MarkUtils.MARKER_ID_MAP_STOP_EXIT_LINE;
                return true;
            case AutoOverlayType.FavoriteMain://收藏夹中主图查看模式
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_POI_8_USUAL;
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_POI_8_FOCUS;
                return true;
            case AutoOverlayType.FavoritePoi://收藏夹中poi查看模式
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_POI_9_USUAL;
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_POI_9_FOCUS;
                return true;
            case AutoOverlayType.OverlayTypeGpsPoints://•菜单需求中,gps轨迹点的图层
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_POI_10_USUAL;
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_POI_10_USUAL;
                return true;
            case AutoOverlayType.GuideOverlayTypeCustomizeCarTeam://new 车队头像
                baseMarker.mPoiMarkerID = MarkUtils.MARKER_ID_CAR_TEAM;
                baseMarker.mFocusMarkerID = MarkUtils.MARKER_ID_CAR_TEAM;
                return true;

            default:
                break;
        }
        return false;
    }


    @Override
    public void getCarAnimationStyle(@AutoCarStyleParam.AutoCarStyleParam1 int style, CarAnimationStyle carAnStyle) {
        carAnStyle.duration = 1200;
        carAnStyle.fromZoom = 0.8;
        carAnStyle.endZoom = 1.6;
        carAnStyle.fromAlpha = 0.61;
        carAnStyle.endAlpha = 0.35;
        carAnStyle.netfromZoom = 0.5;
        carAnStyle.netendZoom = 0.75;
    }

    /**
     * @return bool      true 填充纹理成功, false填充纹理失败
     * @brief 获取非通用点的信息
     * @param[in] dataInfo 额外的数据信息
     * @param[in/out] vecMarker  HMI填充的纹理信息
     * @note 当dataInfo.extraDataType == TrafficFacilityCameraType,表示传入的是巡航交通设施电子眼信息(例如:测速摄像头,监控摄像头,穿红灯,违章拍照,/公交,应急车道,非机动车道拍照,只在巡航模式生效，导航中不生效。),info.bizDataInfo.cruiseFacilityInfo 有效
     * 当dataInfo.extraDataType == TrafficEventInfoType,表示传入的巡航交通事件息(例如车祸,施工等),info.bizDataInfo.congestion有效
     * 当dataInfo.extraDataType == TrafficFacilityInfoType,表示传入的是巡航交通设施电子眼信息(例如前方左转,右转,落石等),info.bizDataInfo.cruiseFacilityInfo 有效
     */
    @Override
    public BizPointMarker[] getExtraPointMarker(BizPointExtraDataInfo dataInfo) {
        Log.i(TAG, "getExtraPointMarker: dataInfo = " + dataInfo.extraDataType);
        int length = dataInfo.bizDataInfo.cruiseFacilityInfo.length;
        BizPointMarker[] bizPointMarkers = new BizPointMarker[length];
        for (int i = 0; i < length; ++i) {
            bizPointMarkers[i] = new BizPointMarker();
        }
        // BL2.0 与向东确认后，巡航的样式Marker回调写在这里
        if (dataInfo.extraDataType == BizPointExtraDataType.CruiseTrafficEventInfoType) { //巡航交通事件类型
            //对应BL1.x的CruiseNoNaviCongestionEventOverlayItem
            for (int i = 0; i < length; ++i) {
                MarkUtils.createMakerByText(mContext, mMapView, "巡航交通事件", 0, MarkUtils.MARKER_ID_CRUISE_TRAFFIC);
                bizPointMarkers[i].mPoiMarkerID = MarkUtils.MARKER_ID_CRUISE_TRAFFIC;
            }
        } else if (dataInfo.extraDataType == BizPointExtraDataType.CruiseTrafficFacilityInfoType) { ///**< 巡航交通设施信息*/
            //对应BL1.x的CruiseRoadFacilityOverlayItem
            for (int i = 0; i < length; ++i) {
                MarkUtils.createMakerByText(mContext, mMapView, "巡航交通设施信息", 0, MarkUtils.MARKER_ID_CRUISE_FAC);
                bizPointMarkers[i].mPoiMarkerID = MarkUtils.MARKER_ID_CRUISE_FAC;
            }

        } else if (dataInfo.extraDataType == BizPointExtraDataType.CruiseTrafficFacilityCameraType) { //   /**< 巡航电子眼*/
            //对应BL1.x的CruiseCameraOverlayItem
            for (int i = 0; i < length; ++i) {
                MarkUtils.createMakerByText(mContext, mMapView, "巡航电子眼", 0, MarkUtils.MARKER_ID_CRUISE_EDOG);
                bizPointMarkers[i].mPoiMarkerID = MarkUtils.MARKER_ID_CRUISE_EDOG;
            }
        }
        return bizPointMarkers;
    }


    public static final int CONGESTION_STATUS_SLOW = 2;
    public static final int CONGESTION_STATUS_BAD = 3;
    public static final int CONGESTION_STATUS_DARK = 4;
    public static final int CONGESTION_STATUS_EXTREMEBLOCKED = 5;

    /**
     * @brief 获取非通用线的信息
     * @param[in] dataInfo
     * CongestionStatus 额外的数据信息 0:道路状态未知,1:道路通畅,2:道路缓行, 3:道路阻塞,4:严重拥堵,5:极度拥堵
     * @param[in/out] vecMarker  HMI填充的纹理信息
     * 当dataInfo.extraDataType == CruiseCongestionInfoType,表示传入的巡航拥堵信息,info.bizDataInfo.congestion有效
     */
    @Override
    public BizLineMarker[] getExtraLineMarker(BizPointExtraDataInfo dataInfo) {
        CommonUtil.showShortToast("拥堵路段回调");
//        CommonUtil.writeTxtToSDCard("拥堵纹理回传getExtraLineMarker", new Gson().toJson(dataInfo));
        BizLineMarker[] bizLineMarkers = null;
        if (dataInfo.extraDataType == BizPointExtraDataType.CruiseCongestionInfoType) {  //拥堵数据
            //对应BL1.x的GLLineOverlayItem
            int length = dataInfo.bizDataInfo.congestion.length;
            bizLineMarkers = new BizLineMarker[length];
            for (int i = 0; i < length; ++i) {
                bizLineMarkers[i] = new BizLineMarker();
//                bizLineMarkers[i].mLineWidth = 24;
                bizLineMarkers[i].mLineColor = (0xFFFFFFFF);
                int lineId = 0;
                switch (dataInfo.bizDataInfo.congestion[i].congestionStatus) {
                    case CONGESTION_STATUS_SLOW://道路缓行
                        bizLineMarkers[i].mLineWidth = 24;//越堵越粗
                        lineId = MarkUtils.MARKER_ID_MAP_SLOW;
                        break;
                    case CONGESTION_STATUS_BAD://拥堵路段
                        bizLineMarkers[i].mLineWidth = 28;
                        lineId = MarkUtils.MARKER_ID_LR_BAD;
                        break;
                    case CONGESTION_STATUS_DARK://严重拥堵
                        bizLineMarkers[i].mLineWidth = 32;
                        lineId = MarkUtils.MARKER_ID_MAP_DARK;
                        break;
                    case CONGESTION_STATUS_EXTREMEBLOCKED://极度拥堵
                        bizLineMarkers[i].mLineWidth = 36;//越堵越粗
                        lineId = MarkUtils.MARKER_ID_MAP_DARK;
                        break;
                    default:
                        lineId = MarkUtils.MARKER_ID_MAP_SLOW;
                        break;
                }

                bizLineMarkers[i].mFillLineMarker = lineId;
            }
        }
        return bizLineMarkers;
    }

    /**
     * @return true HMI填充纹理成功; false HMI填充纹理失败。
     * @brief 批量的获取点的纹理资源
     * @param[in] type 图层信息
     * @param[in/out] pointList 根据需要挑中对象中的纹理id
     * @note 当param.type == OverlayTypeMotorCade, 传入是车队图层
     */
    @Override
    public BizPointBaseData[] getBatchMarker(@AutoOverlayType.AutoOverlayType1 int type) {
        return new BizPointBaseData[0];
    }

    /**
     * @brief 获取矢量路口放大图属性
     * @param[in] naviType   0真实导航, 1模拟导航
     * @param[out] CrossVectorMaker  矢量路口放大图makerId
     * @param[out] VectorCrossAttr 矢量路口放大图属性
     */
    @Override
    public void getCrossVectorAttr(int naviType, CrossVectorMarker CrossMaker, VectorCrossAttr
            CrossAttr) {
        Log.i(TAG, "getCrossVectorAttr: navitype = " + naviType);
        CrossMaker.arrowResIdOuter = MarkUtils.MARKER_ID_ARROW_OUT;
        CrossMaker.arrowResIdInner = MarkUtils.MARKER_ID_ARROW_IN;
        CrossMaker.backgroundResId = MarkUtils.MARKER_ID_ZOOM_COR;
        CrossMaker.carResId = MarkUtils.MARKER_ID_CAR;
        CrossAttr.isDayMode = true;
        RectInt32 areaRect = new RectInt32();
        areaRect.left = 0;//rect坐标需上层传入，和屏幕密度以及横竖屏相关
        areaRect.top = 0;//rect坐标需上层传入，和屏幕密度以及横竖屏相关
        areaRect.right = 400;//rect坐标需上层传入，和屏幕密度以及横竖屏相关
        areaRect.bottom = 300;//rect坐标需上层传入，和屏幕密度以及横竖屏相关
        CrossAttr.areaRect = areaRect;
        /* 箭头边线宽度 */
        CrossAttr.arrowBorderWidth = 22;
        MapRGBA arrowBorderColor = new MapRGBA();
        arrowBorderColor.a = 50;
        arrowBorderColor.r = 0;
        arrowBorderColor.g = 50;
        arrowBorderColor.b = 20;
        /* 箭头边线颜色 */
        CrossAttr.arrowBorderColor = arrowBorderColor;
        /* 箭头内部宽度 */
        CrossAttr.arrowLineWidth = 28;
        MapRGBA arrowLineColor = new MapRGBA();
        arrowLineColor.a = (byte) 255;
        arrowLineColor.r = (byte) 255;
        arrowLineColor.g = (byte) 246;
        arrowLineColor.b = 0;
        /* 箭头内部颜色 */
        CrossAttr.arrowLineColor = arrowLineColor;
        MapRGBA areaColor = new MapRGBA();
        areaColor.a = (byte) 217;
        areaColor.r = 95;
        areaColor.g = 95;
        areaColor.b = 95;
        CrossAttr.areaColor = areaColor;

        //箭头纹理;
        //!< 箭头线冒纹理坐标 */
        CrossAttr.arrowlineCapTextureInfo.x1 = 0.23f;
        CrossAttr.arrowlineCapTextureInfo.x2 = 0.77f;
        CrossAttr.arrowlineCapTextureInfo.y1 = 0.77f;
        CrossAttr.arrowlineCapTextureInfo.y2 = 1.0f;

        //!< 箭头头部纹理坐标 */
        CrossAttr.arrowHeaderCapTextureInfo.x1 = 0.23f;
        CrossAttr.arrowHeaderCapTextureInfo.x2 = 0.77f;
        CrossAttr.arrowHeaderCapTextureInfo.y1 = 0.75f;
        CrossAttr.arrowHeaderCapTextureInfo.y2 = 0.0f;

        //!< 箭头纹理坐标 */
        CrossAttr.arrowlineTextureInfo.x1 = 0.23f;
        CrossAttr.arrowlineTextureInfo.x2 = 0.77f;
        CrossAttr.arrowlineTextureInfo.y1 = 0.77f;
        CrossAttr.arrowlineTextureInfo.y2 = 0.77f;
        CrossAttr.arrowlineTextureInfo.textureLen = 0.001f;
    }

    /**
     * @return 返回HMI构造的路线纹理属性
     * @brief 获取路线图层的属性
     * @param[in] overlyType 路口大图图层
     * @param[in] type 线的属性
     * @param[in/out] nPassedColor 矢量路口放大图属性, 经过路的颜色 第0个代表填充色，第1个代表边线颜色， 第2个代表鱼骨线颜色, 需要HMI填充
     * @param[in] extraParam 矢量路口放大图属性
     * @note 1, HMI可修改这个nPassedColor, 改变走过的路线颜色
     * 2,extraParam 暂时为空
     * 3,type 可选值:RouteOverlayPlan,RouteOverlayCruise,RouteOverlayGuide,RouteOverlayRestrict,RouteOverlayCompare,
     */
    @Override
    public boolean getBizRouteProperty(@AutoOverlayType.AutoOverlayType1 int overlayType,
                                       @PathLineStyleType.PathLineStyleType1 int type,
                                       ArrayList<RouteOverlayParam> list,
                                       int nPassedColor[],
                                       BizBundle extraPara) {
        if (extraPara != null) {
            int value = extraPara.getInt32(BizBundleTag.ROUTE_OVERLY_IS_MUTIL_ALTERNATIVE_MODE_TAG, -1);
            if (value == 1) {
                Log.i(TAG, "getBizRouteProperty: 路线是多备选");
            } else {
                Log.i(TAG, "getBizRouteProperty: 路线不是多备选");
            }

            int comparetype = extraPara.getInt32(BizBundleTag.ROUTE_COMPARE_TPIS_TYPE_TAG, -1);
            Log.i(TAG, "对比路线类型 comparetype = " + comparetype);

            String comparename = extraPara.getString16(BizBundleTag.ROUTE_COMPARE_TPIS_NAME_TAG, "");
            Log.i(TAG, "对比路线名字 comparename = " + comparename);
            list.addAll(naviRouteOverlayParam.getPropertys(mMapView, type, value == 1));
        }

        return true;
    }

    /**
     * @brief 从asset中读取资源
     * @param[in] resName  资源名称
     * @param[out] dataBuff  读取的资源二进制流
     * @param[out] dataLen  读取的资源的长度
     * @note 目前只有在设置3d路口大图横竖屏的时, 会去读取3dlandscape.xml或者3dportrait.xml, 设置给引擎
     */
    @Override
    public byte[] readAssets(String resName) {
        return new byte[0];
    }

    /**
     * @return true HMI填充纹理成功; false HMI填充纹理失败。
     * @brief 获取线相关的纹理
     * @param[in] param 需要给HMI识别的参数
     * @param[in] extraParam 一些复杂类型的扩展数据，根据业务场景进行获取, 其中key参见：MarkerExtraBizBundleName
     * @param[out] baseMarker， HMI填充后的基本纹理
     * @note 目前支持的类型有:
     * 当param.type == RouteOverlayRestrict, 传入是限行区域的图层,无扩展参数
     */
    @Override
    public boolean getBizLineOverlayMarker(BizCallbackData param, BizBundle extraParam, BizLineMarker baseMarker) {
        int overlayType = param.type;
        switch (overlayType) {
            case AutoOverlayType.RouteOverlayRestrict:
                baseMarker.mFillLineMarker = MarkUtils.MARKER_ID_MAP_LR;
                baseMarker.mLineWidth = 16;
                baseMarker.mBorderLineWidth = 16;
                baseMarker.mLineColor = 0XFFE53B00;
                baseMarker.mBorderLineColor = 0x26E53B00;
                baseMarker.lineItemType = GLLineItemType.TYPE_MARKER_LINE_COLOR;
                return true;
            case AutoOverlayType.RouteOverlayPlan:
                int elemType = extraParam.getInt32(BizBundleTag.ROUTE_OVERLY_ELEM_TYPE_TAG, -1);
                if (elemType == RouteOverlayElem.RouteOverlayElemAvoidJamLine) {
                    baseMarker.mFillLineMarker = MarkUtils.MARKER_ID_MAP_BAD;
                    baseMarker.mLineWidth = 12;
                    baseMarker.mBorderLineWidth = 0;
                    baseMarker.lineItemType = GLLineItemType.TYPE_MARKER_LINE;
                    return true;
                }
                break;

            case AutoOverlayType.SearchOverlayLine:
                Log.i(TAG, "getBizPolygonOverlayMarker: SearchOverlayLine");
                baseMarker.mLineColor = 0xd140a7ff;
                baseMarker.mFillLineMarker = MarkUtils.MARKER_ID_MAP_STOP_EXIT_LINE;
                baseMarker.mLineWidth = 10;
                //TODO

                baseMarker.lineItemType = lineType == -999 ? LineItemType.TypeMarkerLine : lineType;
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * @return true HMI填充纹理成功; false HMI填充纹理失败。
     * @brief 获取多边形的纹理
     * @param[in] param 需要给HMI识别的参数
     * @param[in] extraParam 一些复杂类型的扩展数据，根据业务场景进行获取, 其中key参见：MarkerExtraBizBundleName
     * @param[out] baseMarker， HMI填充后的基本纹理
     * @note 当param.type == RouteOverlayRestrict, 传入是限行区域的图层,无扩展参数
     * 当param.type == EndareaOverlayPolygon, 传入是终点区域的图层,无扩展参数
     */
    @Override
    public boolean getBizPolygonOverlayMarker(BizCallbackData param, BizBundle extraParam, BizPolygonMarker baseMarker) {

        switch (param.type) {
            case AutoOverlayType.SearchOverlayPolygon:
                baseMarker.lineColor = 0xd140a7ff;
                baseMarker.lineMarkId = MarkUtils.MARKER_ID_MAP_STOP_EXIT_LINE;
                baseMarker.lineWidth = 8;
                baseMarker.polygonColor = 0x26c4edfe;
                baseMarker.lineItemType = LineItemType.TypeMarkerLine;
                return true;

            case RouteOverlayRestrict:
                baseMarker.polygonColor = 0x26E53B00;
                baseMarker.lineColor = 0XFFE53B00;
                baseMarker.lineMarkId = MarkUtils.MARKER_ID_MAP_LR;
                baseMarker.lineWidth = 16;
                return true;

            case AutoOverlayType.SearchOverlayLine:
                baseMarker.lineColor = 0xd140a7ff;
                baseMarker.polygonColor = 0x26c4edfe;
                baseMarker.lineWidth = 8;
                baseMarker.lineMarkId = MarkUtils.MARKER_ID_MAP_LR;
                return true;

            case AutoOverlayType.EndareaOverlayPolygon:
                baseMarker.lineColor = 0xd140a7ff;
                baseMarker.polygonColor = 0x265d9bcf;
                baseMarker.lineMarkId = MarkUtils.MARKER_ID_MAP_LR;
                baseMarker.lineWidth = 8;
                return true;

            default:
                break;
        }
        return false;
    }

    /**
     * <
     *
     * @brief 获取转向箭头的纹理
     * @param[in] overlyType 图层类型,目前支持RouteOverlayPlan,RouteOverlayCruise,RouteOverlayGuide,RouteOverlayRestrict,RouteOverlayCompare
     * @param[out] routeStyle  转向箭头纹理信息
     * @param[in] extraParam  暂无
     * @note 前置条件: 需要先绘制相应的图层
     */
    @Override
    public void GetRouteArrowStyle(@AutoOverlayType.AutoOverlayType1 int overlayType,
                                   RouteArrowStyle routeStyle,
                                   BizBundle extraPara) {
        initRouteArrow(routeStyle);
    }

    /**
     * 函数名                GetCarMakerId
     *
     * @return void
     * @brief 获取车标的纹理信息
     * @param[in] mode 当前模式
     * @param[in] bGpsSuccess  gtrue：GPS信号正常 gfalse：gps信号不正常
     * @param[in/out] carStyle   车标makerId
     */
    @Override
    public void getCarMarkerId(@AutoCarStyleParam.AutoCarStyleParam1 int style,
                              boolean bGpsSuccess, CarMarkStyle carStyle) {
        Log.i(TAG, "getCarMakerId: style = " + style + ",carStyle = " + carStyle);
        //车标相关图层，其它属性也建议配置进去
        carStyle.carId = MarkUtils.MARKER_ID_CAR;
        carStyle.carDirId = MarkUtils.MARKER_ID_CAR_DIRECTION;
        carStyle.carFlashId = MarkUtils.MARKER_ID_FLASH;
        carStyle.carLineId = MarkUtils.MARKER_ID_MAP_LR;
        carStyle.endLineColor = ENDLINECOLOR;
        carStyle.endLineWidth = ENDLINEWIDTH;
        carStyle.eastResID = MarkUtils.MARKER_ID_EAST;
        carStyle.southResID = MarkUtils.MARKER_ID_SOUTH;
        carStyle.westResID = MarkUtils.MARKER_ID_WEST;
        carStyle.northResID = MarkUtils.MARKER_ID_NORTH;
        carStyle.relativeDistance = 65;
    }

    @Override
    public void getScaleCarStyle(@AutoCarStyleParam.AutoCarStyleParam1 int style,
                                 boolean bGpsSuccess, float fScale, CarMarkStyle carStyle ) {

    }

    private void initRouteArrow(RouteArrowStyle routeArrowStyle) {
        routeArrowStyle.mAutoZoomerWidth = false;

        routeArrowStyle.mLineWidth = 46; // 箭头宽度
        routeArrowStyle.mLineColor = Color.argb(0xff, 0xff, 0xff, 0xff);
        routeArrowStyle.mBorderLineColor = Color.argb(0xff, 0x0b, 0x6f, 0xc4);

        routeArrowStyle.mCapTexInfo.x1 = 0.12f;
        routeArrowStyle.mCapTexInfo.y1 = 0.85f;
        routeArrowStyle.mCapTexInfo.x2 = 0.88f;
        routeArrowStyle.mCapTexInfo.y2 = 1.0f;

        routeArrowStyle.mHeaderTextuerInfo.x1 = 0.12f;
        routeArrowStyle.mHeaderTextuerInfo.y1 = 0.75f;
        routeArrowStyle.mHeaderTextuerInfo.x2 = 0.88f;
        routeArrowStyle.mHeaderTextuerInfo.y2 = 0.0f;

        routeArrowStyle.mTextureInfo.x1 = 0.12f;
        routeArrowStyle.mTextureInfo.y1 = 0.77f;
        routeArrowStyle.mTextureInfo.x2 = 0.88f;
        routeArrowStyle.mTextureInfo.y2 = 0.77f;
        routeArrowStyle.mTextureInfo.textureLen = 0.00f;

        //三维设置
        routeArrowStyle.mSolidResMarker = MarkUtils.MARKER_ID_ARROW;
        routeArrowStyle.mSolidShadowColor = Color.argb(0x99, 0x00, 0x00, 0x00);
        routeArrowStyle.mSolidSideFaceColor = Color.argb(0xff, 0x33, 0x81, 0xe2);
        routeArrowStyle.mSolidTopFaceColor = Color.argb(0xff, 0xff, 0xff, 0xff);
        routeArrowStyle.mSolidHeight = 3;
        routeArrowStyle.mSolidThickness = 10;
        routeArrowStyle.mSolidBorderLineTopLineWidth = 2;
        routeArrowStyle.mSolidBorderLineSideLineWidth = 2;
        routeArrowStyle.mSolidBorderLineBottomLineWidth = 2;
        routeArrowStyle.mSolidArrowHeaderParam.headerAngle = 80;
        routeArrowStyle.mSolidArrowHeaderParam.headerWidthRate = 3;

        routeArrowStyle.mSolidCapTextureInfo.x1 = 0.35f;
        routeArrowStyle.mSolidCapTextureInfo.y1 = 0.75f;
        routeArrowStyle.mSolidCapTextureInfo.x2 = 0.65f;
        routeArrowStyle.mSolidCapTextureInfo.y2 = 0.82f;

        routeArrowStyle.mSolidHeaderTextuerInfo.x1 = 0.1f;
        routeArrowStyle.mSolidHeaderTextuerInfo.y1 = 0.75f;
        routeArrowStyle.mSolidHeaderTextuerInfo.x2 = 0.9f;
        routeArrowStyle.mSolidHeaderTextuerInfo.y2 = 0.1f;

        routeArrowStyle.mSolidTextureInfo.x1 = 0.35f;
        routeArrowStyle.mSolidTextureInfo.y1 = 0.625f;
        routeArrowStyle.mSolidTextureInfo.x2 = 0.65f;
        routeArrowStyle.mSolidTextureInfo.y2 = 0.625f;
        routeArrowStyle.mNormalMarker.fillLineMarker = MarkUtils.MARKER_ID_ARROW_IN;
        routeArrowStyle.mNormalMarker.borderLineMarker = MarkUtils.MARKER_ID_ARROW_OUT;
    }

}
