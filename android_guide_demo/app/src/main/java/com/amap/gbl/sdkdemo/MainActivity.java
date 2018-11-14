package com.amap.gbl.sdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.amap.gbl.sdkdemo.GuideUtils.MapStyleReader;
import com.amap.gbl.sdkdemo.Utils.BitmapUtils;
import com.amap.gbl.sdkdemo.Utils.FileUtils;
import com.amap.gbl.sdkdemo.Utils.ScreenUtils;
import com.amap.gbl.sdkdemo.presenter.InitPresenter;
import com.autonavi.gbl.biz.IBizLayerService;
import com.autonavi.gbl.biz.IBizSceneService;
import com.autonavi.gbl.biz.ICalculatePreviewUtil;
import com.autonavi.gbl.biz.bizenum.AutoCarStyleParam;
import com.autonavi.gbl.biz.bizenum.AutoOverlayType;
import com.autonavi.gbl.biz.bizenum.BizSceneType;
import com.autonavi.gbl.biz.bizenum.RouteOverlayElem;
import com.autonavi.gbl.biz.bizenum.RoutePathPointScene;
import com.autonavi.gbl.biz.bizenum.RouteScene;
import com.autonavi.gbl.biz.bizenum.SceneControlType;
import com.autonavi.gbl.biz.model.BizPathInfo;
import com.autonavi.gbl.biz.model.CarLocation;
import com.autonavi.gbl.biz.model.PathPoint;
import com.autonavi.gbl.biz.model.PathPoints;
import com.autonavi.gbl.biz.model.RouteDrawStyleParam;
import com.autonavi.gbl.common.model.RectDouble;
import com.autonavi.gbl.common.path.drive.accessor.DrivePathAccessor;
import com.autonavi.gbl.common.path.drive.model.POIForRequest;
import com.autonavi.gbl.common.path.drive.model.POIInfo;
import com.autonavi.gbl.common.path.model.option.RouteOptionEnum;
import com.autonavi.gbl.common.path.model.result.PathResult;
import com.autonavi.gbl.common.path.model.result.VariantPath;
import com.autonavi.gbl.guide.model.CrossImageInfo;
import com.autonavi.gbl.guide.model.NaviPath;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.map.MapRoadTip;
import com.autonavi.gbl.map.MapviewModeParam;
import com.autonavi.gbl.map.PreviewParam;
import com.autonavi.gbl.map.ScenicInfo;
import com.autonavi.gbl.map.glinterface.MapLabelItem;
import com.autonavi.gbl.map.listener.MapListener;
import com.autonavi.gbl.map.utils.GLMapStaticValue;
import com.autonavi.gbl.map.utils.GLMapUtil;
import com.autonavi.gbl.route.model.RouteAosOption;
import com.autonavi.gbl.route.observer.IRouteResultObserver;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceDataPath;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;
import com.autonavi.gbl.common.model.WorkPath;
import com.autonavi.gbl.common.model.UserConfig;
import com.autonavi.gbl.pos.model.PosWorkPath;

import java.io.File;
import java.util.ArrayList;

import com.amap.gbl.sdkdemo.platform.CC;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.autonavi.gbl.route.RouteService;
import com.autonavi.gbl.pos.PosService;
import com.autonavi.gbl.guide.GuideService;
import com.autonavi.gbl.route.model.RouteServiceParam;
import com.autonavi.gbl.pos.model.LocModeType;
import com.autonavi.gbl.guide.model.GuideEnum;
import com.autonavi.gbl.pos.model.LocSignData;

import static com.autonavi.gbl.biz.bizenum.SceneControlType.FollowMode;
import static com.autonavi.gbl.pos.model.PosEnum.LocDataGnss;

import com.autonavi.gbl.pos.model.LocGnss;

import android.location.LocationManager;
import android.text.format.Time;

import com.autonavi.gbl.common.model.Coord2DDouble;
import com.autonavi.gbl.user.behavior.BehaviorService;
import com.autonavi.gbl.user.behavior.model.BehaviorServiceParam;
import com.autonavi.gbl.user.behavior.model.ConfigKey;
import com.autonavi.gbl.user.behavior.model.ConfigValue;
import com.autonavi.gbl.user.behavior.observer.BehaviorObserver;
import com.autonavi.gbl.user.behavior.observer.intfc.IBehaviorServiceObserver;
import com.autonavi.gbl.user.syncsdk.model.SyncEventType;
import com.autonavi.gbl.user.syncsdk.model.SyncRet;

import android.location.GpsStatus;
import android.os.SystemClock;
import android.widget.ToggleButton;

public class MainActivity extends Activity
    implements View.OnClickListener, MapListener, IRouteResultObserver, CompoundButton.OnCheckedChangeListener {

    public final static String TAG = MainActivity.class.getSimpleName().toString() + " zjz";
    public MapStyleReader iMapStyleReader;
    public IBizLayerService mIBizLayerService;
    public IBizSceneService mIBizSceneService;
    private Activity mContext;
    private static int initFlg = 0;

    public GLMapView mapView;
    public GlMapSurface glMapSurface;

    private TextView mTextView;
    private TextView mSoundTextView;
    private TextView mCruiseTextView;
    private ServiceDataPath path;
    private int mMainEngineID;
    private String strKey;
    private String strSecurityCode;
    private ToggleButton mBroadcastModeTB;
    private ImageView iv_cross;

    private MyCruiseObserver mCruiseObserver;
    private MyPosLocInfoObserver mPosLocInfoObserver;
    private MyPosParallelRoadObserver mPosParallelRoadObserver;
    private MyPosSwitchParallelRoadObserver mPosSwitchParallelRoadObserver;
    private MySoundPlayObserver mSoundPlayObserver;
    private MyNaviObserver mNaviObserver;

    public RouteService mRouteService;
    public GuideService mGuideService;
    private BehaviorService mBehaviorService;
    private IBehaviorServiceObserver mBehaviorServiceObserver;
    public PosService mPosService;
    private long mRouteInitResult = -9999;
    private long mPosInitResult = -9999;
    private long mGuideInitResult = -9999;

    private LocSignData mLocSignData = new LocSignData();
    private Button mBtn_startnavi;//进入和退出巡航
    boolean isNaving = false;//是否处于巡航状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InitPresenter();//初始化

        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initMap();
        initBiz();
        initCar();
        initObserver();
        initTBT();
        startInfo = new POIInfo();
        endInfo = new POIInfo();
        alertDialog = new AlertDialog.Builder(this);
        Log.i(TAG, "LocationUtil.register");
        LocationUtil.register(this, 500, 0, new LocationUtil.OnLocationChangeListener() {
            /**
             * 获取最后一次保留的坐标
             *
             * @param location 坐标
             */
            @Override
            public void getLastKnownLocation(Location location) {
                //更新参数
                if (mLocSignData == null) { mLocSignData = new LocSignData(); }
                mLocSignData.dataType = LocDataGnss;
                mLocSignData.gnss = new LocGnss();

                switch (location.getProvider()) {
                    case LocationManager.GPS_PROVIDER:
                        mLocSignData.gnss.sourType = 0;
                        break;
                    case LocationManager.NETWORK_PROVIDER:
                        mLocSignData.gnss.sourType = 1;
                        break;
                    default:
                        // 不属于三种类型
                        break;
                }

                Time time = new Time();
                time.set(location.getTime());
                mLocSignData.gnss.year = time.year;
                mLocSignData.gnss.month = time.month + 1;
                mLocSignData.gnss.day = time.monthDay;
                mLocSignData.gnss.hour = time.hour;
                mLocSignData.gnss.minute = time.minute;
                mLocSignData.gnss.second = time.second;
                mLocSignData.gnss.stPt = new Coord2DDouble(0.0, 0.0);
                mLocSignData.gnss.stPtS = new Coord2DDouble();
                mLocSignData.gnss.stPtS.lon = location.getLongitude();
                mLocSignData.gnss.stPtS.lat = location.getLatitude();
                //TODO 不这么写会导致部分机型巡航态下车标不移动的问题，引擎给出具体修复方案之前不能删除
                mLocSignData.gnss.stPt = mLocSignData.gnss.stPtS;
                mLocSignData.gnss.accuracy = location.getAccuracy();
                mLocSignData.gnss.alt = (float)location.getAltitude();
                mLocSignData.gnss.course = location.getBearing();
                mLocSignData.gnss.speed = (float)(location.getSpeed() * 3.6);
                // 产品需求
                if (mLocSignData.gnss.speed >= 30) {//当实际速度>=30km/h时，显示速度=实际速度+3
                    mLocSignData.gnss.speed += 3;
                }

                GpsStatus status = LocationUtil.getGpsStatus(); //取当前状态
                if (status != null) {
                    mLocSignData.gnss.num = status.getMaxSatellites();
                }
                mLocSignData.gnss.tickTime = SystemClock.elapsedRealtime();
                mLocSignData.gnss.isNS = 'N';
                mLocSignData.gnss.isEW = 'E';
                // FIXME: 12/18/15
                int count = 9;
                mLocSignData.gnss.num = count;
                mLocSignData.gnss.hdop = 0.9f;
                mLocSignData.gnss.vdop = 0.9f;
                mLocSignData.gnss.pdop = 0.9f;
                mLocSignData.gnss.status = 'A';
                //TODO:BL2.0 此字段暂未使用; "bl_pos_def.h"描述;
                mLocSignData.gnss.mode = 'N';

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNaving) {//进入巡航状态太更新位置
                            //调用setSignInfo设置定位GPS信号
                            mPosService.setSignInfo(mLocSignData);
                            if (mTextView != null) {
                                StringBuffer sb = new StringBuffer();
                                //sb.append("onLocationChanged \r\n");
                                sb.append("pos:" + mLocSignData.gnss.stPtS.lon + "," + mLocSignData.gnss.stPtS.lat);
                                mTextView.setText(sb);
                            }
                        }
                    }
                });

            }

            /**
             * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
             *
             * @param location 坐标
             */
            @Override
            public void onLocationChanged(Location location) {
                // Log.i(TAG, "onLocationChanged");
                getLastKnownLocation(location);
            }
        });
    }

    private Button btnPreview;
    private Button btnStopNavi;
    private TextView tvNaviInfo;

    private void initView() {
        findViewById(R.id.start_route).setOnClickListener(this);
        mBtn_startnavi = (Button)findViewById(R.id.btn_start_cruise);
        mBtn_startnavi.setOnClickListener(this);
        mTextView = (TextView)findViewById(R.id.tv_text);
        assert (mTextView != null);
        mSoundTextView = (TextView)findViewById(R.id.sound_text);
        assert (mSoundTextView != null);
        mCruiseTextView = (TextView)findViewById(R.id.cruise_text);
        assert (mCruiseTextView != null);
        findViewById(R.id.btn_sim_navi).setOnClickListener(this);
        findViewById(R.id.btn_start_navi).setOnClickListener(this);

        btnPreview = (Button)findViewById(R.id.btn_preview);
        btnPreview.setOnClickListener(this);

        btnStopNavi = (Button)findViewById(R.id.btn_stop_navi);

        mBroadcastModeTB = (ToggleButton)findViewById(R.id.btn_broadcast_model);
        mBroadcastModeTB.setOnCheckedChangeListener(this);
        btnStopNavi.setOnClickListener(this);
        btnStopNavi.setVisibility(View.GONE);

        iv_cross = (ImageView)findViewById(R.id.iv_cross);
        tvNaviInfo = (TextView)findViewById(R.id.tv_naviinfo);
    }

    private void initMap() {
        mapView = new GLMapView(CC.getApplication());
        glMapSurface = (GlMapSurface)findViewById(R.id.mapview);
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

        findViewById(R.id.start_route).setOnClickListener(this);
        mBtn_startnavi = (Button)findViewById(R.id.btn_start_cruise);
        mBtn_startnavi.setOnClickListener(this);
        mTextView = (TextView)findViewById(R.id.tv_text);
        assert (mTextView != null);
        mSoundTextView = (TextView)findViewById(R.id.sound_text);
        assert (mSoundTextView != null);
        mCruiseTextView = (TextView)findViewById(R.id.cruise_text);
        assert (mCruiseTextView != null);
        mapView.setMapListener(this);
        mapView.setRenderFps(15);//设置渲染帧率

        findViewById(R.id.btn_sim_navi).setOnClickListener(this);
        findViewById(R.id.btn_start_navi).setOnClickListener(this);

    }

    private void initBiz() {
        //设置纹理
        iMapStyleReader = new MapStyleReader(this, mapView);
        mIBizLayerService = (IBizLayerService)ServiceMgr.getServiceMgrInstance().createBLService(
            ServiceManagerEnum.BizLayerMultiServiceID);
        int init1 = mIBizLayerService.init(mapView.getNativeMapViewInstance(mMainEngineID), iMapStyleReader);//初始化
        Log.i(TAG, "initIBizLayerService: init = " + init1);
        boolean recycled = mIBizLayerService.isRecycled();
        Log.i(TAG, "initBiz: recycled = " + recycled);
        mIBizSceneService = (IBizSceneService)ServiceMgr.getServiceMgrInstance().createBLService(
            ServiceManagerEnum.BizSceneMultiServiceID);
        int init2 = mIBizSceneService.init(mapView.getNativeMapViewInstance(mMainEngineID), mIBizLayerService);
        Log.i(TAG, "initIBizSceneService: init = " + init2);

        mIBizLayerService.setOverlayVisible(AutoOverlayType.GuideOverlayTypeVectorCross, true);//显示矢量图
    }

    private void initCar() {
        mapView.setMapViewLeftTop(mMainEngineID, ScreenUtils.getScreenWidth(mContext) / 2
            , ScreenUtils.getScreenHeight(mContext) / 2);
        //开启跟随模式，默认关闭
        mIBizSceneService.setSceneControl(FollowMode, true);
        //设置车头向上
        //        mIBizSceneService.setSceneControl(SceneControlType.GuideCarUp, true);
        //设置路线置灰
        mIBizSceneService.setSceneControl(SceneControlType.PassGrey, true);
        //设置动态比例尺
        mIBizSceneService.setSceneControl(SceneControlType.DynamicLevel, true);
        //打开碰撞开关
        mIBizSceneService.setSceneControl(SceneControlType.OverlayCollisionSwitch, true);
        //设置车标可见
        mIBizLayerService.setCarOverlayVisible(true);
        //更新车标样式
        mIBizLayerService.updateCarStyle(AutoCarStyleParam.GuideCarStyle);
        CarLocation carLoc = new CarLocation();
        carLoc.longitude = (float)116.473195;//打开默认处在首开广场
        carLoc.latitude = (float)39.993253;
        //设置车标
        mIBizLayerService.setCarPosition(carLoc);
        MapviewModeParam modeparam = new MapviewModeParam();
        modeparam.bChangeCenter = true;
        modeparam.mode = GLMapStaticValue.EAMapviewMode3D;//3D车上
        mapView.setMapMode(mMainEngineID, modeparam, true, true);
    }

    private void initObserver() {
        mCruiseObserver = new MyCruiseObserver(this, mCruiseTextView);
        mPosLocInfoObserver = new MyPosLocInfoObserver(mTextView);
        mPosParallelRoadObserver = new MyPosParallelRoadObserver(mTextView);
        mPosSwitchParallelRoadObserver = new MyPosSwitchParallelRoadObserver(mTextView);
        mSoundPlayObserver = new MySoundPlayObserver(this, mSoundTextView);
        mNaviObserver = new MyNaviObserver(mTextView,myNaviMessageHandler);
    }

    // 0未开始，1=模拟导航，2=导航
    private int naviType = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_route: {

                requestRoute();
            }
            break;
            case R.id.btn_start_cruise: {
                if (!isNaving) {
                    mIBizSceneService.setScene(BizSceneType.BizSceneTypeCruiseMain);
                    isNaving = mGuideService.startNavi(GuideEnum.NaviTypeCruise);
                } else {
                    isNaving = false;
                    mGuideService.stopNavi();
                }
                mBtn_startnavi.setText(isNaving ? "退出巡航" : "进入巡航");//在巡航状态就切换按钮未退出巡航
                CommonUtil.showShortToast(isNaving ? "进入巡航" : "退出巡航");//执行成功后toast提示当前状态
            }

            break;
            case R.id.btn_sim_navi: {
                naviType = 1;
                if(btnStopNavi.getVisibility() == View.GONE){
                    btnStopNavi.setVisibility(View.VISIBLE);
                }
                startSimNavi();
            }
            break;
            case R.id.btn_start_navi: {
                naviType = 2;
                if(btnStopNavi.getVisibility() == View.GONE){
                    btnStopNavi.setVisibility(View.VISIBLE);
                }
                startNavi();
            }
            break;
            case R.id.btn_preview: {
                setPreView();
            }
            break;
            case R.id.btn_stop_navi: {
                changeNaviStatus();
            }
            break;
            default:
                break;

        }
    }

    private void requestRoute() {
        Log.d(TAG, "开始算路请求");
        initAosRoutePara();
        if (null != mRouteService) {
            int code = mRouteService.requestAosRoute(mRouteAosOption);
            Log.i(TAG, "requestRoute: aoscode = " + code);// 1代表成功
        } else {
            Log.i(TAG, "算路失败");
        }
    }

    private void initRoute() {
        mRouteService = (RouteService)ServiceMgr.getServiceMgrInstance().getBLService(
            ServiceManagerEnum.RouteSingleServiceID);
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
        mRouteInitResult = mRouteService.init(param);
        Log.i(TAG, "initRouteService: route = " + mRouteInitResult);
        mRouteService.addRouteResultObserver(this);  //注册观察者
    }

    private void initPos() {
        mPosService = (PosService)ServiceMgr.getServiceMgrInstance().getBLService(
            ServiceManagerEnum.PosSingleServiceID);
        PosWorkPath posWorkPath = new PosWorkPath();

        posWorkPath.locPath  = IOUtils.getAppSDCardFileDir() + File.separator + "loc";
        posWorkPath.contextPath  = IOUtils.getAppSDCardFileDir() + File.separator + "pos_context";
        File locPath = new File(posWorkPath.locPath );
        if (!locPath.exists()) {
            locPath.mkdir();
        }

        File contextPath = new File(posWorkPath.contextPath );
        if (!contextPath.exists()) {
            contextPath.mkdir();
        }

        LocModeType type = new LocModeType();
        mPosInitResult = mPosService.init(posWorkPath,type);
        Log.i(TAG, "initPos: pos = " + mPosInitResult);
        //0步行  1自驾  2公交车
        mPosService.setMatchMode(1);
        mPosService.addLocInfoObserver(mPosLocInfoObserver, 0);
        mPosService.addParallelRoadObserver(mPosParallelRoadObserver, 0);
        mPosService.addSwitchParallelRoadObserver(mPosSwitchParallelRoadObserver, 0);
        mPosService.signalRecordSwitch(true, false, 1);
    }

    private void initGuide() {
        mGuideService = (GuideService)ServiceMgr.getServiceMgrInstance().getBLService(
            ServiceManagerEnum.GuideSingleServiceID);
        String guideRootDir = IOUtils.getAppSDCardFileDir() + File.separator + "guide";
        CommonUtil.createSubDir(guideRootDir, "cache");
        CommonUtil.createSubDir(guideRootDir, "navi");
        CommonUtil.createSubDir(guideRootDir, "res");
        WorkPath workPath = new WorkPath();
        workPath.cache = guideRootDir + File.separator + "cache";
        workPath.navi = guideRootDir + File.separator + "navi";
        workPath.res = guideRootDir + File.separator + "res";
        UserConfig userConfig = new UserConfig();
        userConfig.deviceID = "1498635cd464d9150b27b7486e436a2f";
        userConfig.userBatch = "0";
        userConfig.userCode = "AN_Amap_IOS_FC";
        mGuideInitResult = mGuideService.init(CC.getApplication(), workPath, userConfig, null);

        mGuideService.addCruiseObserver(mCruiseObserver);
        mGuideService.addNaviObserver(mNaviObserver);
        mGuideService.setSoundPlayObserver(mSoundPlayObserver);
        Log.i(TAG, "initGuideService: guide = " + mGuideInitResult);

        mGuideService.control(GuideEnum.GCKSocolStatus, "1"); //socol开关
        mGuideService.control(GuideEnum.CruiseSpeedCamera, "1");
        mGuideService.control(GuideEnum.GCKAutoFlag, "1");
        mGuideService.control(GuideEnum.GCKCruiseOpenCamera, "1");

        //初始化BehaviorService服务
        mBehaviorService = (BehaviorService)ServiceMgr.getServiceMgrInstance().getBLService(
            ServiceManagerEnum.BehaviorSingleServiceID);
        BehaviorServiceParam param = new BehaviorServiceParam();
        param.dataPath = CC.getApplication().getFilesDir().getAbsolutePath();
        mBehaviorServiceObserver = new BehaviroServiceObserver();
        mBehaviorService.addObserver(mBehaviorServiceObserver);
        mBehaviorService.init(param);
        mBehaviorService.setSDKLogLevel(1);
    }

    private boolean initTBT() {
        initPos();
        initRoute();
        initGuide();
        int i = ServiceMgr.getServiceMgrInstance().bindPos2Guide(mPosService, mGuideService);//定位服务绑定导航服务
        Log.i(TAG, "initTBT: 绑定服务 i = " + i);
        return mPosInitResult == 0 && mRouteInitResult == 0 && mGuideInitResult == 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            LocationUtil.unregister();
            ServiceMgr.getServiceMgrInstance()
                .removeBLService(ServiceManagerEnum.PosSingleServiceID);
            ServiceMgr.getServiceMgrInstance()
                .removeBLService(ServiceManagerEnum.RouteSingleServiceID);
            ServiceMgr.getServiceMgrInstance()
                .removeBLService(ServiceManagerEnum.GuideSingleServiceID);
            if (null != mBehaviorService) {
                mBehaviorService.removeObserver(mBehaviorServiceObserver);
            }
            mapView.detachedFromWindow();
        }
    }

    private RouteAosOption mRouteAosOption;
    private POIInfo startInfo;
    private POIInfo endInfo;
    private ArrayList<POIInfo> vias = new ArrayList<>();
    private Coord2DDouble presspoints;
    private AlertDialog.Builder alertDialog;
    private POIForRequest mPoiForRequest;
    private PathPoints mPathPoints = new PathPoints();
    /**
     * 23D切换,默认3D
     */
    private int map23DMode = 2;

    private void initAosRoutePara() {
        //        mRouteAosOption = RouteAosOption.obtain();
        if (mRouteAosOption == null) { mRouteAosOption = RouteAosOption.obtain(); }

        if (null == startInfo || endInfo == null) {
            Toast.makeText(getApplicationContext(), "请先设置起始点", Toast.LENGTH_LONG).show();
            return;
        }

        mRouteAosOption.setFromX(String.valueOf(startInfo.longitude));
        mRouteAosOption.setFromY(String.valueOf(startInfo.latitude));
        mRouteAosOption.setToX(String.valueOf(endInfo.longitude));
        mRouteAosOption.setToY(String.valueOf(endInfo.latitude));
        Log.i(TAG, "RouteActivity startPoint " + startInfo.longitude + "," + startInfo.latitude);
        Log.i(TAG, "RouteActivity endPoint " + endInfo.longitude + "," + endInfo.latitude);
        mRouteAosOption.setSdkVer(RouteService.getEngineVersion());
        mRouteAosOption.setRouteVer(RouteService.getRouteVersion());
        //if (eta) {
        //    mRouteAosOption.setContentOption(0x20 | 0x10000);  //开启限行开关
        //} else {
        //    mRouteAosOption.setContentOption(0);  //关闭限行开关
        //}
        //
        //if (carid) {
        //    mRouteAosOption.setCarPlate("闽D309AK");
        //} else {
        //    mRouteAosOption.setCarPlate("");
        //}

        mPoiForRequest = POIForRequest.obtain();
        mPoiForRequest.addPoint(POIForRequest.PointTypeStart, startInfo);
        mPoiForRequest.addPoint(POIForRequest.PointTypeEnd, endInfo);
        mPathPoints = new PathPoints();
        mPathPoints.mStartPoints = new PathPoint[1];
        mPathPoints.mEndPoints = new PathPoint[1];
        mPathPoints.mStartPoints[0] = new PathPoint();
        mPathPoints.mStartPoints[0].mPos.lat = startInfo.latitude;
        mPathPoints.mStartPoints[0].mPos.lon = startInfo.longitude;
        mPathPoints.mEndPoints[0] = new PathPoint();
        mPathPoints.mEndPoints[0].mPos.lat = endInfo.latitude;
        mPathPoints.mEndPoints[0].mPos.lon = endInfo.longitude;
    }

    @Override
    public void onScreenShotFinished(int i, long l) {

    }

    @Override
    public void onScreenShotFinished(int i, Bitmap bitmap) {

    }

    @Override
    public void onScreenShotFinished(int i, String s) {

    }

    @Override
    public void onMotionEvent(int i, int i1, int i2, int i3) {

    }

    @Override
    public void onMoveBegin(int i, int i1, int i2) {

    }

    @Override
    public void onMoveEnd(int i, int i1, int i2) {

    }

    @Override
    public void onMove(int i, int i1, int i2) {

    }

    @Override
    public void onLongPress(int engineId, int px, int py) {
        Log.i(TAG, "onLongPress: px = " + px + ", py = " + py);
        presspoints = mapView.mapToLonLat(engineId, mapView.fromPixels(engineId, px, py).x,
            mapView.fromPixels(engineId, px, py).y);
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
                                startInfo.longitude = (float)presspoints.lon;
                                startInfo.latitude = (float)presspoints.lat;
                                Log.i(TAG, "lon = " + startInfo.longitude + ",lat = " + startInfo.latitude);
                                break;
                            case 1: //途经点
                                POIInfo via = new POIInfo();
                                via.longitude = (float)presspoints.lon;
                                via.latitude = (float)presspoints.lat;
                                vias.add(via);
                                break;
                            case 2: //终点
                                endInfo.longitude = (float)presspoints.lon;
                                endInfo.latitude = (float)presspoints.lat;
                                break;
                        }

                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onDoublePress(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean onSinglePress(int i, int i1, int i2, boolean b) {
        return false;
    }

    @Override
    public void onSliding(int i, float v, float v1) {

    }

    @Override
    public void onMapCenterChanged(int i, int i1, int i2) {

    }

    @Override
    public void onMapSizeChanged(int i) {

    }

    @Override
    public void onMapLevelChanged(int i, boolean b) {

    }

    @Override
    public void onMapModeChanged(int i, int i1) {

    }

    @Override
    public void onMapPreviewEnter(int i) {

    }

    @Override
    public void onMapPreviewExit(int i) {

    }

    @Override
    public void onClickLabel(int i, MapLabelItem[] mapLabelItems) {

    }

    @Override
    public void onClickBlank(int i, float v, float v1) {

    }

    @Override
    public void onRenderMap(int i, int i1) {

    }

    @Override
    public void onRealCityAnimationFinished(int i) {

    }

    @Override
    public void onMapAnimationFinished(int i, int i1) {

    }

    @Override
    public void onRouteBoardData(int i, MapRoadTip[] mapRoadTips) {

    }

    @Override
    public void onMapHeatActive(int i, boolean b) {

    }

    @Override
    public void onScenicActive(int i, ScenicInfo scenicInfo) {

    }

    @Override
    public void onMotionFinished(int i, int i1) {

    }

    @Override
    public void onPreDrawFrame(int i) {

    }

    private NaviPath naviPath;
    private PathResult paths;

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
        Log.i(TAG, " onNewRoute: ");

        //if(naviPath == null || naviPath.paths == null || naviPath.point == null)
        //{
        //    return;
        //}

        paths = pathResult;
        if (paths == null) {
            return;
        }
        naviPath = new NaviPath();
        int count = pathResult.getPathCount();
        CommonUtil.showShortToast("算路成功" + "\r\n" + "路线个数:" + count);
        naviPath.paths = new VariantPath[count];
        for (int i = 0; i < count; i++) {
            naviPath.paths[i] = pathResult.getPath(i);
        }
        naviPath.point = mPoiForRequest;
        //if (yaw) {
        //    naviPath.type = RouteOptionEnum.RouteTypeYaw;   //偏航使用偏航重算
        //} else {
        naviPath.type = RouteOptionEnum.RouteTypeCommon;   //第一次算路使用common
        //}
        naviPath.strategy = 0;

        mGuideService.setNaviPath(naviPath, 0);

        if (count == 1) {  //只有一条路线
            //矩形框
            int onePathBound = ICalculatePreviewUtil.getOnePathBound(pathResult.getPath(0), rect);
            Log.i(TAG, "onNewRoute: onePathBound = " + onePathBound);
            Log.i(TAG, "onNewRoute: rect = " + rect);
        } else { //多条路线
            int pathResultBound = ICalculatePreviewUtil.getPathResultBound(pathResult, rect);
            Log.i(TAG, "onNewRoute: pathResultBound = " + pathResultBound);
            Log.i(TAG, "onNewRoute: rect = " + rect);
            // Log.i(TAG, "onNewRoute: " + rect.left + "," + rect.right + "," + rect.top + "," + rect.bottom);
        }
        if (pathResult != null && pathResult.getPathCount() > 0) {
            segmentCount = (int)DrivePathAccessor.obtain(pathResult.getPath(0)).getSegmentCount();
            Log.i(TAG, "onNewRoute: segmentCount = " + segmentCount);
        }
        drawRoute(AutoOverlayType.RouteOverlayPlan, pathResult, count);

    }

    int segmentCount;

    private ArrayList<BizPathInfo> pathAttributes;

    private void drawRoute(@AutoOverlayType.AutoOverlayType1 int type, PathResult pathResult, long count) {

        pathAttributes = new ArrayList<BizPathInfo>();
        rect = null;
        for (int i = 0; i < count; i++) {
            BizPathInfo bean = new BizPathInfo();
            bean.mIsDrawPath = true;//是否要绘制
            bean.mIsDrawPathCamera = true;//是否绘制电子眼
            bean.mIsDrawPathTrafficLight = true; //是否要绘制路线上的交通灯
            bean.mIsDrawArrow = true;//是否要绘制转向箭头
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
        //if (routePlan) {  //是路径规划页，设置overlay的显示范围  9-20
        //    mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem
        // .RouteOverlayElemTrafficBlock, 9, 20);
        //    mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem
        // .RouteOverlayElemAvoidJamPoint, 9, 20);
        //    mIBizLayerService.setRouteOverlayElemDisplayScale(AutoOverlayType.RouteOverlayPlan, RouteOverlayElem
        // .RouteOverlayElemAvoidJamLine, 9, 20);
        //}
        mIBizLayerService.clearOverlay(type);
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
        mIBizLayerService.drawRoute(type); //画路线
    }

    @Override
    public void onNewRouteError(int mode, int type, int errorCode, Object externDataPtr, boolean isLocal,
                                boolean isChange) {
        Log.i(TAG, " onNewRouteError: ");
    }

    private RectDouble unionRect(RectDouble rect1, RectDouble rect2) {
        RectDouble rect = new RectDouble();
        rect.left = Math.min(rect1.left, rect2.left);
        rect.right = Math.max(rect1.right, rect2.right);
        rect.top = Math.min(rect1.top, rect2.top);
        rect.bottom = Math.max(rect1.bottom, rect2.bottom);
        return rect;
    }

    private boolean startSimNavi() {
        mIBizSceneService.setScene(BizSceneType.BizSceneTypeSimulate);
        //LocationInstrument.getInstance().setStopLocation(false);
        //                        mapView.setMapModeAndStyle(mMainEngineID, 0, GLMapView.MAPVIEW_TIME_DAY, GLMapView
        // .MAPVIEW_STATE_NAVI_CAR);
        return mGuideService.startNavi(GuideEnum.NaviTypeSimulation);
    }

    private boolean startNavi() {
        mIBizSceneService.setScene(BizSceneType.BizSceneTypeNavi);
        //LocationInstrument.getInstance().setStopLocation(true);
        //                        mapView.setMapModeAndStyle(mMainEngineID, 0, GLMapView.MAPVIEW_TIME_DAY, GLMapView
        // .MAPVIEW_STATE_NAVI_CAR);
        return mGuideService.startNavi(GuideEnum.NaviTypeGPS);
    }

    private boolean isPreview;
    private RectDouble rect = new RectDouble();

    private void setPreView() {

        isPreview = !isPreview;
        if (isPreview) {
            PreviewParam previewParam = new PreviewParam();
            previewParam.mapBound = rect;
            previewParam.bUseRect = true;
            mapView.showPreview(mMainEngineID, previewParam, true, 500, -1);  //全览路线
        } else {
            mapView.exitPreview(mMainEngineID, true, true);
        }
        btnPreview.setText(isPreview ? "全览路线开启" : "全览路线关闭");

    }

    private void setBroadcastMode(boolean isChecked) {
        ConfigValue cast_simple = new ConfigValue();
        cast_simple.intValue = isChecked ? 1 : 2;//经典简洁：1；新手详细：2
        int ret = mBehaviorService.setConfig(ConfigKey.ConfigKeyBroadcastMode, cast_simple, 1);
        Log.i("yyc", "setBroadcastMode ret =" + ret);
        String content = isChecked ? "经典简洁" : "新手详细";
        if (ret == 0) {
            CommonUtil.showShortToast(content + "切换成功");
        } else {
            CommonUtil.showShortToast(content + "切换失败");
        }
        ConfigValue cast_value = mBehaviorService.getConfig(ConfigKey.ConfigKeyBroadcastMode);
        Log.i("yyc", "cast_value = " + cast_value.intValue);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        setBroadcastMode(b);
    }

    private class BehaviroServiceObserver implements IBehaviorServiceObserver {
        @Override
        public void notify(@SyncEventType.SyncEventType1 int var1, @SyncRet.SyncRet1 int var2) {
            String content = "BehaviorService初始化回调状态:\nvar1=  " + var1 + ";var2 = " + var2;
            Log.d("yyc", content);
            if (var1 == 10) {
                CommonUtil.showShortToast("BehaviorService init done");
            }

        }
    }

    private boolean isStop = false;

    private void changeNaviStatus() {

        isStop = !isStop;

        if (isStop) {
            boolean b1 = mGuideService.stopNavi();
            CommonUtil.showShortToast(b1 ? "停止导航成功" : "停止导航失败");
        } else {
            requestRoute();
            boolean b1 = naviType == 1 ? startSimNavi() : startNavi();
            CommonUtil.showShortToast(b1 ? "恢复导航成功" : "恢复导航失败");
        }
        btnStopNavi.setText(isStop ? "重新导航" : "停止导航");

    }

    public static final int MESSAGE_SHOW_CROSS_IMAGE = 1;
    public static final int MESSAGE_HIDE_CROSS_IMAGE = 2;
    public static final int MESSAGE_SHOW_SHOW_NAVIINFO = 33;

    private Handler myNaviMessageHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int what = message.what;
            switch (what) {
                case MESSAGE_SHOW_CROSS_IMAGE:
                    onShowCrossImage((CrossImageInfo)message.obj);
                    break;
                case MESSAGE_HIDE_CROSS_IMAGE:
                    onHideCrossImage(message.arg1);
                    break;
                case MESSAGE_SHOW_SHOW_NAVIINFO:
                    tvNaviInfo.setText((String)message.obj);
                default:
                    break;

            }
            return false;
        }
    });

    private void onShowCrossImage(CrossImageInfo info) {

        StringBuffer buffer = new StringBuffer();
        if (info != null) {

            buffer.append("onShowCrossImage: 背景ID = " + info.crossImageID + "\r\n");
            Log.i(TAG, "onShowCrossImage: 背景ID = " + info.crossImageID);

            buffer.append("onShowCrossImage: 路口大图类型 = " + info.type + "\r\n");
            Log.i(TAG, "onShowCrossImage: 路口大图类型 = " + info.type);

            buffer.append("onShowCrossImage: 是否只有矢量图显示 = " + info.isOnlyVector + "\r\n");
            Log.i(TAG, "onShowCrossImage: 是否只有矢量图显示 = " + info.isOnlyVector);

            buffer.append("onShowCrossImage: 路口大图二进制数据 = " + info.dataBuf + "\r\n");
            Log.i(TAG, "onShowCrossImage: 路口大图二进制数据 = " + info.dataBuf);

            buffer.append("onShowCrossImage: 栅格路口大图二进制数据 = " + info.arrowDataBuf + "\r\n");
            Log.i(TAG, "onShowCrossImage: 栅格路口大图二进制数据 = " + info.arrowDataBuf); //箭头

            FileUtils.writeToFile(buffer.toString(), "qalog.log", true);
            if (info.type == 3 || info.type == 4) {   //矢量图或者三维图
                int ret = mIBizLayerService.showCross(info);
                Log.i("yyc", "CrossImage showCross ret = " + ret);
            } else if (info.type == 1) {  //栅格图
                Bitmap dataBuf = BitmapFactory.decodeByteArray(info.dataBuf, 0, info.dataBuf.length);
                Bitmap arrowDataBuf = BitmapFactory.decodeByteArray(info.arrowDataBuf, 0, info.arrowDataBuf.length);
                iv_cross.setImageBitmap(BitmapUtils.eraseArrayBackground(arrowDataBuf, dataBuf));
            }
        }

    }

    private void onHideCrossImage(int type) {
        FileUtils.writeToFile("onHideCrossImage: type = " + type, "qalog.log", true);
        if (type == 3 || type == 4) {
            mIBizLayerService.hideCross(type);
        } else if (type == 1) {  //栅格图
            iv_cross.setImageBitmap(null);
        }

    }
}
