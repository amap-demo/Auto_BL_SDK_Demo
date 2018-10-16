package com.amap.gbl.sdkdemo.platform;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CC {

    private static Application app;
    public static void setApplication(Application app){
        CC.app = app;
    }
    /**
     * 获取当前运用 application对象,任何地方，任何时间都可以直接获取到，不存在失败的问题。
     *
     * @return
     */
    public static Application getApplication() {
        return app;
    }

    public static void copyAssetFile(String assetFile, String rootDirPath, String destFileName) {
        if (TextUtils.isEmpty(destFileName)) {
            return;
        }
        File file = new File(rootDirPath + destFileName);
        String md5 = null;
        if (file.exists()) {
            md5 = IOUtils.getFileMD5(file.getAbsolutePath());

        }else{
            File dirFile = new File(rootDirPath);
            if(!dirFile.exists() || !dirFile.isDirectory())
            {
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
            if(md5 != null) {
                String md5_ = IOUtils.getByteArrayMD5(data);
                if (md5_ != null && !md5_.equalsIgnoreCase(md5)) {
                    file.delete();
                }
            }
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(data);
                //Log.d("AndroidMap", "copyAssetFile: " + assetFile + " to " + destFileName + " success");
            } catch (Exception e) {
                Log.w("AndroidMap", "copyAssetFile: " + assetFile + " to " + destFileName + " fail, " + e.getMessage());
            } finally {
                IOUtils.closeQuietly(fout);
            }
        }
    }
}
