package com.amap.gbl.sdkdemo.Utils;

import android.graphics.Bitmap;

/**
 * Created by zed.qzq on 2018/3/16.
 */

public class BitmapUtils {


    public static Bitmap eraseArrayBackground(Bitmap roadEnlargeArrowTmp,
                                               Bitmap roadEnlargeBg) {

        if (roadEnlargeArrowTmp == null || roadEnlargeBg == null) {
            return null;
        }

        int width = roadEnlargeArrowTmp.getWidth();
        int height = roadEnlargeArrowTmp.getHeight();

        int[] pixels = new int[width * height];
        int[] pixelsBack = new int[width * height];
        roadEnlargeArrowTmp.getPixels(pixels, 0, width, 0, 0, width, height);
        roadEnlargeBg.getPixels(pixelsBack, 0, width, 0, 0, width, height);
        int length = pixels.length;
        for (int i = 0; i < length; i++) {
            if (pixels[i] == 0xFFFF00FF) {
                pixels[i] = pixelsBack[i];
            }
        }

        pixelsBack = null;
        roadEnlargeArrowTmp.recycle();
        roadEnlargeArrowTmp = null;

        roadEnlargeBg.recycle();
        roadEnlargeBg = null;

        Bitmap newBmp = Bitmap.createBitmap(pixels, width, height,
                Bitmap.Config.ARGB_8888);
        pixels = null;
        return newBmp;
    }

}
