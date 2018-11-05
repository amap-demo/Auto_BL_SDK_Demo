package com.amap.gbl.sdkdemo.platform;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by yaozhen on 16/6/23.
 */
public class IOUtils {

    private static final String PROJECT_DIR_NAME = "autotest";

    public static byte[] decodeAssetResData(Context context, String resName) {
        // on 1.6 later

        AssetManager assetManager = context.getAssets();

        InputStream is;
        try {
            is = assetManager.open(resName);
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            byte[] bufferByte = new byte[1024];
            int l;
            while ((l = is.read(bufferByte)) > -1) {
                bout.write(bufferByte, 0, l);
            }
            byte[] rBytes = bout.toByteArray();
            bout.close();
            is.close();
            return rBytes;

        } catch (IOException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static String getFileMD5(String fileName) {
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(fileName);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            byte[] md5Bytes = md5.digest();
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = (md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fileStream);
        }
    }

    public static String getFileMD5(File file) {
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            fileStream.close();
            byte[] md5Bytes = md5.digest();
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = (md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fileStream);
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public static String getByteArrayMD5(byte[] byteArray) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteArray);
            byte[] md5Bytes = md5.digest();
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = (md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 取高德地图在sd卡中的目录
     */
    public static String getAppSDCardFileDir() {
        // 得到存储卡路径
        File sdDir = null;
        boolean sdCardExist = getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡
        // 或可存储空间是否存在
        if (sdCardExist) {
            File fExternalStorageDirectory = Environment
                    .getExternalStorageDirectory();
            sdDir = new File(fExternalStorageDirectory, "auto_bl_demo"); // 错误日志存储到SD卡autonavi目录下
            if (!sdDir.exists()) {
                sdDir.mkdir();
            }
        }
        if (sdDir == null)
            return null;

        return sdDir.toString();
    }

    //fix #8135094
    private static String getExternalStorageState(){
        String externStete = "";
        try{
            externStete = Environment.getExternalStorageState();
        }catch (Exception e){
        }
        return externStete;
    }
}
