package com.amap.gbl.sdkdemo.platform;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * demo 中抽出util类, 以便其它位置使用
 * Created by liaoqiuhua on 16/9/6.
 */
public class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();

    private static Toast mToast;
    private static Handler mHandler = new Handler();

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
                else
                    mToast.setText(content);
                ;
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface JniCallMethod{

    }

}

