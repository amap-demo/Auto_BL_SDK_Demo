package com.amap.gbl.sdkdemo.model;

import com.amap.gbl.sdkdemo.platform.CommonUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.gbl.sdkdemo.R;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.gloverlay.GLLineItemType;
import com.autonavi.gbl.map.gloverlay.GLMarker;
import com.autonavi.gbl.map.gloverlay.GLTextureProperty;


/**
 * Created by zed.qzq on 2017/11/1.
 */

public class MarkUtils {
    private static final String TAG = "MarkUtils";

    public final static int MARKER_ID_BUBBLE_START = 1003;//起点
    public final static int MARKER_ID_BUBBLE_END = 1004;//终点
    public final static int MARKER_ID_MAP_LR_FEEROAD = 1013;
    public final static int MARKER_ID_MAP_STOP_EXIT_LINE = 1014;
    public final static int MARKER_ID_SEARCH_POI_1_USUAL = 1017;
    public final static int MARKER_ID_SEARCH_POI_1_FOCUS = 1018;
    public final static int MARKER_ID_SEARCH_POI_2_USUAL = 1019;
    public final static int MARKER_ID_SEARCH_POI_2_FOCUS = 1020;
    public final static int MARKER_ID_SEARCH_POI_3_USUAL = 11021;
    public final static int MARKER_ID_SEARCH_POI_3_FOCUS = 11022;
    public final static int MARKER_ID_SEARCH_POI_4_USUAL = 11023;
    public final static int MARKER_ID_SEARCH_POI_4_FOCUS = 11024;
    public final static int MARKER_ID_SEARCH_POI_5_USUAL = 11025;
    public final static int MARKER_ID_SEARCH_POI_5_FOCUS = 11026;
    public final static int MARKER_ID_SEARCH_POI_6_USUAL = 11027;
    public final static int MARKER_ID_SEARCH_POI_6_FOCUS = 11028;
    public final static int MARKER_ID_SEARCH_POI_7_USUAL = 11029;
    public final static int MARKER_ID_SEARCH_POI_7_FOCUS = 11030;

    public final static int MARKER_ID_CAR = 1021;
    public final static int MARKER_ID_USER = 1022;  //用户星星

    public final static int MARKER_ID_EDOG = 1023;  //电子狗

    public final static int MARKER_ID_CAR_DIRECTION = 1024;//车标外围圆

    public final static int MARKER_ID_ROADNAME_LEFT_DAY = 1025;  //车道路牌
    public final static int MARKER_ID_NEW_ROAD = 1026;//新路线
    public final static int MARKER_ID_OLD_ROAD = 1002;//新路线

    public final static int MARKER_ID_YONG_DU = 1027; //拥堵路牌
    public final static int MARKER_ID_CRUISE_LINE_POINT = 1028; //蓝色的点
    public final static int MARKER_ID_VIA = 1029; //途经点
    public final static int MARKER_ID_CAMERA = 1030; //电子眼
    public final static int MARKER_ID_MAP_TRAFFIC = 1031; //交通
    public final static int MARKER_ID_TEST_V = 1015; //区间测速
    public final static int MARKER_ID_MIX_ROAD = 1016; //分歧路

    public final static int MARKER_ID_BUILDING_MARKER = 1034; //地标建筑图层
    public final static int MARKER_ID_ARROW_IN = 1035; //箭头
    public final static int MARKER_ID_ARROW_OUT = 1036; //转向箭头
    public final static int MARKER_ID_ROAD_DASH = 1037; //转向箭头

    public final static int MARKER_ID_HAWKEYE_CAR = 1038; //鹰眼图
    public final static int MARKER_ID_HAWKEYE_START = 1039;
    public final static int MARKER_ID_HAWKEYE_END = 1040;

    public final static int MARKER_ID_MAP_BAD = 1041;
    public final static int MARKER_ID_MAP_SLOW = 1042;
    public final static int MARKER_ID_MAP_DARK = 1043;
    public final static int MARKER_ID_MAP_LR = 1044;

    public final static int MARKER_ID_NAVI_ALONG = 1045; //加油站

    public final static int MARKER_ID_LR_BAD = 1046; //红色纹理
    public final static int MARKER_ID_PARKING = 1047; //停车场
    public final static int MARKER_ID_TIE = 1050; //标签
    public final static int MARKER_ID_ZOOM_COR = 1051; //放大路口

    public final static int MARKER_ID_EDOG_TRAFFIC = 1052;

    public final static int MARKER_ID_MAP_AOLR = 1053;
    public final static int MARKER_ID_DOT_CAR = 1054;

    public final static int MARKER_ID_TRAFFIC_EVENT = 1055;//交通事件
    public final static int MARKER_ID_BLOCK_EVENT = 1056;//封路事件
    public final static int MARKER_ID_JAM_EVENT = 1057;//躲避掉的拥堵事件
    public final static int MARKER_ID_GUIDE_EVENT = 1058;//导航交通事件
    public final static int MARKER_ID_POP_EVENT = 1059;//路径上弹出的overlay
    public final static int MARKER_ID_CAR_TEAM = 1060;//车队
    public final static int MARKER_ID_NORMAL_ROAD = 1061;//旧路线
    public final static int MARKER_ID_QUICK_ROAD = 1062;//更快路线
    public final static int MARKER_ID_ETA_EVENT = 1063;//ETA
    public final static int MARKER_ID_CRUISE_LANE = 1064;//车道线
    public final static int MARKER_ID_CRUISE_TRAFFIC = 1065;//巡航交通事件类型
    public final static int MARKER_ID_CRUISE_FAC = 1066;////**< 巡航交通设施信息*/
    public final static int MARKER_ID_CRUISE_EDOG = 1067;// /**< 巡航电子眼*/
    public final static int MARKER_ID_ARROW = 1068;// 3D箭头
    public final static int MARKER_ID_POI_8_USUAL = 1069;//收藏夹看图
    public final static int MARKER_ID_POI_8_FOCUS = 1070;
    public final static int MARKER_ID_POI_9_USUAL = 1071;//收藏夹POI查看模式
    public final static int MARKER_ID_POI_9_FOCUS = 1072;
    public final static int MARKER_ID_POI_10_USUAL = 1073;//GPS轨迹点图层
    public final static int MARKER_ID_POI_10_FOCUS = 1074;
    public final static int MARKER_ID_POI_11_USUAL = 1075;//组队图层
    public final static int MARKER_ID_POI_11_FOCUS = 1076;

    public final static int MARKER_ID_MOVE = 10006;
    public final static int MARKER_ID_END = 10007;
    public final static int MARKER_ID_POIC = 10008;
    public final static int MARKER_ID_TRAFFICC = 10009;

    public final static int MARKER_ID_EAST = 20000;
    public final static int MARKER_ID_SOUTH = 20001;
    public final static int MARKER_ID_WEST = 20002;
    public final static int MARKER_ID_NORTH = 20003;
    public final static int MARKER_ID_FLASH = 20004;

    public static void createRouteMarker(Context context, GLMapView mapView) {
        addOverlayTexture(context, mapView, MARKER_ID_VIA, R.drawable.b_poi);
        //车标、起终点走鹰眼图的适配
        addOverlayTexture(mapView, MARKER_ID_CAR, R.drawable.car);
        addOverlayTexture(mapView, MARKER_ID_BUBBLE_START, R.drawable.trace_bubble_start);
        addOverlayTexture(mapView, MARKER_ID_BUBBLE_END, R.drawable.trace_bubble_end);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_1_USUAL, R.drawable.b_poi_1);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_1_FOCUS, R.drawable.b_poi_1_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_2_USUAL, R.drawable.b_poi_2);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_2_FOCUS, R.drawable.b_poi_2_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_3_USUAL, R.drawable.b_poi_3);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_3_FOCUS, R.drawable.b_poi_3_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_4_USUAL, R.drawable.b_poi_4);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_4_FOCUS, R.drawable.b_poi_4_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_5_USUAL, R.drawable.b_poi_5);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_5_FOCUS, R.drawable.b_poi_5_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_6_USUAL, R.drawable.b_poi_6);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_6_FOCUS, R.drawable.b_poi_6_hl);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_7_USUAL, R.drawable.b_poi_7);
        addOverlayTexture(context, mapView, MARKER_ID_SEARCH_POI_7_FOCUS, R.drawable.b_poi_7_hl);
        addOverlayTexture(context, mapView, MARKER_ID_POI_8_USUAL, R.drawable.b_poi_8);
        addOverlayTexture(context, mapView, MARKER_ID_POI_8_FOCUS, R.drawable.b_poi_8_hl);
        addOverlayTexture(context, mapView, MARKER_ID_POI_9_USUAL, R.drawable.b_poi_9);
        addOverlayTexture(context, mapView, MARKER_ID_POI_9_FOCUS, R.drawable.b_poi_9_hl);
        addOverlayTexture(context, mapView, MARKER_ID_POI_10_USUAL, R.drawable.b_poi_10);
        addOverlayTexture(context, mapView, MARKER_ID_POI_10_FOCUS, R.drawable.b_poi_10_hl);
        //addOverlayTexture(context, mapView, MARKER_ID_POI_11_USUAL, R.drawable.b_poi_11);
        //addOverlayTexture(context, mapView, MARKER_ID_POI_11_FOCUS, R.drawable.b_poi_11_hl);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_STOP_EXIT_LINE, R.drawable.map_stop_exit_line);
        //addOverlayTexture(context, mapView, MARKER_ID_USER, R.drawable.favorite_layer);
        //addOverlayTexture(context, mapView, MARKER_ID_CAR_DIRECTION, R.drawable.navi_direction);
        addOverlayTexture(context, mapView, MARKER_ID_EAST, R.drawable.east);
        addOverlayTexture(context, mapView, MARKER_ID_SOUTH, R.drawable.south);
        addOverlayTexture(context, mapView, MARKER_ID_WEST, R.drawable.west);
        addOverlayTexture(context, mapView, MARKER_ID_NORTH, R.drawable.north);
        addOverlayTexture(context, mapView, MARKER_ID_FLASH, R.drawable.flash);
        addOverlayTexture(context, mapView, MARKER_ID_CRUISE_LINE_POINT, R.drawable.cruise_navi_line_point);
        addOverlayTexture(context, mapView, MARKER_ID_CAMERA, R.drawable.autonavi_camera_day);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_TRAFFIC, R.drawable.map_traffic);
        //addOverlayTexture(context, mapView, MARKER_ID_TEST_V, R.drawable.trace_bubble_fast);
        addOverlayTexture(context, mapView, MARKER_ID_ARROW_IN, R.drawable.arrow_line_inner);
        addOverlayTexture(context, mapView, MARKER_ID_ARROW_OUT, R.drawable.arrow_line_outer_in_cross);
        addOverlayTexture(context, mapView, MARKER_ID_ROAD_DASH, R.drawable.ic_cross_road_dash);
        addOverlayTexture(context, mapView, MARKER_ID_HAWKEYE_CAR, R.drawable.hawkeye_navi_car_circle);
        addOverlayTexture(context, mapView, MARKER_ID_HAWKEYE_START, R.drawable.hawkeye_start);
        addOverlayTexture(context, mapView, MARKER_ID_HAWKEYE_END, R.drawable.hawkeye_end);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_BAD, R.drawable.map_lr_bad);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_SLOW, R.drawable.map_lr_slow);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_DARK, R.drawable.map_lr_darkred);
        addOverlayTexture(context, mapView, MARKER_ID_MAP_LR, R.drawable.map_lr);
        addOverlayTexture(context, mapView, MARKER_ID_EDOG, R.drawable.edog_camera_left);
        addOverlayTexture(context, mapView, MARKER_ID_NAVI_ALONG, R.drawable.navi_along_search_gas_station_big_icon);
        addOverlayTexture(context, mapView, MARKER_ID_LR_BAD, R.drawable.map_lr_bad);
        //addOverlayTexture(context, mapView, MARKER_ID_PARKING, R.drawable.parking);
        addOverlayTexture(context, mapView, MARKER_ID_ZOOM_COR, R.drawable.external_cross_bg);  //放大路口-----虚化背景
        addOverlayTexture(context, mapView, MARKER_ID_EDOG_TRAFFIC, R.drawable.auto_ic_edog__traffic);  //交通灯
        addOverlayTexture(context, mapView, MARKER_ID_MAP_AOLR, R.drawable.map_aolr);
        addOverlayTexture(context, mapView, MARKER_ID_DOT_CAR, R.drawable.map_lr_dott_car);
        addOverlayTexture(context, mapView, MARKER_ID_TRAFFIC_EVENT, R.drawable.auto_traffic_report_accident);
        addOverlayTexture(context, mapView, MARKER_ID_BLOCK_EVENT, R.drawable.traffic_normal_block);
        addOverlayTexture(context, mapView, MARKER_ID_JAM_EVENT, R.drawable.auto_traffic_report_jam);
        addOverlayTexture(context, mapView, MARKER_ID_GUIDE_EVENT, R.drawable.traffic_control_close_local);
        addOverlayTexture(context, mapView, MARKER_ID_POP_EVENT, R.drawable.b_poi_tr);
        //addOverlayTexture(context, mapView, MARKER_ID_CAR_TEAM, R.drawable.brand_benz);
        addOverlayTexture(context, mapView, MARKER_ID_CRUISE_LANE, R.drawable.auto_map_ic_lanemarker);
        addOverlayTexture(context, mapView, MARKER_ID_ARROW, R.drawable.threed_arrow_line_shadow);
        addOverlayTexture(context, mapView, MARKER_ID_ETA_EVENT, R.drawable.auto_navi_eta_point_icon);
        //飞线模块
        addOverlayTexture(context, mapView, MARKER_ID_MOVE, R.drawable.ic_center); //移动过程中
        addOverlayTexture(context, mapView, MARKER_ID_END, R.drawable.ic_click_map_center); //移动结束
        addOverlayTexture(context, mapView, MARKER_ID_TRAFFICC, R.drawable.b_poi_tr); //交通事件点击
        addOverlayTexture(context, mapView, MARKER_ID_POIC, R.drawable.b_poi_hl);//普通overlay点击
    }

    private static void addOverlayTexture(Context context, GLMapView mapView, int makerId, int resId) {
        GLTextureProperty textureProperty = new GLTextureProperty();
        textureProperty.mAnchor = 4;
        textureProperty.mId = makerId;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 1;
        options.inTargetDensity = 1;
        textureProperty.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), resId, options);
        mapView.addOverlayTexture(1, textureProperty);
    }

    /**
     * 针对鹰眼图内相关图标做的适配
     * @param mapView
     * @param makerId
     * 仅包含车标、起、终点  MARKER_ID_CAR、MARKER_ID_BUBBLE_START、MARKER_ID_BUBBLE_END
     * @param resId
     */
    private static void addOverlayTexture(GLMapView mapView, int makerId, int resId) {
        GLTextureProperty textureProperty = new GLTextureProperty();
        textureProperty.mAnchor = 4;
        textureProperty.mId = makerId;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 480;
        //适配鹰眼图纹理填充，以满足低分辨率机型起终点及车位图标不会太大
        //当前标准dpi为480时显示效果正常，以480为基准进行放大缩小
        options.inTargetDensity = CommonUtil.getDensityDpi();
        Log.d(TAG, "addOverlayTexture: dpi = " + CommonUtil.getDensityDpi());
//        Log.d(TAG, "addOverlayTexture: 分辨率宽：" + ScreenUtils.getScreenWidth(context)
//                + ",高：" + ScreenUtils.getScreenHeight(context));
        textureProperty.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), resId, options);
        Log.d(TAG, "addOverlayTexture: textureProperty.mBitmap:width:"
                + textureProperty.mBitmap.getWidth() + ",textureProperty.mBitmap:height:"
                + textureProperty.mBitmap.getHeight());
        mapView.addOverlayTexture(1, textureProperty);
    }


    /**
     * 调用此方法后可通过ID进行调用
     *
     * @param context
     * @param mapView
     * @param makerId
     * @param resId
     */
    public static void addOverlayTexture(Context context, GLMapView mapView, int makerId, int resId, float mXRatio, float mYRatio) {
        GLTextureProperty textureProperty = new GLTextureProperty();
        textureProperty.mAnchor = 4;
        textureProperty.mId = makerId;
        textureProperty.mXRatio = mXRatio;
        textureProperty.mYRatio = mYRatio;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 1;
        options.inTargetDensity = 1;
        textureProperty.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), resId, options);
        mapView.addOverlayTexture(1, textureProperty);
    }


    /**
     * 根据文字，创建图片纹理
     *
     * @param context
     * @param mapView
     * @param bgResId
     * @param text
     * @param id
     */
    public static void createMakerByText(Context context, GLMapView mapView, String text, int bgResId, int id) {
        createMakerByText(context, mapView, text, bgResId, id, 1);

    }

    private static void createMakerByText(Context context, GLMapView mapView, String text, int bgResId, int id, int engineId) {
        View view = View.inflate(context, R.layout.layout_texture_text, null);
        TextView textView = (TextView) view.findViewById(R.id.tx_texture_id);
        textView.setText(text);

        textView.setBackgroundColor(context.getResources().getColor(R.color.auto_color_f36472));
        if (bgResId != 0) {

            Drawable drawable = context.getResources().getDrawable(bgResId);
            drawable.setBounds(0, 0, 32, 32);
            //在左边添加一个长宽为32的图片
            textView.setCompoundDrawables(drawable, null, null, null);
        }

        view.setDrawingCacheEnabled(true);


        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        GLTextureProperty glTextureProperty = new GLTextureProperty();
        glTextureProperty.mId = id;
        glTextureProperty.mAnchor = 9;
        Bitmap bitmap = null;
        try {

            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getDrawingCache(true));
            glTextureProperty.mBitmap = bitmap;
        } catch (Exception e) {

            Log.i(TAG,"createMakerByText" + text);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = 1;
            options.inTargetDensity = 1;
            glTextureProperty.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.bubble_sticker_danger_big, options);
        }

        glTextureProperty.mXRatio = 0.5f;
        glTextureProperty.mYRatio = 0.5f;
        mapView.addOverlayTexture(engineId, glTextureProperty);

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }


    /**
     * 生成Texture纹理，并设置相关属性
     *
     * @param type
     * @param resid
     */
    public static void createLineTexure(GLMapView mapView, int type, int resid) {
        GLTextureProperty property = new GLTextureProperty();
        property.mId = resid;

        if (resid != -1 && resid != GLMarker.GL_MARKER_NOT_SHOW) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = 1;
            options.inTargetDensity = 1;
            property.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), resid, options);
        }

        property.mAnchor = GLMarker.AG_ANCHOR_CENTER;

        setProperTextureProperty(type, property);
        mapView.addOverlayTexture(1, property);
    }

    /**
     * 设置纹理相关的属性类型
     *
     * @param itemType 纹理属性类型
     * @param property 需要赋值的属性对象
     */
    private static void setProperTextureProperty(int itemType, GLTextureProperty property) {
        if (property == null) {
            return;
        }

        switch (itemType) {
            case GLLineItemType.TYPE_MARKER_LINE_COLOR: {
                property.isGenMimps = true;
                property.isRepeat = false;
                break;
            }
            case GLLineItemType.TYPE_MARKER_LINE:
            case GLLineItemType.TYPE_MARKER_LINE_ARROW:
            case GLLineItemType.TYPE_MARKER_LINE_DOT:
            case GLLineItemType.TYPE_MARKER_LINE_FERRY: {
                property.isGenMimps = true;
                property.isRepeat = true;
                break;
            }

            case GLLineItemType.TYPE_MARKER_LINE_RESTRICT:
            case GLLineItemType.TYPE_MARKER_LINE_DOT_COLOR: {
                property.isGenMimps = false;
                property.isRepeat = true;
                break;
            }
            case GLMarker.GL_MARKER_LINE_USE_COLOR:
                //设置该值=true飞线不显示锯齿
                property.isGenMimps = true;
                break;
            default:
                break;
        }
    }

    /**
     * 飞线添加使用
     *
     * @param mapView
     * @param makerId
     * @param resId
     * @param anchor
     * @param xRatio
     * @param yRatio
     * @param genmimps
     * @param repeat
     */
    public static void addOverlayTexture(GLMapView mapView, int makerId, int resId,
                                         int anchor, float xRatio, float yRatio, boolean genmimps, boolean repeat) {
        GLTextureProperty textureProperty = new GLTextureProperty();
        textureProperty.mAnchor = anchor;
        textureProperty.isGenMimps = genmimps;
        textureProperty.isRepeat = repeat;
        textureProperty.mXRatio = xRatio;
        textureProperty.mYRatio = yRatio;
        textureProperty.mId = makerId;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 1;
        options.inTargetDensity = 1;
        textureProperty.mBitmap = BitmapFactory.decodeResource(mapView.getResources(), resId, options);
        mapView.addOverlayTexture(1, textureProperty);
    }
}
