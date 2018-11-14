package com.amap.gbl.sdkdemo.presenter;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.amap.gbl.sdkdemo.platform.CC;
import com.amap.gbl.sdkdemo.platform.RestPlatformInterface;
import com.autonavi.gbl.alc.ALCManager;
import com.autonavi.gbl.alc.model.ALCGroup;
import com.autonavi.gbl.alc.model.ALCLogLevel;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.BLInitParam;
import com.autonavi.gbl.servicemanager.model.BaseInitParam;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InitPresenter {
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

    private static final String TAG = "InitPresenter";
    public final static String DATA_DIR_NAME = "sdkDemo3x";
    public final static String pcCfgFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + DATA_DIR_NAME;
    private RestPlatformInterface pPlatformUtil = new RestPlatformInterface();

    public InitPresenter() {
        initMngService();
    }


    /**
     * 初始化
     * @return
     */
    private int initMngService() {

        String pcLogPath = pcCfgFilePath + "/bllog";//sdcard/amapauto20/bllog
        Log.i(TAG, "BLPATH:pcCfgFilePath: " + pcCfgFilePath);
        Log.i(TAG, "BLPATH:pcLogPath: " + pcLogPath);
        File f = new File(pcLogPath);//sdcard/amapauto20/bllog
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
            Log.i(TAG, "initMngService: mkdirs = " + mkdirs);
        }

        f = new File(pcCfgFilePath, "GNaviConfig.xml");//sdcard/amapauto20/GNaviConfig.xml
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
        param.logLevel = ALCLogLevel.LogLevelNone; //打印全部日志
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

}
