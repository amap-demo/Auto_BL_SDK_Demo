package com.amap.gbl.sdkdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.common.model.UserConfig;
import com.autonavi.gbl.common.model.WorkPath;
import com.autonavi.gbl.data.MapDataService;
import com.autonavi.gbl.data.model.DataType;
import com.autonavi.gbl.data.model.DownLoadMode;
import com.autonavi.gbl.data.model.InitConfig;
import com.autonavi.gbl.data.model.OperationErrCode;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.GlMapSurface;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.guide.GuideService;
import com.autonavi.gbl.route.RouteService;
import com.autonavi.gbl.route.model.RouteServiceParam;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceDataPath;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;
import com.autonavi.gbl.data.observer.intfc.IDataInitObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName().toString();
    private static Toast mToast;
    private static Handler mHandler = new Handler();

    private TextView mTextView;
    private Button buttonMapDataService;
    private Button buttonVoiceDataService;

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
    public final static String DATA_DIR_NAME = "sdkDemo4";
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
    private int mMainEngineID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManagerService();
        setContentView(R.layout.activity_main);
        //initMap();
        initRouteService();
        initGuideService();

        buttonMapDataService = (Button) findViewById(R.id.button_map_data_service);
        buttonMapDataService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMapDataService();
            }
        });
        buttonVoiceDataService = (Button) findViewById(R.id.button_voice_data_service);
        buttonVoiceDataService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initVoiceDataService();
            }
        });
    }
    @Override
    protected void onDestroy() {
        unInitManagerService();
        super.onDestroy();
    }



    private int initManagerService() {

        String pcLogPath = pcCfgFilePath + "/bllog";//sdcard/sdkDemo4/bllog
        Log.i(TAG, "BLPATH:pcCfgFilePath: " + pcCfgFilePath);
        Log.i(TAG, "BLPATH:pcLogPath: " + pcLogPath);
        File f = new File(pcLogPath);//sdcard/sdkDemo4x/bllog
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
            Log.i(TAG, "initMngService: mkdirs = " + mkdirs);
        }

        f = new File(pcCfgFilePath, "GNaviConfig.xml");//sdcard/sdkDemo4/GNaviConfig.xml
        Context context = CC.getApplication();
        if (!f.exists()) {
            copyAssets(context, f, "GNaviConfig.xml");
        }

        f = new File(pcCfgFilePath, "offline_conf/all_city_compile.json");
        if (!f.exists()) {
            copyAssets(context, f, "offline_conf/all_city_compile.json");
        }

        f = new File(pcCfgFilePath, "res/global.db");
        if (!f.exists()) {
            copyAssets(context, f, "res/global.db");
        }

        f = new File(pcCfgFilePath, "voice_conf/bl_voice.db");
        if (!f.exists()) {
            copyAssets(context, f, "voice_conf/bl_voice.db");
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
        String publicServerSecurityCode = "c60a***6685586***218a1edb***04b1";

        //测网联调服务器类型;
        int testServerType = ServiceManagerEnum.AosDevelopmentEnv;
        //测网联调服务器临时渠道密钥Key,需要向高德产品申请获取
        String testServerKey            = "d854***d9799cb***56d80d2b***0c6b";
        //测网联调服务器临时安全码，,需要向高德产品申请获取
        String testServerSecurityCode   = "c60a***6685586***218a1edb***04b2";

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
        //配置云+端数据
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

    private void unInitManagerService() {
        ServiceMgr.getServiceMgrInstance().unInitBL();
        ServiceMgr.getServiceMgrInstance().unInitBaseLibs();
        Log.i(TAG, "unInitBL");
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

    private void initRouteService() {
        RouteService routeService = (RouteService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.RouteSingleServiceID);
        Log.i(TAG, "initRouteService: mRouteService = " + routeService);
        RouteServiceParam param = new RouteServiceParam();
        String routeRootDir = pcCfgFilePath + File.separator + "route";
        param.mPath = new WorkPath();
        param.mPath.cache = routeRootDir + File.separator + "cache";
        File dest = new File(param.mPath.cache);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        param.mPath.navi = routeRootDir + File.separator + "navi";
        dest = new File(param.mPath.navi);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        param.mPath.res = routeRootDir + File.separator + "res";
        dest = new File(param.mPath.res);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        param.mConfig = new UserConfig();
        param.mConfig.deviceID = Build.ID;
        param.mConfig.userBatch = Build.BOARD;
        param.mConfig.userCode = Build.USER;
        long init = routeService.init(param);
        Log.i(TAG, "initRouteService: init = " + init);
    }

    private void initGuideService() {
        GuideService guideService = (GuideService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.GuideSingleServiceID);
        String guideRootDir = pcCfgFilePath + File.separator + "guide";

        WorkPath workPath = new WorkPath();
        workPath.cache = guideRootDir + File.separator + "cache";
        File dest = new File(workPath.cache);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        workPath.navi = guideRootDir + File.separator + "navi";
        dest = new File(workPath.navi);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        workPath.res = guideRootDir + File.separator + "res";
        dest = new File(workPath.res);
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }

        UserConfig userConfig = new UserConfig();
        userConfig.deviceID = Build.ID;
        userConfig.userBatch = Build.BOARD;
        userConfig.userCode = Build.USER;

        int res = guideService.init(CC.getApplication(), workPath, userConfig, null);
        Log.i(TAG, "initGuideService: res = " + res);
    }

    private  class ServiceIntent extends Intent implements IDataInitObserver{
        public ServiceIntent(Context packageContext, Class<?> cls)
        {
            super(packageContext,cls);
        }

        @Override
        public void onInit(@DownLoadMode.DownLoadMode1 int var1, @DataType.DataType1 int var2, @OperationErrCode.OperationErrCode1 int var3)
        {
            startActivity(this);
        }
    }
    private void initMapDataService() {

        ServiceIntent intent = new ServiceIntent(MainActivity.this, ServiceActivity.class);
        MapDataService mapDataService = (MapDataService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.MapDataSingleServiceID);
        InitConfig config = new InitConfig();
        //设置json文件路径，例如：
        config.strStoredPath = "";
        config.strConfigfilePath =pcCfgFilePath + File.separator + "offline_conf/";
        int res = mapDataService.init(config,intent);
        Log.i(TAG, "initIMapDataService: res = " + res);
    }

    public static void showShortToast(final int stringId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null)
                    mToast = Toast.makeText(CC.getApplication(), stringId, Toast.LENGTH_SHORT);
                else mToast.setText(stringId);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }

    public static void showShortToast(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null)
                    mToast = Toast.makeText(CC.getApplication(), content, Toast.LENGTH_SHORT);
                else mToast.setText(content);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }
}
