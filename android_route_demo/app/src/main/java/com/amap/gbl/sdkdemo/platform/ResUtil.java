package com.amap.gbl.sdkdemo.platform;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;


import java.io.IOException;
import java.io.InputStream;

public class ResUtil {

    public static byte[] decodeAssetResData(Context context,
                                            String resName) {
        // on 1.6 later

        AssetManager assetManager = context.getAssets();

        InputStream is;
        try {
            is = assetManager.open(resName);
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            byte[] bufferByte = new byte[1024];
            int l = -1;
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

    public static int dipToPixel(Context context, int dipValue) {
        if (context == null) {
            return dipValue; // 原值返回
        }
        try {
            float pixelFloat = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                            .getResources().getDisplayMetrics());
            return (int) pixelFloat;
        } catch (Exception e) {
        }
        return dipValue;
    }

    public static int getAutoDimenValue(Context mContext,int resId){
        return (int)(mContext.getResources().getDimension(resId));
    }

    public static int getColorWithAlpha(Context mContext, int colorId, int alphaId){

        String alphaStr = getResources().getString(alphaId);
        int alpha = (int)(Double.parseDouble(alphaStr)*255.0);

        int color = mContext.getResources().getColor(colorId);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha,red,green,blue);
    }

    /**
     * 从string.xml资源文件中获取字符串
     *
     * @param context
     * @param resId
     * @return
     */
    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    /**
     * 从string.xml资源文件中获取字符串
     *
     * @param obj
     * @param resId
     * @return
     */
    public static String getString(Object obj, int resId) {
        //TODO by zhouhan 用getString(int resId)方法替换。
        return getResources().getString(resId);
    }

    public static String getString(int resId){
        return getResources().getString(resId);
    }

    public static int getDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    public static Resources getResources(){
        return CC.getApplication().getApplicationContext().getResources();
    }
}
