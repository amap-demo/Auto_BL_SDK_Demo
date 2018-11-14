package com.amap.gbl.sdkdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.amap.gbl.sdkdemo.model.MapStyleReader;
import com.amap.gbl.sdkdemo.platform.CommonUtil;
import com.amap.gbl.sdkdemo.presenter.IRouteObserverPresenter;
import com.amap.gbl.sdkdemo.presenter.RouteManager;
import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.biz.IBizLayerService;
import com.autonavi.gbl.biz.IBizSceneService;
import com.autonavi.gbl.biz.ICalculatePreviewUtil;
import com.autonavi.gbl.biz.bizenum.AutoOverlayType;
import com.autonavi.gbl.biz.bizenum.RouteOverlayElem;
import com.autonavi.gbl.biz.bizenum.RoutePathPointScene;
import com.autonavi.gbl.biz.bizenum.RouteScene;
import com.autonavi.gbl.biz.model.BizPathInfo;
import com.autonavi.gbl.biz.model.CarLocation;
import com.autonavi.gbl.biz.model.PathPoint;
import com.autonavi.gbl.biz.model.PathPoints;
import com.autonavi.gbl.biz.model.RouteDrawStyleParam;
import com.autonavi.gbl.common.model.Coord2DDouble;
import com.autonavi.gbl.common.model.Coord2DInt32;
import com.autonavi.gbl.common.model.RectDouble;
import com.autonavi.gbl.common.path.drive.accessor.DrivePathAccessor;
import com.autonavi.gbl.common.path.drive.model.POIForRequest;
import com.autonavi.gbl.common.path.drive.model.POIInfo;
import com.autonavi.gbl.common.path.drive.model.RestrictionInfo;
import com.autonavi.gbl.common.path.model.option.RerouteOption;
import com.autonavi.gbl.common.path.model.option.RouteOption;
import com.autonavi.gbl.common.path.model.option.RouteOptionEnum;
import com.autonavi.gbl.common.path.model.result.PathResult;
import com.autonavi.gbl.common.path.model.result.VariantPath;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.map.MapRoadTip;
import com.autonavi.gbl.map.PreviewParam;
import com.autonavi.gbl.map.ScenicInfo;
import com.autonavi.gbl.map.glinterface.GLGeoPoint;
import com.autonavi.gbl.map.glinterface.MapLabelItem;
import com.autonavi.gbl.map.listener.MapListener;
import com.autonavi.gbl.map.utils.GLMapStaticValue;
import com.autonavi.gbl.map.utils.GLMapUtil;
import com.autonavi.gbl.route.model.RouteAosOption;
import com.autonavi.gbl.route.model.RouteEnum;
import com.autonavi.gbl.route.observer.IRouteResultObserver;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceDataPath;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.autonavi.gbl.route.model.RouteEnum.RouteControlKeyVehicleHeight;
import static com.autonavi.gbl.route.model.RouteEnum.RouteControlKeyVehicleLength;
import static com.autonavi.gbl.route.model.RouteEnum.RouteControlKeyVehicleType;
import static com.autonavi.gbl.route.model.RouteEnum.RouteControlKeyVehicleWidth;

public class MainActivity extends AppCompatActivity implements IRouteObserverPresenter, View.OnClickListener, MapListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName().toString();
    private TextView tv_mapVersion = null;
    private TextView tv_mapLevel = null;
    private EditText et_centerLon = null;
    private Button btn_zoom_in = null;
    private Button btn_zoom_out = null;
    private Button btn_offline_route = null;//在线算路
    private Button btn_online_route = null;//在线算路
    private Button btn_aos_route = null;//在线算路
    private Button btn_clear_route = null; //清除路线
    private Button btn_start_point = null;
    private Button btn_via_point = null;
    private Button btn_end_point = null;
    private ToggleButton btn_route_truck = null;
    private ToggleButton btn_route_restrict = null;
    private CheckBox cb_route_congestion = null;
    private CheckBox cb_route_toll = null;
    private CheckBox cb_route_avoid_highway = null;
    private CheckBox cb_route_highway = null;
    private Button btn_car_plate = null;
    private EditText et_car_plate_id = null;
    private Button btn_select_route = null;
    private ToggleButton btn_route_preview = null;
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
    public GLMapView mMapView;
    public GlMapSurface glMapSurface;
    private static int initFlg = 0;
    private int mMainEngineID;
    private RouteOption mRouteOption;
    private POIForRequest mPoiForRequest;
    private PathPoints mPathPoints;
    private RouteAosOption mRouteAosOption;
    private RectDouble rect;
    private RerouteOption rerouteOption;
    private ArrayList<BizPathInfo> pathAttributes;
    private PathResult paths;
    private long count;
    private CarLocation carLoc;
    private AlertDialog.Builder alertDialog;
    private POIInfo startInfo;
    private POIInfo endInfo;
    private ArrayList<POIInfo> vias = new ArrayList<>();
    private Coord2DDouble presspoints;
    private int segmentCount;
    private boolean routePlan = true;
    public MapStyleReader iMapStyleReader;
    public IBizLayerService mIBizLayerService;
    public IBizSceneService mIBizSceneService;
    private boolean carid = false;
    private boolean bRestrict = false;

    private boolean bRouteTruck = false;
    private boolean bRouteAvoidCongestion = false;
    private boolean bRouteAvoidToll = false;
    private boolean bRouteAvoidHighway = false;
    private boolean bRouteHighway =false;
    private String   mCarPlate = null;
    private boolean bSetCarPlate = false;
    private boolean bSetTruck = false;
    private int drawRouteCount = 0;
    private int drawRouteIndex = 0;
    private int drawRouteType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initManagerService();
        setContentView(R.layout.activity_main);
        initMap();
        initRoute();
        initBiz();
        initData();
        initView();
        tv_mapVersion.setText("AE："+ GLMapView.getMapEngineVersion(mMainEngineID) + "\r\n" + "BL：" + ServiceMgr.getVeison());  //获取地图引擎版本号
        Log.i(TAG, "地图引擎版本号  = " + GLMapView.getMapEngineVersion(mMainEngineID));
    }

    private void initData()
    {
        startInfo = new POIInfo();
        endInfo = new POIInfo();
    }

    private  void initRoute()
    {
        boolean init = RouteManager.getInstance().init();
        Log.i(TAG, "initTbt: init = " + init);
        if (init) {
            RouteManager.getInstance().registerRouteObserver(this);
        }
    }

    private void uninitTbt() {
        RouteManager.getInstance().uninit();
    }

    private void initBiz() {
        //设置纹理
        iMapStyleReader = new MapStyleReader(this, mMapView);
        mIBizLayerService = (IBizLayerService) ServiceMgr.getServiceMgrInstance().createBLService(ServiceManagerEnum.BizLayerMultiServiceID);
        int init1 = mIBizLayerService.init(mMapView.getNativeMapViewInstance(mMainEngineID), iMapStyleReader);//初始化
        Log.i(TAG, "initIBizLayerService: init = " + init1);
        boolean recycled = mIBizLayerService.isRecycled();
        Log.i(TAG, "initBiz: recycled = " + recycled);
        mIBizSceneService = (IBizSceneService) ServiceMgr.getServiceMgrInstance().createBLService(ServiceManagerEnum.BizSceneMultiServiceID);
        int init2 = mIBizSceneService.init(mMapView.getNativeMapViewInstance(mMainEngineID), mIBizLayerService);
        Log.i(TAG, "initIBizSceneService: init = " + init2);
    }

    private void initMap() {
        mMapView = new GLMapView(CC.getApplication());
        glMapSurface = (GlMapSurface) findViewById(R.id.mapview);
        glMapSurface.setGLMapView(mMapView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;
        //创建地图  初始化地图
        mMainEngineID = mMapView.createEngineWithFrame(mMapView.mDefaultDeviceId, GLMapUtil.AMAP_ENGINE_TYPE_MAIN,
                new Rect(0, 0, mScreenWidth, mScreenHeight), mScreenWidth, mScreenHeight);
        Log.i(TAG, "initMap: mMainEngineID = " + mMainEngineID);
        mMapView.setMapListener(this);
        mMapView.setMapCenter(mMainEngineID, 221010004, 101712921);  //设置中心点为天安门
        mMapView.setMapLevel(mMainEngineID, 15f);//默认缩放级别为15
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

        f = new File(pcCfgFilePath, "detail_list");
        if (!f.exists()) {
            copyAssets(CC.getApplication(), f, "detail_list");
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

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  MapListener  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onScreenShotFinished(int deviceId, long bufInstance) {
        Log.i(TAG, "onScreenShotFinished: deviceId = " + deviceId + " , bufInstance = " + bufInstance);
    }

    @Override
    public void onScreenShotFinished(int deviceId, Bitmap bitmap) {
        Log.i(TAG, "onScreenShotFinished: deviceId = " + deviceId + " , bitmap = " + bitmap);
    }

    @Override
    public void onScreenShotFinished(int deviceId, String filePath) {
        Log.i(TAG, "onScreenShotFinished: deviceId = " + deviceId + " , filePath = " + filePath);
    }

    /**
     * @brief 屏幕触碰事件回调
     * @param[in] engineId    引擎实例ID
     * @param[in] action  触碰事件
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMotionEvent(int engineId, int action, int px, int py) {
//        Log.i(TAG, "onMotionEvent: 屏幕触碰事件回调 :" + px + ".............." + py);
    }

    /**
     * @brief 移图开始事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMoveBegin(int engineId, int px, int py) {
    }

    /**
     * @brief 移图结束事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMoveEnd(int engineId, int px, int py) {
//        Log.i(TAG, "onMoveEnd: 移图结束事件 :" + px + ".............." + py);
    }

    /**
     * @brief 移图事件
     * @param[in] engineId    引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onMove(int engineId, int px, int py) {
//        Log.i(TAG, "onMove: 移图事件 :" + px + ".............." + py);
    }

    /**
     * @brief 长按手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onLongPress(final int engineId, final int px, final int py) {
        Log.i(TAG, "onLongPress: 长按手势回调:" + px + ".............." + py);
        Log.i(TAG, "onLongPress: px = " + px + ", py = " + py);
        presspoints = mMapView.mapToLonLat(engineId, mMapView.fromPixels(engineId, px, py).x, mMapView.fromPixels(engineId, px, py).y);
        Log.i(TAG, "onLongPress: presspoints = " + presspoints.lat + "," + presspoints.lon);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle("点类型");
                final String[] pointTypes = {"起点", "途经点", "终点"};
                // 选择默认选中，-1表示不选中
                alertDialog.setSingleChoiceItems(pointTypes, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:  //起点
                                startInfo.longitude = (float) presspoints.lon;
                                startInfo.latitude = (float) presspoints.lat;
                                Log.i(TAG, "lon = " + startInfo.longitude + ",lat = " + startInfo.latitude);
                                CommonUtil.showShortToast("添加起点成功");
                                break;
                            case 1: //途经点
                                if(vias.size() < 3) {
                                    POIInfo via = new POIInfo();
                                    via.longitude = (float) presspoints.lon;
                                    via.latitude = (float) presspoints.lat;
                                    vias.add(via);
                                    CommonUtil.showShortToast("添加中途点成功,个数：" + vias.size());
                                }
                                else
                                {
                                    CommonUtil.showShortToast("添加中途点失败，中途点数已满最大值3个");
                                }
                                break;
                            case 2: //终点
                                endInfo.longitude = (float) presspoints.lon;
                                endInfo.latitude = (float) presspoints.lat;
                                CommonUtil.showShortToast("添加终点成功");
                                break;
                        }

                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    /**
     * @return 接受者是否消费该回调  true：消费，false：没有消费
     * @brief 双击手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] px    x坐标
     * @param[in] py    y坐标
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public boolean onDoublePress(int engineId, int px, int py) {
        Log.i(TAG, "onDoublePress: 双击手势回调 : " + px + ".............." + py);
        return false;
    }

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
    public boolean onSinglePress(int engineId, final int px, final int py, boolean clickElement) {
        Log.i(TAG, "onSinglePress: 单击手势回调 : " + px + ".............." + py);
        return false;
    }

    /**
     * @brief 滑动手势回调
     * @param[in] engineId  引擎实例ID
     * @param[in] velocityX    X方向速度
     * @param[in] velocityY    Y方向速度
     */
    @Override
    public void onSliding(int engineId, float velocityX, float velocityY) {
        Log.i(TAG, "onSliding: 滑动手势回调 : " + velocityX + ".............." + velocityY);
    }

    /**
     * @brief 地图中心点改变回调
     * @param[in] engineId    引擎实例ID
     * @param[in] lon        改变后的地图中心点X轴坐标
     * @param[in] lat        改变后的地图中心点Y轴坐标
     */
    @Override
    public void onMapCenterChanged(int engineId, int px, int py) {
        Log.i(TAG, "onMapCenterChanged: 地图中心点改变回调 : " + px + ".............." + py);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GLGeoPoint center = mMapView.getMapCenter(mMainEngineID);
                Coord2DDouble coord2DDouble = mMapView.mapToLonLat(mMainEngineID, center.x, center.y);
                String LonLat = Double.toString(coord2DDouble.lon).substring(0,10);
                LonLat += ",";
                LonLat += Double.toString(coord2DDouble.lat).substring(0,9);
                et_centerLon.setText(LonLat);
            }
        });
    }

    /**
     * @brief 地图画布大小改变
     * @param[in] engineId  引擎实例ID
     */
    @Override
    public void onMapSizeChanged(int engineId) {

    }

    /**
     * @brief 地图比例尺改变回调
     * @param[in] engineId  引擎实例ID
     * @param[in] bZoomIn   是否是图面放大操作
     */
    @Override
    public void onMapLevelChanged(final int engineId, boolean bZoomIn) {

    }

    /**
     * @brief 地图中心点改变回调
     * @param[in] engineId    引擎实例ID
     * @param[in] viewMode    视图模式
     */
    @Override
    public void onMapModeChanged(int engineId, @GLMapStaticValue.EAMapviewMode1 int viewMode) {

    }

    /**
     * @brief 调用全览回调
     * @param[in] engineId    引擎实例ID
     */
    @Override
    public void onMapPreviewEnter(int engineId) {

    }

    /**
     * @brief 退出全览回调
     * @param[in] engineId    引擎实例ID
     */
    @Override
    public void onMapPreviewExit(int engineId) {

    }

    /**
     * @brief onClickLabel 点击到了图面的label, 由渲染引擎绘制的label
     * @param[in] engineId   引擎实例id
     * @param[in] pLabels    点击的labels
     */
    @Override
    public void onClickLabel(int engineId, MapLabelItem[] pLabels) {
    }


    /**
     * @param engineId 引擎实例ID
     * @param px       点击x坐标
     * @param py       点击y坐标
     * @brief onClickBlank 点击地图空白处的回调
     * @note 坐标参数都是屏幕坐标
     */
    @Override
    public void onClickBlank(int engineId, float px, float py) {
        Log.i(TAG, "onClickBlank: 点击地图空白处的回调");
    }

    /**
     * @brief 地图引擎渲染回调，不要在这里面做太多事，否则严重影响绘制效率
     * @param[in] engineId   引擎ID
     * @param[in] type 渲染回调类型
     */
    @Override
    public void onRenderMap(int engineId, @GLMapStaticValue.EARenderMapType int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int level = mMapView.getZoomLevel(mMainEngineID);
                String realValue = "比例尺值：";
                switch ( level )
                {
                    case 20:
                        realValue = realValue + "5米";
                        break;
                    case 19:
                        realValue = realValue + "10米";
                        break;
                    case 18:
                        realValue = realValue + "25米";
                        break;
                    case 17:
                        realValue = realValue + "50米";
                        break;
                    case 16:
                        realValue = realValue + "100米";
                        break;
                    case 15:
                        realValue = realValue + "200米";
                        break;
                    case 14:
                        realValue = realValue + "500米";
                        break;
                    case 13:
                        realValue = realValue + "1千米";
                        break;
                    case 12:
                        realValue = realValue + "2千米";
                        break;
                    case 11:
                        realValue = realValue + "5千米";
                        break;
                    case 10:
                        realValue = realValue + "10千米";
                        break;
                    case 9:
                        realValue = realValue + "20千米";
                        break;
                    case 8:
                        realValue = realValue + "30千米";
                        break;
                    case 7:
                        realValue = realValue + "50千米";
                        break;
                    case 6:
                        realValue = realValue + "100千米";
                        break;
                    case 5:
                        realValue = realValue + "200千米";
                        break;
                    case 4:
                        realValue = realValue + "500千米";
                        break;
                    case 3:
                        realValue = realValue + "1000千米";
                        break;
                    default:
                        break;
                }

                tv_mapLevel.setText("比例级数：" + level + "级\n\r"  + realValue);
            }
        });
    }

    /**
     * @brief 三维动画结束后，回调客户端进行处理
     * @param[in] engineId   引擎ID
     * @note onRealCityAnimateFinish 不要干太耗时的事情，否则会降低绘制效率
     */
    @Override
    public void onRealCityAnimationFinished(int engineId) {

    }

    /**
     * @brief 动画执行结束
     * @param[in] engineId   引擎ID
     * @param[in] animID       动画ID
     */
    @Override
    public void onMapAnimationFinished(int engineId, int animationId) {

    }

    /**
     * 道路提示回调,如道路附近建筑名
     *
     * @param[in] engineId   引擎ID
     * @param[in] pRoadTips 道路提示信息数组指针
     * @param[in] size 道路提示信息个数
     */
    @Override
    public void onRouteBoardData(int engineId, MapRoadTip[] pRoadTips) {

    }

    /**
     * 热力图回调
     *
     * @param[in] engineId   引擎ID
     * @param[in] bActive 热力是否活跃状态
     */
    @Override
    public void onMapHeatActive(int engineId, boolean bActive) {

    }

    /**
     * 景区控件回调
     *
     * @param[in] engineId   引擎ID
     * @param[in] ScenicInfo 景区信息,无图为NULL
     */
    @Override
    public void onScenicActive(int engineId, ScenicInfo pInfo) {

    }

    /**
     * @brief 地图动作结束回调, 所有地图操作结束都会回调此函数
     * @param[in] nMotion      动画类型
     */
    @Override
    public void onMotionFinished(int engineId, int nMotion) {
    }

    /**
     * @brief 地图引擎每绘制一帧之前的回调，不要在这里面做太多事，否则严重影响绘制效率
     * @param[in] engineId   引擎ID
     */
    @Override
    public void onPreDrawFrame(int engineId) {

    }

    @Override
    public void onNewRoute(int mode, int type, PathResult pathResult, Object externDataPtr, boolean isLocal) {

        count = pathResult.getPathCount();
        CommonUtil.showShortToast("算路成功" + "\r\n" + "路线个数:" + count);
        paths = pathResult;

        if( paths == null )
        {
            return;
        }

        if (pathResult != null && pathResult.getPathCount() > 0) {
            VariantPath path = pathResult.getPath(0);
            RestrictionInfo restrictionInfo = DrivePathAccessor.obtain(path).getRestrictionInfo();
            if (restrictionInfo != null) {
                String title = restrictionInfo.title;     //!< 标题
                String desc = restrictionInfo.desc;      //!< 描述
                String tips = restrictionInfo.tips;      //!< tips描述
                int cityCode = restrictionInfo.cityCode;     //!< 城市代码
                short mType = restrictionInfo.type;       //!< 类型
                int titleType = restrictionInfo.titleType;  //!< tips类型
                ArrayList<Long> ruleIDs = restrictionInfo.ruleIDs; //!< 规则ID 新增字段

                StringBuffer sb = new StringBuffer();
                if (ruleIDs != null && ruleIDs.size() > 0) {
                    for (int i = 0; i < ruleIDs.size(); i++) {
                        if (i == (ruleIDs.size() - 1)) {
                            sb.append(ruleIDs.get(i));
                        } else {
                            sb.append(ruleIDs.get(i) + ";");
                        }
                    }
                }

                CommonUtil.showShortToast("标题:" + title + "\r\n" +
                        "tips描述:" + tips + "\r\n" +
                        "城市代码:" + cityCode + "\r\n" +
                        "类型:" + mType + "\r\n" +
                        "tips类型:" + titleType + "\r\n" +
                        "规则ID:" + sb);

                Log.i(TAG, "标题:" + title + "\r\n" +
                        "tips描述:" + tips + "\r\n" +
                        "城市代码:" + cityCode + "\r\n" +
                        "类型:" + mType + "\r\n" +
                        "tips类型:" + titleType + "\r\n" +
                        "规则ID:" + sb);
            }

            if (pathResult != null && pathResult.getPathCount() > 0) {
                segmentCount = (int) DrivePathAccessor.obtain(pathResult.getPath(0)).getSegmentCount();
                Log.i(TAG, "onNewRoute: segmentCount = " + segmentCount);
            }
        }

        clearRoute();
        drawRoute(AutoOverlayType.RouteOverlayPlan, paths, count);
    }

    @Override
    public void onNewRouteError(int mode, int type, int errorCode, Object externDataPtr, boolean isLocal, boolean isChange) {
        Log.i(TAG, "onNewRouteError: ");
        Log.i(TAG, "type = " + type);
        Log.i(TAG, "errorCode = " + errorCode);
        Log.i(TAG, "isLocal = " + isLocal);
        if( errorCode == 18 || errorCode == 20 || errorCode == 24 || errorCode == 25 )
        {
            CommonUtil.showShortToast("算路失败: 无离线数据");
        }
        else
        {
            CommonUtil.showShortToast("算路失败: type：" + type + "\r\n" +
                    "errorCode:" + errorCode + "\r\n" +
                    "isLocal:" + isLocal);
        }
    }

    private RectDouble unionRect(RectDouble rect1, RectDouble rect2) {
        RectDouble rect = new RectDouble();
        rect.left = Math.min(rect1.left, rect2.left);
        rect.right = Math.max(rect1.right, rect2.right);
        rect.top = Math.min(rect1.top, rect2.top);
        rect.bottom = Math.max(rect1.bottom, rect2.bottom);
        return rect;
    }

    private void initAosRoutePara() {
//        mRouteAosOption = RouteAosOption.obtain();
        if (mRouteAosOption == null) mRouteAosOption = RouteAosOption.obtain();
        mRouteAosOption.setFromX(String.valueOf(startInfo.longitude));
        mRouteAosOption.setFromY(String.valueOf(startInfo.latitude));
        mRouteAosOption.setToX(String.valueOf(endInfo.longitude));
        mRouteAosOption.setToY(String.valueOf(endInfo.latitude));
        Log.i(TAG, "RouteActivity startPoint " + startInfo.longitude + "," + startInfo.latitude);
        Log.i(TAG, "RouteActivity endPoint " + endInfo.longitude + "," + endInfo.latitude);
        mRouteAosOption.setSdkVer(RouteManager.getInstance().getEngineVersion());
        mRouteAosOption.setRouteVer(RouteManager.getInstance().getRouteVersion());

        if (bRestrict == true) {
            mRouteAosOption.setContentOption(0x20 | 0x10000);  //开启限行开关
            Log.i(TAG, "RouteActivity aosRoute restrict on");
        } else {
            mRouteAosOption.setContentOption(0);  //关闭限行开关
            Log.i(TAG, "RouteActivity aosRoute restrict off");
        }

        if (bSetCarPlate) {
            mRouteAosOption.setCarPlate(mCarPlate);
            Log.i(TAG, "RouteActivity aosRoute mCarPlate:" + mCarPlate);
        } else {
            mRouteAosOption.setCarPlate("");
        }

        if( bSetTruck == true )
        {
            mRouteAosOption.setCarType(1);
            mRouteAosOption.setCarHeight(5);
            mRouteAosOption.setCarWidth(4);
            mRouteAosOption.setCarLength(20);
            Log.i(TAG, "RouteActivity aosRoute bSetTruck");
        }
        else
        {
            mRouteAosOption.setCarType(0);
        }
        String policy = "1";
        if(bRouteAvoidCongestion)
        {
            policy = policy + "|2";
        }

        if(bRouteAvoidToll)
        {
            policy = policy + "|4";
        }

        if(bRouteAvoidHighway)
        {
            policy = policy + "|8";
        }

        if (bRouteHighway)
        {
            policy = policy + "|16";
        }

        Log.i(TAG, "RouteActivity aosRoute policy List src：" + policy);

        String policyList[] = policy.split("\\|");

        Log.i(TAG, "RouteActivity aosRoute policy List：" + policyList[0] + "length:" +policyList.length);
        for (int i = 0; i < policyList.length; i++) {
            Log.i(TAG, "initAosRoutePara: " + policyList[i]);
        }
        if( policy.toString().equals("".toString()) )
        {
            policy = "1";
        }

        mRouteAosOption.setPolicy2(policy);
        Log.i(TAG, "RouteActivity aosRoute policy：" + policy);

        mPoiForRequest = POIForRequest.obtain();
        mPoiForRequest.addPoint(POIForRequest.PointTypeStart, startInfo);
        mPoiForRequest.addPoint(POIForRequest.PointTypeEnd, endInfo);
        String strViaPoint = "";
        if (vias.size() > 0) {
            for (int i = 0; i < vias.size(); i++) { //添加途径点
                POIInfo viaPoint = vias.get(i);
                mPoiForRequest.addPoint(POIForRequest.PointTypeVia, viaPoint);
                Log.i(TAG, "RouteActivity viaPoint " + viaPoint.longitude + "," + viaPoint.latitude);
                strViaPoint += viaPoint.longitude + "," + viaPoint.latitude;
                if( i != vias.size() )
                {
                    strViaPoint += "|";
                }
            }
        }
        Log.i(TAG, "RouteActivity viaPoint string: " + strViaPoint);
        mRouteAosOption.setViaPoints(strViaPoint);

        mPathPoints = new PathPoints();
        mPathPoints.mStartPoints = new PathPoint[1];
        mPathPoints.mEndPoints = new PathPoint[1];
        mPathPoints.mStartPoints[0] = new PathPoint();
        mPathPoints.mStartPoints[0].mPos.lat = startInfo.latitude;
        mPathPoints.mStartPoints[0].mPos.lon = startInfo.longitude;
        mPathPoints.mEndPoints[0] = new PathPoint();
        mPathPoints.mEndPoints[0].mPos.lat = endInfo.latitude;
        mPathPoints.mEndPoints[0].mPos.lon = endInfo.longitude;
        if (vias.size() > 0) {
            mPathPoints.mViaPoints = new PathPoint[vias.size()];
            for (int i = 0; i < vias.size(); i++) { //添加途径点
                mPathPoints.mViaPoints[i] = new PathPoint();
                mPathPoints.mViaPoints[i].mPos.lat = vias.get(i).latitude;
                mPathPoints.mViaPoints[i].mPos.lon = vias.get(i).longitude;
            }
        }
    }

    private void clearRoute()
    {
        if( mRouteAosOption != null )
        {
            mRouteAosOption.recycle();
            mRouteAosOption = null;
        }

        if ( mPoiForRequest != null )
        {
            mPoiForRequest.recycle();
            mPoiForRequest = null;
        }

        if( vias != null)
        {
            vias.clear();
        }
        mIBizLayerService.clearRouteOverly(AutoOverlayType.RouteOverlayPlan);
        mIBizLayerService.clearRouteOverly(AutoOverlayType.RouteOverlayTrafficEventTips);
        mIBizLayerService.clearRouteOverly(AutoOverlayType.RouteOverlayTrafficJamTips);
        mIBizLayerService.clearRouteOverly(AutoOverlayType.RouteOverlayTrafficBlockTips);
        mIBizLayerService.clearRouteOverly(AutoOverlayType.GuideOverlayTypeViaPoint);
        mIBizLayerService.clearRouteOverly(AutoOverlayType.GuideOverlayTypeDestPoint);
    }

    private void drawRoute(@AutoOverlayType.AutoOverlayType1 int type, PathResult pathResult, long count) {
        pathAttributes = new ArrayList<BizPathInfo>();
        rect = null;
        for (int i = 0; i < count; i++) {
            BizPathInfo bean = new BizPathInfo();
            bean.mIsDrawPath = true;//是否要绘制
            bean.mIsDrawPathCamera = true;//是否绘制电子眼
            bean.mIsDrawPathTrafficLight = true; //是否要绘制路线上的交通灯
            bean.mIsDrawArrow = false;//是否要绘制转向箭头
            bean.mIsVisible = true;//是否要显示
            bean.mIsTrafficEventOpen = true;//是否要打开交通事件显示开关，默认为开
//                bean.mIsNewRoute = false; //是否是新路径 绘制对比路线时的新路线时用(废弃)
            bean.mPath = pathResult.getPath(i);//路径
            pathAttributes.add(bean);
            //矩形框
            RectDouble outRect = new RectDouble();
            ICalculatePreviewUtil.getOnePathBound(bean.mPath, outRect);
            if (rect == null) {
                rect = outRect;
                Log.i(TAG, "drawRoute: outRect = " + outRect);
            } else {
                rect = unionRect(rect, outRect);
            }
        }

        RouteDrawStyleParam mRouteDrawStyleParam = new RouteDrawStyleParam();
        mRouteDrawStyleParam.mIsNavi = false;
        mRouteDrawStyleParam.mIsOffLine = false;
        mRouteDrawStyleParam.mRoutePathPointScene = RoutePathPointScene.RoutePathPointSceneCimmute;
        mRouteDrawStyleParam.mRouteScene = RouteScene.RouteSceneCimmute;
        if (routePlan) {  //是路径规划页，设置overlay的显示范围  9-20
            mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem.RouteOverlayElemTrafficBlock, 9, 20);
            mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem.RouteOverlayElemAvoidJamPoint, 9, 20);
            mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem.RouteOverlayElemAvoidJamLine, 9, 20);
        }

        mIBizLayerService.setRouteDrawStyleParam(type, mRouteDrawStyleParam); //hmi设置所需的风格
        mIBizLayerService.setPathPoints(type, mPathPoints);   //设起始点
        mIBizLayerService.setSelectedPath(type, 0); //设置选中路线
        mIBizLayerService.setBizPathInfo(type, pathAttributes); //设置路线信息
        int[] segmentCounts = new int[segmentCount];
        for (int i = 0; i < (segmentCount - 1); i++) {
            segmentCounts[i] = i;
        }
        mIBizLayerService.setRouteArrowMapLevel(type, 11); //比例尺11以上的都显示箭头
        mIBizLayerService.setRouteArrowShowSegment(type, segmentCounts);

        mIBizLayerService.updateRouteArrow(type);//更新箭头
        drawRouteCount = (int)count;
        drawRouteIndex = 0;
        drawRouteType = type;
        mIBizLayerService.drawRoute(type); //画路线
    }

    private void switchRoute()
    {
        if( ++drawRouteIndex >= drawRouteCount )
        {
            drawRouteIndex = 0;
        }
        mIBizLayerService.setSelectedPath(drawRouteType, drawRouteIndex); //设置选中路线
        mIBizLayerService.drawRoute(drawRouteType); //画路线
    }

    private void initRoutePara() {

        mRouteOption = RouteOption.obtain();
        mPoiForRequest = POIForRequest.obtain();
        mPoiForRequest.addPoint(POIForRequest.PointTypeStart, startInfo);
        mPoiForRequest.addPoint(POIForRequest.PointTypeEnd, endInfo);
        if (vias.size() > 0) {
            for (int i = 0; i < vias.size(); i++) { //添加途径点
                mPoiForRequest.addPoint(POIForRequest.PointTypeVia, vias.get(i));
            }
        }
        mPathPoints = new PathPoints();
        mPathPoints.mStartPoints = new PathPoint[1];
        mPathPoints.mEndPoints = new PathPoint[1];
        mPathPoints.mStartPoints[0] = new PathPoint();
        mPathPoints.mStartPoints[0].mPos.lat = startInfo.latitude;
        mPathPoints.mStartPoints[0].mPos.lon = startInfo.longitude;
        mPathPoints.mEndPoints[0] = new PathPoint();
        mPathPoints.mEndPoints[0].mPos.lat = endInfo.latitude;
        mPathPoints.mEndPoints[0].mPos.lon = endInfo.longitude;

        if (vias.size() > 0) {
            mPathPoints.mViaPoints = new PathPoint[vias.size()];
            for (int i = 0; i < vias.size(); i++) { //添加途径点
                mPathPoints.mViaPoints[i] = new PathPoint();
                mPathPoints.mViaPoints[i].mPos.lat = vias.get(i).latitude;
                mPathPoints.mViaPoints[i].mPos.lon = vias.get(i).longitude;
            }
        }

        mRouteOption.setPOIForRequest(mPoiForRequest);

        int calcType = 0;

        if ( bRouteAvoidToll && bRouteAvoidCongestion)
        {
            calcType = RouteOptionEnum.RequestRouteTypeTMCFree;
        }
        else if( bRouteAvoidToll )
        {
            calcType = RouteOptionEnum.RequestRouteTypeMoney; //费用优先（尽量避开收费道路）
        }
        else
        {
            calcType = RouteOptionEnum.RequestRouteTypeBest;//返回三条路线，常规、最短、规避拥堵
        }
        mRouteOption.setRequestRouteType(calcType);

        if (bRestrict == true) {
            mRouteOption.setVehicleRestriction(true);  //开启限行开关
            Log.i(TAG, "RouteActivity aosRoute restrict");
        } else {
            mRouteOption.setVehicleRestriction(false);  //关闭限行开关
        }

        if (bSetCarPlate) {
            mRouteOption.setVehicleId(mCarPlate);
            Log.i(TAG, "RouteActivity aosRoute mCarPlate:" + mCarPlate);
        } else {
            mRouteOption.setVehicleId("");
        }

        if( bSetTruck == true )
        {
            RouteManager.getInstance().controlRoute(RouteControlKeyVehicleType,"1");
            RouteManager.getInstance().controlRoute(RouteControlKeyVehicleHeight,"5");
            RouteManager.getInstance().controlRoute(RouteControlKeyVehicleWidth,"4");
            RouteManager.getInstance().controlRoute(RouteControlKeyVehicleLength,"20");
            Log.i(TAG, "RouteActivity aosRoute bSetTruck");
        }
        else
        {
            RouteManager.getInstance().controlRoute(RouteControlKeyVehicleType,"0");
        }

    }

    private void initView() {
        tv_mapVersion = (TextView) findViewById(R.id.tv_map_version_id);
        tv_mapLevel = (TextView) findViewById(R.id.tv_map_level_id);
        et_centerLon = (EditText) findViewById(R.id.et_center_lon_id);
        btn_zoom_in = (Button) findViewById(R.id.btn_zoom_in);
        btn_zoom_in.setOnClickListener(this);
        btn_zoom_out = (Button) findViewById(R.id.btn_zoom_out);
        btn_zoom_out.setOnClickListener(this);
        btn_offline_route = (Button) findViewById(R.id.btn_offline_route);
        btn_offline_route.setOnClickListener(this);
        btn_online_route = (Button) findViewById(R.id.btn_online_route);
        btn_online_route.setOnClickListener(this);
        btn_aos_route = (Button) findViewById(R.id.btn_aos_route);
        btn_aos_route.setOnClickListener(this);
        btn_clear_route = (Button) findViewById(R.id.btn_clear_route);
        btn_clear_route.setOnClickListener(this);
        btn_select_route = (Button) findViewById(R.id.btn_select_route);
        btn_select_route.setOnClickListener(this);
        btn_start_point = (Button) findViewById(R.id.btn_start_point);
        btn_start_point.setOnClickListener(this);
        btn_via_point = (Button) findViewById(R.id.btn_via_point);
        btn_via_point.setOnClickListener(this);
        btn_end_point = (Button) findViewById(R.id.btn_end_point);
        btn_end_point.setOnClickListener(this);
        btn_car_plate = (Button) findViewById(R.id.btn_car_plate);
        btn_car_plate.setOnClickListener(this);
        et_car_plate_id = (EditText) findViewById(R.id.et_car_plate_id);

        btn_route_truck = (ToggleButton)findViewById(R.id.btn_route_truck);
        btn_route_truck.setOnCheckedChangeListener(this);
        btn_route_restrict = (ToggleButton)findViewById(R.id.btn_route_restrict);
        btn_route_restrict.setOnCheckedChangeListener(this);
        cb_route_congestion = (CheckBox)findViewById(R.id.cb_route_congestion);
        cb_route_congestion.setOnCheckedChangeListener(this);
        cb_route_toll = (CheckBox)findViewById(R.id.cb_route_toll);
        cb_route_toll.setOnCheckedChangeListener(this);
        cb_route_avoid_highway = (CheckBox)findViewById(R.id.cb_route_avoid_highway);
        cb_route_avoid_highway.setOnCheckedChangeListener(this);
        cb_route_highway = (CheckBox)findViewById(R.id.cb_route_highway);
        cb_route_highway.setOnCheckedChangeListener(this);
        btn_route_preview = (ToggleButton)findViewById(R.id.btn_route_preview);
        btn_route_preview.setOnCheckedChangeListener(this);
        alertDialog = new AlertDialog.Builder(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_zoom_in:
                mMapView.zoomIn(mMainEngineID);
                break;
            case R.id.btn_zoom_out:
                mMapView.zoomOut(mMainEngineID);
                break;
            case R.id.btn_start_point:
                {
                    Log.i(TAG, "btn_start_point:" + et_centerLon.getText().toString());
                    String lonLat = et_centerLon.getText().toString();
                    if( lonLat.indexOf(",") != -1) {
                        String[] lonLatList = lonLat.split(",", 2);
                        String lon = lonLatList[0];
                        String lat = lonLatList[1];
                        startInfo.longitude = Float.valueOf(lon);
                        startInfo.latitude = Float.valueOf(lat);
                        Log.i(TAG, "btn_start_point1:" + startInfo.longitude + "," + startInfo.latitude);
                        Coord2DInt32 point = mMapView.lonLatToMap(mMainEngineID, startInfo.longitude, startInfo.latitude);
                        mMapView.setMapCenter(mMainEngineID, point.lon, point.lat);
                        CommonUtil.showShortToast("添加起点成功");
                    }
                    else
                    {
                        CommonUtil.showShortToast("添加起点失败，坐标不符合规则");
                    }
                    break;
                }
            case R.id.btn_via_point: {
                Log.i(TAG, "btn_via_point:" + et_centerLon.getText().toString());
                Coord2DDouble vpoint = new Coord2DDouble();
                String lonLat = et_centerLon.getText().toString();
                if (lonLat.indexOf(",") != -1) {
                    String[] lonLatList = lonLat.split(",", 2);
                    String lon = lonLatList[0];
                    String lat = lonLatList[1];
                    vpoint.lon = Double.valueOf(lon);
                    vpoint.lat = Double.valueOf(lat);
                    if (vias.size() < 3) {
                        POIInfo via = new POIInfo();
                        via.longitude = (float) vpoint.lon;
                        via.latitude = (float) vpoint.lat;
                        vias.add(via);
                        Coord2DInt32 point1 = mMapView.lonLatToMap(mMainEngineID, vpoint.lon, vpoint.lat);
                        mMapView.setMapCenter(mMainEngineID, point1.lon, point1.lat);
                        Log.i(TAG, "btn_via_point:" + point1.lon + "," + point1.lat);
                        CommonUtil.showShortToast("添加中途点成功，个数：" + vias.size());
                    } else {
                        CommonUtil.showShortToast("添加中途点失败，中途点数已满最大值3个");
                    }
                } else {
                    CommonUtil.showShortToast("添加途径点失败，坐标不符合规则");
                }
                break;
            }
            case R.id.btn_end_point:
            {
                Log.i(TAG, "btn_end_point:" + et_centerLon.getText().toString());
                String lonLat = et_centerLon.getText().toString();
                if (lonLat.indexOf(",") != -1) {
                    String[] lonLatList = lonLat.split(",", 2);
                    String lon = lonLatList[0];
                    String lat = lonLatList[1];
                    endInfo.longitude = Float.valueOf(lon);
                    endInfo.latitude = Float.valueOf(lat);
                    Coord2DInt32 point2 = mMapView.lonLatToMap(mMainEngineID, endInfo.longitude, endInfo.latitude);
                    mMapView.setMapCenter(mMainEngineID, point2.lon, point2.lat);
                    Log.i(TAG, "startPoint:" + point2.lon + "," + point2.lat);
                    CommonUtil.showShortToast("添加终点成功");
                } else {
                    CommonUtil.showShortToast("添加终点失败，坐标不符合规则");
                }
                break;
            }
            case R.id.btn_offline_route:  //离线算路
                initRoutePara();
                CommonUtil.showShortToast("发起离线算路" );
                mRouteOption.setConstrainCode(0x100);
                RouteManager.getInstance().requestRoute(mRouteOption);
                break;
            case R.id.btn_online_route: //在线算路
                initRoutePara();
                mRouteOption.setConstrainCode(0);
                CommonUtil.showShortToast("发起在线算路" );
                int i = RouteManager.getInstance().requestRoute(mRouteOption);
                break;
            case R.id.btn_aos_route: //aos算路
                initAosRoutePara();
                int code = RouteManager.getInstance().requestAosRoute(mRouteAosOption);
                CommonUtil.showShortToast("发起AOS请求算路");
                Log.i(TAG, "onClick code:" + code);
                break;
            case R.id.btn_clear_route: //清除算路
                clearRoute();
                Log.i(TAG, "onClick code: btn_clear_route");
                break;
            case R.id.btn_car_plate:
                if( bSetCarPlate )
                {
                    bSetCarPlate = false;
                    mCarPlate = "";
                    btn_car_plate.setText("设置车牌");
                }
                else
                {
                    bSetCarPlate = true;
                    mCarPlate = et_car_plate_id.getText().toString();
                    btn_car_plate.setText("取消车牌");
                }
                Log.i(TAG, "onClick code: btn_car_plate:" + mCarPlate);
                break;
            case R.id.btn_select_route:
                switchRoute();
                CommonUtil.showShortToast("切换主选算路" );
                break;
            default:
                break;
        }

        Log.i(TAG, "onClick code:" + v.getId());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId())
        {
            case R.id.btn_route_restrict:
                if( isChecked == true )
                {
                    bRestrict = true;
                }
                else
                {
                    bRestrict = false;
                }
                Log.i(TAG, "onCheckedChanged code:btn_route_restrict:" + bRestrict);
                break;
            case R.id.btn_route_truck:
                if( isChecked == true )
                {
                    bSetTruck = true;
                }
                else
                {
                    bSetTruck = false;
                }
                Log.i(TAG, "onCheckedChanged code:btn_route_truck:" + bSetTruck);
                break;
            case R.id.cb_route_congestion:
                bRouteAvoidCongestion = isChecked;
                Log.i(TAG, "onCheckedChanged code:cb_route_congestion:" + isChecked);
                break;
            case R.id.cb_route_toll:
                if(bRouteHighway ==false)
                {
                    bRouteAvoidToll = isChecked;
                }
                else
                {
                    cb_route_toll.setChecked(false);
                    CommonUtil.showShortToast("避免收费和优先高速不能同时使用" );
                }
                Log.i(TAG, "onCheckedChanged code:cb_route_toll:" + isChecked);
                break;
            case R.id.cb_route_avoid_highway:
                if(bRouteHighway ==false)
                {
                    bRouteAvoidHighway = isChecked;
                }
                else
                {
                    cb_route_avoid_highway.setChecked(false);
                    CommonUtil.showShortToast("避免高速和优先高速不能同时使用" );
                }
                Log.i(TAG, "onCheckedChanged code:cb_route_avoid_highway:" + isChecked);
                break;
            case R.id.cb_route_highway:
                if( bRouteAvoidToll == false || bRouteAvoidHighway == false)
                {
                    bRouteHighway = isChecked;
                }
                else
                {
                    cb_route_highway.setChecked(false);
                    CommonUtil.showShortToast("高速和避免收费不能同时使用" );
                }
                Log.i(TAG, "onCheckedChanged code:cb_route_highway:" + isChecked);
                break;
            case R.id.btn_route_preview:
                if( isChecked )
                {
                    PreviewParam previewParam = new PreviewParam();
                    previewParam.mapBound = rect;
                    previewParam.screenTop = 50;
                    previewParam.screenBottom = 50;
                    previewParam.screenLeft = 20;
                    previewParam.screenRight = 20;
                    previewParam.bUseRect = true;
                    mMapView.showPreview(mMainEngineID, previewParam, true, 500, -1);  //全览路线
                }
                else
                {
                    mMapView.exitPreview(mMainEngineID, true, true);
                }
                break;
            default:
                break;
        }

    }
}
