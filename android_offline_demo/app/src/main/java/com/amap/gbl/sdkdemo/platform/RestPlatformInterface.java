package com.amap.gbl.sdkdemo.platform;

import android.util.Log;

import com.autonavi.gbl.cobase.observer.IPlatformInterface;

import java.util.LinkedHashMap;

/**
 * 没有ServerKey的实现接口
 * rest开放平台
 */
public class RestPlatformInterface implements IPlatformInterface {
    public static final String TAG = RestPlatformInterface.class.getSimpleName().toString();
    @Override
    public void copyAssetFile(String assetFilePath, String destFilePath) {
        CommonUtil.copyAssetFile(assetFilePath, destFilePath);
    }

    @Override
    public float getDensity(int deviceId) {
        return CC.getApplication().getResources().getDisplayMetrics().density;
    }

    @Override
    public int getDensityDpi(int deviceId) {
        return CC.getApplication().getResources().getDisplayMetrics().densityDpi;
    }

    @Override
    public
    @NetworkStatus
    int getNetStatus() {
        //todo
        Log.i(TAG, "getNetStatus: 1111111");
        return NetworkStatusNotReachable;
    }

    @Override
    public LinkedHashMap<String, String> getCdnNetworkParam() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        /**< diu    设备唯一号,android--imei, ios--IDFV */
        linkedHashMap.put("diu", "1498635cd464d9150b27b7486e436a2f");

        /**< client_network_class   获取当前网络状态 */
        linkedHashMap.put("client_network_class", "4");

        /**< lon	用户位置——经度 */
        linkedHashMap.put("lon", "116.427915");

        /**< lat	用户位置——纬度 */
        linkedHashMap.put("lat", "39.902895");
        return linkedHashMap;
    }

    @Override
    public LinkedHashMap<String, String> getAosNetworkParam() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

        /**< diu    设备唯一号,android--imei, ios--IDFV */
        linkedHashMap.put("diu", "1498635cd464d9150b27b7486e436a2f");

        /**< client_network_class   获取当前网络状态 */
        linkedHashMap.put("client_network_class", "4");

        return linkedHashMap;
    }

    @Override
    public String amapEncode(String strInput) {
        String strTemp = new String();
        return strTemp;
    }

    @Override
    public byte[] amapEncodeBinary(byte[] binaryInput) {
        byte[] byteTemp = new byte[0];
        return byteTemp;
    }

    @Override
    public String amapDecode(String strInput) {
        String strTemp = new String();
        return strTemp;
    }

    @Override
    public String getAosSign(String raw) {
        String strTemp = new String();
        return strTemp;
    }
}
