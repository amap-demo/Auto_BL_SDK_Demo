package com.amap.gbl.sdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.amap.gbl.sdkdemo.MyApplication;
//import com.autonavi.auto.test.SearchActivity;
//import com.autonavi.auto.test.control.MarkUtils;
//import com.autonavi.auto.test.control.NaviConstant;
//import com.autonavi.auto.test.presenter.InitConfigPresenter;
import com.autonavi.gbl.biz.IBizLayerService;
import com.autonavi.gbl.biz.bizenum.AutoOverlayType;
import com.autonavi.gbl.biz.bizenum.PolygonPointType;
import com.autonavi.gbl.biz.model.BizPointBaseData;
import com.autonavi.gbl.biz.model.BizPointMarker;
import com.autonavi.gbl.biz.model.BizPolygonData;
import com.autonavi.gbl.biz.model.BizPolygonPointData;
import com.autonavi.gbl.biz.model.EaglStyle;
import com.autonavi.gbl.common.model.Coord3DDouble;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.MapColorParam;
import com.autonavi.gbl.map.MapViewParam;
import com.autonavi.gbl.map.utils.GLMapUtil;
//import com.autonavi.server.aos.serverkey;
import com.amap.gbl.sdkdemo.platform.CC;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * demo 中抽出util类, 以便其它位置使用
 * Created by liaoqiuhua on 16/9/6.
 */
public class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();
    private static Toast mToast;
    private static Handler mHandler = new Handler();

    public static Resources getResources() {
        return MyApplication.getAppContext().getResources();
    }

    public static byte[] readAssetsFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        InputStream input = null;
        ByteArrayOutputStream out = null;
        try {
            input = CC.getApplication().getAssets().open(fileName);
            out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = input.read(buff, 0, 1024)) > 0) {
                out.write(buff, 0, rc);
            }
            byte[] bytes = out.toByteArray();
            Log.d("AndroidMap", "readAssetsFile: " + fileName + ", len=" + bytes.length);
            return bytes;
        } catch (Exception e) {
            Log.w("AndroidMap", "readAssetsFile Exception: " + e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static void copyAssetFile(String assetFile, String destFilePath) {
        if (TextUtils.isEmpty(destFilePath)) {
            return;
        }
        File file = new File("/mnt/sdcard" + destFilePath);
        String md5 = null;
        if (file.exists()) {
            md5 = IOUtils.getFileMD5(file.getAbsolutePath());

        } else {
            String dir = destFilePath.substring(0, destFilePath.lastIndexOf("/"));
            File dirFile = new File("/mnt/sdcard" + dir);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                boolean res = dirFile.mkdir();
                //Log.d("AndroidMap", "mkdir " + dir + " " + res);
            }
            try {
                boolean res = file.createNewFile();
                //Log.d("AndroidMap", "createNewFile " + destFilePath + " " + res);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] data = IOUtils.decodeAssetResData(CC.getApplication(), assetFile);
        if (data != null) {
            if (md5 != null) {
                String md5_ = IOUtils.getByteArrayMD5(data);
                if (md5_ != null && !md5_.equalsIgnoreCase(md5)) {
                    file.delete();
                }
            }
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(data);
                //Log.d("AndroidMap", "copyAssetFile: " + assetFile + " to " + destFilePath + " success");
            } catch (Exception e) {
                Log.w("AndroidMap", "copyAssetFile: " + assetFile + " to " + destFilePath + " fail, " + e.getMessage());
            } finally {
                IOUtils.closeQuietly(fout);
            }
        }
    }

/*
    public static String getSign(String params) {
        if (params == null) {
            params = "";
        }
        String signStr = serverkey.getAosChannel() + params + "@"
                + serverkey.getAosKey();
        try {
            byte[] idByte = signStr.getBytes();
            return serverkey.sign(idByte);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSign(List<String> signParam) {
        String signStr = "";
        if (signParam != null && signParam.size() > 0) {
            for (String s : signParam) {
                if (s != null) {
                    signStr += s;
                }
            }
        }
        return getSign(signStr);
    }
*/


    public static float getDensity() {
        return CC.getApplication().getResources().getDisplayMetrics().density;
    }

    public static int getDensityDpi() {
        return CC.getApplication().getResources().getDisplayMetrics().densityDpi;
    }

    public static void createSubDir(String rootDir, String subDirName) {
        String guideRootDir = rootDir;
        File guideRootFile = new File(guideRootDir);
        if (!guideRootFile.exists() || !guideRootFile.isDirectory()) {
            guideRootFile.mkdir();
        }
        File subDirFile = new File(guideRootDir + File.separator + subDirName);
        if (!subDirFile.exists() || !subDirFile.isDirectory()) {
            subDirFile.mkdir();
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface JniCallMethod {

    }

    public static int getResColor(@ColorRes int ColorID) {
        return getResources().getColor(ColorID);
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

    /**
     * 获得当前时间 格式为"yyyy-MM-dd  HH:mm:ss"
     *
     * @param time 时间戳,单位:毫秒
     */
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 获取指定范围的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    //获取设备号
    public static String getDeviceID(Context context) {
        String deviceId = "0";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //deviceId = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

}

