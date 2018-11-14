package com.amap.gbl.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.aos.BLAosServiceManager;
import com.autonavi.gbl.aos.model.CEtaRequestReponseParam;
import com.autonavi.gbl.aos.model.GAddressPredictResponseParam;
import com.autonavi.gbl.aos.model.GCancelSignPayResponseParam;
import com.autonavi.gbl.aos.model.GCarLtdBindResponseParam;
import com.autonavi.gbl.aos.model.GCarLtdCheckTokenResponseParam;
import com.autonavi.gbl.aos.model.GCarLtdQuickLoginResponseParam;
import com.autonavi.gbl.aos.model.GCarLtdQuickRegisterResponseParam;
import com.autonavi.gbl.aos.model.GDriveReportSmsResponseParam;
import com.autonavi.gbl.aos.model.GDriveReportUploadResponseParam;
import com.autonavi.gbl.aos.model.GFeedbackReportResponseParam;
import com.autonavi.gbl.aos.model.GHolidayListResponseParam;
import com.autonavi.gbl.aos.model.GLogUploadResponseParam;
import com.autonavi.gbl.aos.model.GMojiWeatherResponseParam;
import com.autonavi.gbl.aos.model.GParkOrderCreateResponseParam;
import com.autonavi.gbl.aos.model.GParkOrderDetailResponseParam;
import com.autonavi.gbl.aos.model.GParkOrderListResponseParam;
import com.autonavi.gbl.aos.model.GParkPayStatusResponseParam;
import com.autonavi.gbl.aos.model.GParkServiceResponseParam;
import com.autonavi.gbl.aos.model.GPaymentBindAlipayResponseParam;
import com.autonavi.gbl.aos.model.GPaymentStatusResponseParam;
import com.autonavi.gbl.aos.model.GPaymentUnbindAlipayResponseParam;
import com.autonavi.gbl.aos.model.GQRCodeConfirmResponseParam;
import com.autonavi.gbl.aos.model.GQueryCarMsgResponseParam;
import com.autonavi.gbl.aos.model.GQueryPersentWeatherResponseParam;
import com.autonavi.gbl.aos.model.GQueryWeatherByLinkResponseParam;
import com.autonavi.gbl.aos.model.GReStrictedAreaResponseParam;
import com.autonavi.gbl.aos.model.GSendToPhoneResponseParam;
import com.autonavi.gbl.aos.model.GTrafficEventCommentResponseParam;
import com.autonavi.gbl.aos.model.GTrafficEventDetailRequestParam;
import com.autonavi.gbl.aos.model.GTrafficEventDetailResponseParam;
import com.autonavi.gbl.aos.model.GTrafficRestrictResponseParam;
import com.autonavi.gbl.aos.model.GUserDeviceResponseParam;
import com.autonavi.gbl.aos.model.GWorkdayListResponseParam;
import com.autonavi.gbl.aos.observer.BLAosResponseObserver;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.map.MapRoadTip;
import com.autonavi.gbl.map.MapviewModeParam;
import com.autonavi.gbl.map.ScenicInfo;
import com.autonavi.gbl.map.glinterface.GLGeoPoint;
import com.autonavi.gbl.map.glinterface.MapLabelItem;
import com.autonavi.gbl.map.listener.MapListener;
import com.autonavi.gbl.map.utils.GLMapStaticValue;
import com.autonavi.gbl.map.utils.GLMapUtil;
import com.autonavi.gbl.map.gloverlay.BaseMapOverlay;
import com.autonavi.gbl.map.gloverlay.GLOverlayBundle;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceDataPath;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;
import com.amap.gbl.sdkdemo.map.PointOverlay;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;
import android.widget.TextView;
import com.autonavi.gbl.utilcomponent.BLToolPoiID;

import static com.autonavi.gbl.map.GLMapView.MAPVIEW_STATE_NAVI_CAR;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MapListener {

    public static final String TAG = MainActivity.class.getSimpleName().toString();
    private TextView mTextView;
    private TextView tv_msg1;
    private TextView tv_msg2;


    private Button btn_show_road; //二维路网
    private Button btn_show_road_arrow; //二维道路箭头
    private Button btn_show_region;//二维背景面（绿地、水系等）
    private Button btn_show_build_model;//二维建筑物
    private Button btn_show_poi;//标注、poi
    private Button btn_show_simple3d_on;//简易三维
    private Button btn_day;// 夜晚模式
    private Button btn_traffic;//交通
    private Button btn_zoomin; //放大
    private Button btn_zoomout;//缩小
    private Button btn_mode;//车模式
    private Button btn_pointOverlay; //pointOverlay 扎点
    private Button btn_pointVisiable; //btn_pointVisiable  pointOverlay是否可见
    private Button btn_openlayeron; //开放图层
    //加载引擎
    static {
        System.loadLibrary("GPlatformInterface");
        System.loadLibrary("AutoCrypto");
        System.loadLibrary("AutoSSL");
        System.loadLibrary("GComm3rd");
        System.loadLibrary("GNet");
        System.loadLibrary("sync_cxx");
        System.loadLibrary("GNaviDice");
        System.loadLibrary("Gbl");//bl
    }

    private ServiceDataPath path;
    public final static String DATA_DIR_NAME = "sdkDemo3x";
    /**
     * 配置的路径
     */
    public final static String pcCfgFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + DATA_DIR_NAME;
    //rest平台
    RestPlatformInterface pPlatformUtil = new RestPlatformInterface();

    String strKey;
    String strSecurityCode;
    public GLMapView mapView;
    public GlMapSurface glMapSurface;
    private static int initFlg = 0;
    public int mMainEngineID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManagerService();
        BLAosServiceManager.getInstance().init(blAosResponseObserver);
        setContentView(R.layout.activity_main);
        initMap();
    }

    private void initMap() {
        mapView = new GLMapView(CC.getApplication());
        glMapSurface = (GlMapSurface) findViewById(R.id.mapview);
        glMapSurface.setGLMapView(mapView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;
        //创建地图  初始化地图
        mMainEngineID = mapView.createEngineWithFrame(mapView.mDefaultDeviceId, GLMapUtil.AMAP_ENGINE_TYPE_MAIN,
                new Rect(0, 0, mScreenWidth, mScreenHeight), mScreenWidth, mScreenHeight);
        Log.i(TAG, "initMap: mMainEngineID = " + mMainEngineID);
        mapView.setMapCenter(mMainEngineID, 221010004, 101712921);  //设置中心点为天安门
        mapView.setMapLevel(mMainEngineID, 15f);//默认缩放级别为15
        mapView.setMapListener(this);

        tv_msg1 = (TextView) findViewById(R.id.tv_msg1);
        tv_msg2 = (TextView) findViewById(R.id.tv_msg2);
        btn_show_road = (Button) findViewById(R.id.btn_show_road);
        btn_show_road.setOnClickListener(this);
        btn_show_road.setBackgroundColor(Color.TRANSPARENT);
        btn_show_road_arrow = (Button) findViewById(R.id.btn_show_road_arrow);
        btn_show_road_arrow.setOnClickListener(this);
        btn_show_road_arrow.setBackgroundColor(Color.TRANSPARENT);
        btn_show_region = (Button) findViewById(R.id.btn_show_region);
        btn_show_region.setOnClickListener(this);
        btn_show_region.setBackgroundColor(Color.TRANSPARENT);
        btn_show_build_model = (Button) findViewById(R.id.btn_show_build_model);
        btn_show_build_model.setOnClickListener(this);
        btn_show_build_model.setBackgroundColor(Color.TRANSPARENT);
        btn_show_poi = (Button) findViewById(R.id.btn_show_poi);
        btn_show_poi.setOnClickListener(this);
        btn_show_poi.setBackgroundColor(Color.TRANSPARENT);
        btn_show_simple3d_on = (Button) findViewById(R.id.btn_show_simple3d_on);
        btn_show_simple3d_on.setOnClickListener(this);
        btn_show_simple3d_on.setBackgroundColor(Color.TRANSPARENT);
        btn_day = (Button) findViewById(R.id.btn_day);
        btn_day.setOnClickListener(this);
        btn_day.setBackgroundColor(Color.TRANSPARENT);
        btn_traffic = (Button) findViewById(R.id.btn_traffic);
        btn_traffic.setOnClickListener(this);
        btn_traffic.setBackgroundColor(Color.TRANSPARENT);
        btn_zoomin = (Button) findViewById((R.id.btn_zoomin));
        btn_zoomin.setOnClickListener(this);
        btn_zoomin.setBackgroundColor(Color.TRANSPARENT);
        btn_zoomout = (Button) findViewById((R.id.btn_zoomout));
        btn_zoomout.setOnClickListener(this);
        btn_zoomout.setBackgroundColor(Color.TRANSPARENT);
        btn_mode = (Button) findViewById((R.id.btn_mode));
        btn_mode.setOnClickListener(this);
        btn_mode.setBackgroundColor(Color.TRANSPARENT);
        btn_pointOverlay = (Button) findViewById((R.id.btn_pointOverlay));
        btn_pointOverlay.setOnClickListener(this);
        btn_pointOverlay.setBackgroundColor(Color.TRANSPARENT);
        btn_pointVisiable = (Button) findViewById((R.id.btn_pointVisiable));
        btn_pointVisiable.setOnClickListener(this);
        btn_pointVisiable.setBackgroundColor(Color.TRANSPARENT);
        btn_openlayeron = (Button) findViewById(R.id.btn_openlayeron);
        btn_openlayeron.setOnClickListener(this);
        btn_openlayeron.setBackgroundColor(Color.TRANSPARENT);
        tv_msg1.setText(GLMapView.getMapEngineVersion(mMainEngineID) + "\r\n" + "BL：" + ServiceMgr.getVeison());  //获取地图引擎版本号
        Log.i(TAG, "地图引擎版本号  = " + GLMapView.getMapEngineVersion(mMainEngineID));
    }

    private int initManagerService() {

        String pcLogPath = pcCfgFilePath + "/bllog";//sdcard/sdkDemo3x/bllog
        Log.i(TAG, "BLPATH:pcCfgFilePath: " + pcCfgFilePath);
        Log.i(TAG, "BLPATH:pcLogPath: " + pcLogPath);
        File f = new File(pcLogPath);//sdcard/sdkDemo3x/bllog
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
            Log.i(TAG, "initMngService: mkdirs = " + mkdirs);
        }

        f = new File(pcCfgFilePath, "GNaviConfig.xml");//sdcard/sdkDemo3x/GNaviConfig.xml
        Context context = CC.getApplication();
        if (!f.exists()) {
            copyAssets(context, f, "GNaviConfig.xml");
        }

        f = new File(pcCfgFilePath, "all_city_compile.json");
        if (!f.exists()) {
            copyAssets(context, f, "all_city_compile.json");
        }

        f = new File(pcCfgFilePath, "res/global.db");
        if (!f.exists()) {
            copyAssets(context, f, "res/global.db");
        }

        f = new File(pcCfgFilePath, "res/bl_voice.db");
        if (!f.exists()) {
            copyAssets(context, f, "res/bl_voice.db");
        }

        f = new File(pcCfgFilePath, "GRestConfig.ini");
        if (!f.exists()) {
            copyAssets(CC.getApplication(), f, "GRestConfig.ini");
        }

        BaseInitParam param = new BaseInitParam();
        param.logPath = pcLogPath;  //日志路径下
        param.logLevel = ALCLogLevel.LogLevelVerbose; //打印全部日志
        param.groupMask = ALCGroup.GROUP_MASK_BL_AE;  //打印BL和引擎
        param.pPlatformUtil = pPlatformUtil;
        param.restConfigPath = pcCfgFilePath;
        //网络相关
        //公众正式网服务器类型;
        int publicServerType = ServiceManagerEnum.AosProductionEnv;
        //公众正式网服务器渠道密钥Key,需要向高德产品申请获取
        String publicServerKey          = "d854***d9799cb***56d80d2b***0c6a";
        //公众正式网服务器安全码，需要向高德产品申请获取
        String publicServerSecurityCode = "c60a***6685586***218a1edb***04b2";

        //测网联调服务器类型;
        int testServerType = ServiceManagerEnum.AosDevelopmentEnv;
        //测网联调服务器临时渠道密钥Key,需要向高德产品申请获取
        String testServerKey            = "d854***d9799cb***56d80d2b***0c6b";
        //测网联调服务器临时安全码，,需要向高德产品申请获取
        String testServerSecurityCode   = "c60a***6685586***218a1edb***04b1";
        param.serverType = testServerType;

        if (publicServerType == param.serverType)  /* 正网 */
        {
            param.restKey = publicServerKey;
            param.restSecurityCode = publicServerSecurityCode;
        }
        else if(param.serverType == testServerType) /* 测网 */
        {
            param.restKey = testServerKey;
            param.restSecurityCode = testServerSecurityCode    ;
        }

        param.aosDBPath = pcCfgFilePath;
        //调用此接口会打印ALC log、log_GNet.log、BL jni logcat
        int initBaseLibs = ServiceMgr.getServiceMgrInstance().initBaseLibs(param);
        //ALC log
        //log_GNet.log
        //BL jni logcat
        Log.i(TAG, "initBaseLibs = " + initBaseLibs);

        BLInitParam blInitParam = new BLInitParam();
        //配置文件路径
        blInitParam.dataPath.cfgFilePath = pcCfgFilePath;
        //配置云+端数据路径
        blInitParam.dataPath.onlinePath = pcCfgFilePath + "/online/";
        //离线地图
        blInitParam.dataPath.offlinePath = pcCfgFilePath + "/data/navi/compile_v2/chn/";
        //精品三维地图
        blInitParam.dataPath.off3DDataPath = pcCfgFilePath + "/data/navi/compile_v2/chn/";
        f = new File(blInitParam.dataPath.offlinePath);
        if (!f.exists()) {
            f.mkdirs();
        }

        int initBL = ServiceMgr.getServiceMgrInstance().initBL(blInitParam, CC.getApplication());
        Log.i(TAG, "initBL = " + initBL);
        return 1;
    }


    private boolean copyAssets(Context context, File dest, String assetFile) {
        if (context == null) {
            return false;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (!dest.exists()) {
                dest.getParentFile().mkdirs();
            }
            inputStream = context.getAssets().open(assetFile);
            outputStream = new FileOutputStream(dest);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                outputStream.write(buff, 0, len);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean bshow_road_arrow = false;
    private boolean bshow_region = false;
    private boolean bshow_build_model = false;
    private boolean bshow_poi = false;
    private boolean bshow_simple3d_on = false;
    private boolean bmapmode_night = false;
    private boolean bTrafficTMC = false;
    private int mode = -1;

    private boolean bPointOverlay = false;
    private boolean bPointOverlayVisiable = false;
    private PointOverlay pointOverlay = null;

    private int nOpenlayer =0;
    //交通事件开关:第一个官方路况事件，第二个是用户上报路况事件
    public static final String OPENDEFAULTLAYER_JESON = "{\"update_period\": 30,\"cachemode\":2," +
        " \"url\": \"http://maps.testing.amap.com/ws/mps/lyrdata/ugc\"," +
        "\"bounds\":[{\"y1\": 122421247,\"x2\": 235405312, \"x1\": 188874751, \"y2\": 85065727}], " +
        "\"sublyr\": [{\"type\": 4, \"sid\": 9000004, \"zlevel\": 2}], \"minzoom\": 6, \"maxzoom\": 20, \"id\": 9001}";

    //TODO 未来会从服务器获取，暂时采用硬编码
    public static final String OPENLAYER2_JESON = "{\"update_period\": 90, \"cachemode\":1, " +
        "\"url\": \"http://maps.testing.amap.com/ws/mps/lyrdata/ugc\"," +
        "\"bounds\":[{\"y1\": 122421247,\"x2\": 235405312, \"x1\": 188874751, \"y2\": 85065727}]," +
        " \"sublyr\": [{\"type\": 4, \"sid\": 9000003}], \"minzoom\": 6, \"maxzoom\": 20, \"id\": 9003}";

    @Override
    public void onClick(View v) {
        //super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_show_road:
                mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_ROAD, !mapView.getViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_ROAD));
                tv_msg1.setText(mapView.getViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_ROAD) ? "打开路网" : "关闭路网");
                break;
            case R.id.btn_show_road_arrow:
                bshow_road_arrow = !bshow_road_arrow;
                if (bshow_road_arrow) {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_ROAD_ARROW, true);
                    tv_msg1.setText("打开" + "\r\n" + "二维道路箭头");
                } else {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_ROAD_ARROW, false);
                    tv_msg1.setText("关闭" + "\r\n" + "二维道路箭头");
                }
                break;
            case R.id.btn_show_region:
                bshow_region = !bshow_region;
                if (bshow_region) {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_REGION, true);
                    tv_msg1.setText("打开" + "\r\n" + "二维背景面");
                } else {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_REGION, false);
                    tv_msg1.setText("关闭" + "\r\n" + "二维背景面");
                }
                break;
            case R.id.btn_show_build_model:
                bshow_build_model = !bshow_build_model;
                if (bshow_build_model) {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_BUILD_MODEL, true);
                    tv_msg1.setText("打开" + "\r\n" + "二维建筑物");
                } else {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_BUILD_MODEL, false);
                    tv_msg1.setText("关闭" + "\r\n" + "二维建筑物");
                }
                break;
            case R.id.btn_show_poi:
                bshow_poi = !bshow_poi;
                if (bshow_poi) {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_POI, true);
                    tv_msg1.setText("打开" + "\r\n" + "标注/poi");
                } else {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_POI, false);
                    tv_msg1.setText("关闭" + "\r\n" + "标注/poi");
                }
                break;
            case R.id.btn_show_simple3d_on:
                bshow_simple3d_on = !bshow_simple3d_on;
                if (bshow_simple3d_on) {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_SIMPLE3D_ON, true);
                    tv_msg1.setText("打开" + "\r\n" + "简易三维");
                } else {
                    mapView.setViewState(mMainEngineID, GLMapStaticValue.AM_MAP_VIEWSTATE_IS_SHOW_SIMPLE3D_ON, false);
                    tv_msg1.setText("关闭" + "\r\n" + "简易三维");
                }
                break;
            case R.id.btn_day:
                bmapmode_night = !bmapmode_night;
                if (bmapmode_night) {
                    mapView.setMapModeAndStyle(mMainEngineID, 0, GLMapView.MAPVIEW_TIME_DAY, MAPVIEW_STATE_NAVI_CAR);
                    tv_msg1.setText("显示白天");
                } else {
                    mapView.setMapModeAndStyle(mMainEngineID, 0, GLMapView.MAPVIEW_TIME_NIGHT, MAPVIEW_STATE_NAVI_CAR);
                    tv_msg1.setText("显示黑夜");
                }
                mapView.setNaviMode(mMainEngineID,true);

                break;
            case R.id.btn_traffic:  //交通
                bTrafficTMC = !mapView.getTrafficState(mMainEngineID);
                mapView.setTrafficState(mMainEngineID, bTrafficTMC);
                tv_msg1.setText(bTrafficTMC ? "显示交通" : "不显示交通");
                break;
            case R.id.btn_zoomin://放大
                mapView.zoomIn(mMainEngineID);
                tv_msg1.setText("放大");
                break;

            case R.id.btn_zoomout: //缩小
                mapView.zoomOut(mMainEngineID);
                tv_msg1.setText("缩小");
                break;

            case R.id.btn_mode:  //地图模式
                MapviewModeParam modeparam = new MapviewModeParam();
                modeparam.bChangeCenter = true;
                if (++mode > 2) {
                    mode = 0;
                }
                switch (mode) {
                    case 0:
                        modeparam.mode = GLMapStaticValue.EAMapviewModeNorth; //2D北上
                        tv_msg1.setText("2D北上");
                        break;
                    case 1:
                        modeparam.mode = GLMapStaticValue.EAMapviewModeCar;//2D车上
                        tv_msg1.setText("2D车上");
                        break;
                    case 2:
                        modeparam.mode = GLMapStaticValue.EAMapviewMode3D;//3D车上
                        tv_msg1.setText("3D车上");
                        break;
                    default:
                        break;
                }
                float cameraDegree = mapView.getCameraDegree(mMainEngineID);
                mapView.setMapMode(mMainEngineID, modeparam, true, true);
                break;
            case R.id.btn_pointOverlay:
                bPointOverlay = !bPointOverlay;
                if (bPointOverlay) {
                    pointOverlay = new PointOverlay(mMainEngineID, mapView.getContext(), mapView);
                    mapView.getOverlayBundle(mMainEngineID).addOverlay(pointOverlay);
                    pointOverlay.addItem(221010004, 101712921);
                    pointOverlay.setVisible(true);
                } else {
                    mapView.getOverlayBundle(mMainEngineID).removeOverlay(pointOverlay);
                    pointOverlay = null;
                }
                tv_msg1.setText(bPointOverlay ? "天安门显示PointOverlay" : "销毁PointOverlay");
                break;
            case R.id.btn_pointVisiable:
                if (null == pointOverlay){
                    tv_msg1.setText("PointOverlay未创建");
                    break;
                }
                bPointOverlayVisiable = !bPointOverlayVisiable;
                pointOverlay.setVisible(bPointOverlayVisiable);
                tv_msg1.setText(bPointOverlayVisiable ? "PointOverlay可见" : "PointOverlay不可见");
                break;
            case R.id.btn_openlayeron:
                switch (++nOpenlayer % 3) {
                    case 0:
                        mapView.appendOpenLayer(mMainEngineID, OPENDEFAULTLAYER_JESON.getBytes());
                        mapView.deleteOpenLayer(mMainEngineID, 9003);
                        mapView.setBusinessDataPara(mMainEngineID, GLMapStaticValue.BUSINESSDATA_OPENLAYER_ON, 1, 9001, 0, 0);
                        tv_msg1.setText("显示开发图层9001");
                        break;
                    case 1:
                        mapView.deleteOpenLayer(mMainEngineID, 9001);
                        mapView.appendOpenLayer(mMainEngineID, OPENLAYER2_JESON.getBytes());
                        mapView.setBusinessDataPara(mMainEngineID, GLMapStaticValue.BUSINESSDATA_OPENLAYER_ON, 1, 9003, 0, 0);
                        tv_msg1.setText("显示开放图层9003");
                        break;
                    case 2:
                        mapView.deleteOpenLayer(mMainEngineID, 9001);
                        mapView.deleteOpenLayer(mMainEngineID, 9003);
                        mapView.setBusinessDataPara(mMainEngineID, GLMapStaticValue.BUSINESSDATA_OPENLAYER_ON, 0, 9005, 0, 0);
                        tv_msg1.setText("关闭开放图层");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        GLGeoPoint point = mapView.getMapCenter(mMainEngineID);
        tv_msg2.setText("Level:"+mapView.getPreciseLevel(mMainEngineID)
            +"\nAngle:"+mapView.getMapAngle(mMainEngineID) + "\nCamera:"+mapView.getCameraDegree(mMainEngineID));
    }

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    public static void showShortToast(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null)
                    mToast = Toast.makeText(CC.getApplication(), content, Toast.LENGTH_SHORT);
                else{
                    mToast.setText(content);}

                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  MapListener  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    GTrafficEventDetailRequestParam param = new GTrafficEventDetailRequestParam();
    BLToolPoiID poiID = new BLToolPoiID();
    /**
     * @brief onClickLabel 点击到了图面的label, 由渲染引擎绘制的label
     * @param[in] engineId   引擎实例id
     * @param[in] pLabels    点击的labels
     */
    @Override
    public void onClickLabel(int engineId, MapLabelItem[] pLabels) {
        //mSublayerId;// 交通事件(9000001,9000002,9000003)，第三方推荐点就是点的类型
        if (pLabels != null && pLabels.length > 0) {
            final MapLabelItem item = pLabels[0];
            if (item.type == 4) {  //交通事件
                Log.i(TAG, "onClickLabel: " + item.mSublayerId);
                param.eventid = poiID.PoiIDToEventID(item.poiid, 0);
                BLAosServiceManager.getInstance().requestTrafficEventDetail(param);

            } else { //非交通事件
                String poiString = "poi名称:" + item.name + "\npoiID:" +item.poiid + "\npixel20XY:"+item.pixel20X +" "+item.pixel20Y;
                showShortToast(poiString);
            }
        } else {
            //无相关信息

        }
    }




    @Override
    public void onScreenShotFinished(int deviceId, long bufInstance) {  }

    @Override
    public void onScreenShotFinished(int deviceId, Bitmap bitmap) {  }

    @Override
    public void onScreenShotFinished(int deviceId, String filePath) {  }

    /**
     * @brief 屏幕触碰事件回调
     * @param[in] engineId    引擎实例ID
     * @param[in] action  触碰事件
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMotionEvent(int engineId, int action, int px, int py) {   }

    /**
     * @brief 移图开始事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMoveBegin(int engineId, int px, int py) {   }

    /**
     * @brief 移图结束事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMoveEnd(int engineId, int px, int py) {  }

    /**
     * @brief 移图事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMove(int engineId, int px, int py) {  }

    /**
     * @brief 长按手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onLongPress(final int engineId, final int px, final int py) {  }

    /**
     * @return 接受者是否消费该回调  true：消费，false：没有消费
     * @brief 双击手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public boolean onDoublePress(int engineId, int px, int py) {   return  true;}

    /**
     * @return
     * @brief 单击手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @param[in] clickElement 是否点击元素
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public boolean onSinglePress(int engineId, final int px, final int py, boolean clickElement) {    return true;}

    /**
     * @brief 滑动手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] velocityX    X方向速度
     * @param[in] velocityY    Y方向速度
     */
    @Override
    public void onSliding(int engineId, float velocityX, float velocityY) {   }

    /**
     * @brief 地图中心点改变回调
     * @param[in] engineId    引擎实例ID
     * @param[in] lon        改变后的地图中心点X轴坐标
     * @param[in] lat        改变后的地图中心点Y轴坐标
     */
    @Override
    public void onMapCenterChanged(int engineId, int px, int py) {    }

    /**
     * @brief 地图画布大小改变
     * @param[in] engineId  引擎实例ID
     */
    @Override
    public void onMapSizeChanged(int engineId) {    }

    /**
     * @brief 地图比例尺改变回调
     * @param[in] engineId  引擎实例ID
     * @param[in] bZoomIn   是否是图面放大操作
     */
    @Override
    public void onMapLevelChanged(final int engineId, boolean bZoomIn) {
                //  tv_msg2.setText("onMapLevelChanged");

/*            tv_msg2.setText("level:" + mapView.getPreciseLevel(mMainEngineID) + "\n" +
            "angle:" + mapView.getMapAngle(mMainEngineID) + "\n" +
            "camera:" + mapView.getCameraDegree(mMainEngineID));*/
    }

    /**
     * @brief 地图中心点改变回调
     * @param[in] engineId    引擎实例ID
     * @param[in] viewMode    视图模式
     */
    @Override
    public void onMapModeChanged(int engineId, @GLMapStaticValue.EAMapviewMode1 int viewMode) {   }

    /**
     * @brief 调用全览回调
     * @param[in] engineId    引擎实例ID
     */
    @Override
    public void onMapPreviewEnter(int engineId) {   }

    /**
     * @brief 退出全览回调
     * @param[in] engineId    引擎实例ID
     */
    @Override
    public void onMapPreviewExit(int engineId) {   }



    /**
     * @param engineId 引擎实例ID
     * @param px       点击x坐标
     * @param py       点击y坐标
     * @brief onClickBlank 点击地图空白处的回调
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onClickBlank(int engineId, float px, float py) {     }

    /**
     * @brief 地图引擎渲染回调，不要在这里面做太多事，否则严重影响绘制效率
     * @param[in] engineId   引擎ID
     * @param[in] type 渲染回调类型
     */
    @Override
    public void onRenderMap(int engineId, @GLMapStaticValue.EARenderMapType int type) {   }

    /**
     * @brief 三维动画结束后，回调客户端进行处理
     * @param[in] engineId   引擎ID
     * @note onRealCityAnimateFinish 不要干太耗时的事情，否则会降低绘制效率
     */
    @Override
    public void onRealCityAnimationFinished(int engineId) {   }

    /**
     * @brief 动画执行结束
     * @param[in] engineId   引擎ID
     * @param[in] animID       动画ID
     */
    @Override
    public void onMapAnimationFinished(int engineId, int animationId) {  }

    /**
     * 道路提示回调,如道路附近建筑名
     *
     * @param[in] engineId   引擎ID
     * @param[in] pRoadTips 道路提示信息数组指针
     * @param[in] size 道路提示信息个数
     */
    @Override
    public void onRouteBoardData(int engineId, MapRoadTip[] pRoadTips) {   }

    /**
     * 热力图回调
     *
     * @param[in] engineId   引擎ID
     * @param[in] bActive 热力是否活跃状态
     */
    @Override
    public void onMapHeatActive(int engineId, boolean bActive) {  }

    /**
     * 景区控件回调
     *
     * @param[in] engineId   引擎ID
     * @param[in] ScenicInfo 景区信息,无图为NULL
     */
    @Override
    public void onScenicActive(int engineId, ScenicInfo pInfo) {  }

    /**
     * @brief 地图动作结束回调, 所有地图操作结束都会回调此函数
     * @param[in] nMotion      动画类型
     */
    @Override
    public void onMotionFinished(int engineId, int nMotion) {
        //tv_msg2.setText("onMotionFinished");
/*        tv_msg2.setText("Level:" + mapView.getZoomLevel(mMainEngineID) + "\n" +
            "Angle:" + mapView.getMapAngle(mMainEngineID) + "\n" +
            "Camera:" + mapView.getCameraDegree(mMainEngineID)+"zxzxzxzx");*/
    }

    /**
     * @brief 地图引擎每绘制一帧之前的回调，不要在这里面做太多事，否则严重影响绘制效率
     * @param[in] engineId   引擎ID
     */
    @Override
    public void onPreDrawFrame(int engineId) {   }





    private BLAosResponseObserver blAosResponseObserver = new BLAosResponseObserver() {
        @Override
        public void onMojiWeatherResponse(GMojiWeatherResponseParam gMojiWeatherResponseParam) {

        }

        @Override
        public void OnQueryPersentWeatherResponse(GQueryPersentWeatherResponseParam var1) {

        }

        @Override
        public void OnAddressPredictResponse(GAddressPredictResponseParam gAddressPredictResponseParam) {

        }

        @Override
        public void OnHolidayListResponse(GHolidayListResponseParam gHolidayListResponseParam) {

        }

        @Override
        public void OnWorkdayListResponse(GWorkdayListResponseParam gWorkdayListResponseParam) {

        }

        @Override
        public void OnReStrictedAreaResponse(GReStrictedAreaResponseParam gReStrictedAreaResponseParam) {

        }

        @Override
        public void OnEtaRequestResponse(CEtaRequestReponseParam cEtaRequestReponseParam) {

        }

        @Override
        public void OnDriveReportSmsResponse(GDriveReportSmsResponseParam gDriveReportSmsResponseParam) {

        }

        @Override
        public void OnDriveReportUploadResponse(GDriveReportUploadResponseParam gDriveReportUploadResponseParam) {

        }

        @Override
        public void OnTrafficRestrictResponse(GTrafficRestrictResponseParam gTrafficRestrictResponseParam) {

        }

        @Override
        public void OnCarLtdBindResponse(GCarLtdBindResponseParam gCarLtdBindResponseParam) {

        }

        @Override
        public void OnCarLtdCheckTokenResponse(GCarLtdCheckTokenResponseParam gCarLtdCheckTokenResponseParam) {

        }

        @Override
        public void OnCarLtdQuickLoginResponse(GCarLtdQuickLoginResponseParam gCarLtdQuickLoginResponseParam) {

        }

        @Override
        public void OnCarLtdQuickRegisterResponse(GCarLtdQuickRegisterResponseParam gCarLtdQuickRegisterResponseParam) {

        }

        @Override
        public void OnPaymentBindAlipayResponse(GPaymentBindAlipayResponseParam gPaymentBindAlipayResponseParam) {

        }

        @Override
        public void OnPaymentStatusResponse(GPaymentStatusResponseParam gPaymentStatusResponseParam) {

        }

        @Override
        public void OnPaymentUnbindAlipayResponse(GPaymentUnbindAlipayResponseParam gPaymentUnbindAlipayResponseParam) {

        }

        @Override
        public void OnCancelSignPayResponse(GCancelSignPayResponseParam gCancelSignPayResponseParam) {

        }

        @Override
        public void OnFeedbackReportResponse(GFeedbackReportResponseParam gFeedbackReportResponseParam) {

        }

        @Override
        public void OnParkOrderCreateResponse(GParkOrderCreateResponseParam gParkOrderCreateResponseParam) {

        }

        @Override
        public void OnParkOrderDetailResponse(GParkOrderDetailResponseParam gParkOrderDetailResponseParam) {

        }

        @Override
        public void OnParkOrderListResponse(GParkOrderListResponseParam gParkOrderListResponseParam) {

        }

        @Override
        public void OnParkPayStatusResponse(GParkPayStatusResponseParam gParkPayStatusResponseParam) {

        }

        @Override
        public void OnParkServiceResponse(GParkServiceResponseParam gParkServiceResponseParam) {

        }

        @Override
        public void OnQRCodeConfirmResponse(GQRCodeConfirmResponseParam gqrCodeConfirmResponseParam) {

        }

        @Override
        public void OnTrafficEventCommentResponse(GTrafficEventCommentResponseParam gTrafficEventCommentResponseParam) {

        }

        @Override
        public void OnUserDeviceResponse(GUserDeviceResponseParam gUserDeviceResponseParam) {

        }

        public  String getLastUpdateTimeStr(long lastUpdateTime) {
            long curTime = System.currentTimeMillis() / 1000;
            long second = curTime - lastUpdateTime;
            if (second < 60) {
                // 小于60s
                return "小于60s";
            } else {
                long minute = second / 60;
                if (minute < 60) {
                    // 小于60分钟
                    return "小于60分钟";
                } else {
                    long hour = minute / 60;
                    if (hour < 24) {
                        // 小于24小时
                        return "小于24小时";
                    } else {
                        // 大于或等于24小时
                        return "大于或等于24小时";
                    }
                }
            }
        }
        @Override
        public void OnTrafficEventDetailResponse(final GTrafficEventDetailResponseParam gTrafficEventDetailResponseParam) {
            final StringBuffer buffer = new StringBuffer();
            if (gTrafficEventDetailResponseParam != null && gTrafficEventDetailResponseParam.EventData != null) {
                buffer.append(gTrafficEventDetailResponseParam.EventData.title + "\r\n" +
                    "最后更新时间:" + getLastUpdateTimeStr(Long.parseLong(gTrafficEventDetailResponseParam.EventData.lastupdate)));
/*                if (gTrafficEventDetailResponseParam.EventData.subinfo != null && gTrafficEventDetailResponseParam.EventData.subinfo.size() > 0) {
                    for (int i = 0; i < gTrafficEventDetailResponseParam.EventData.subinfo.size(); i++) {
                        buffer.append("\r\n" + "子事件:" + gTrafficEventDetailResponseParam.EventData.subinfo.get(i).title);
                    }


                }*/
            } else {
                buffer.append("未获取到交通事件");
            }
            String trafficStr = new String(buffer);
            showShortToast(trafficStr);
        }

        @Override
        public void OnQueryCarMsgResponse(GQueryCarMsgResponseParam gQueryCarMsgResponseParam) {

        }

        @Override
        public void OnSendToPhoneResponse(GSendToPhoneResponseParam gSendToPhoneResponseParam) {

        }

        @Override
        public void OnLogUploadResponse(GLogUploadResponseParam gLogUploadResponseParam) {

        }
        @Override
        public void OnQueryWeatherByLinkResponse(GQueryWeatherByLinkResponseParam var1) {

        }
    };
}
