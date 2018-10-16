package com.amap.gbl.sdkdemo.model;

import android.os.Environment;

import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

/**
 * Created by zed.qzq on 2017/10/19.
 */

public class NaviConstant {
    public static String TAG = NaviConstant.class.getSimpleName();
    public static String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    //文件目录
    public static String PATH = Environment.getExternalStorageDirectory().getPath() + "/sdkDemo3x/";

    //经纬度坐标
    public static  double[] gaode = {118.185962, 24.489438};//厦门高德
    public static double[] beizhan = {118.074078, 24.636119};//厦门北站
    public static double[] shoukai = {116.473195, 39.993253};//北京首开广场
    public static double[] nanzhan = {116.377751, 39.864446};//北京南站
    public static double[] tiananmen = {116.397496, 39.908696};//北京天安门
    public static double[] gongren = {116.44672, 39.930065};//北京工人体育馆
    public static double[] qinhuadaxue = {116.326836, 40.00366};//北京工人体育馆
    public static double[] tile = {116.45291, 39.969183};//楼块高亮切割
    public static double[] lianpai = {116.459718, 39.955883};//联排建筑
    public static double[] guangchang = {116.397662, 39.906068};//天安门广场，无3D建筑
    public static double[] shanghai = {121.499779, 31.239668};//上海东方明珠


    public static double[] juxing1 = {116.392637, 39.882381};//矩形
    public static double[] juxing2 = {116.392717, 39.882583};//矩形


    public static double[] gaosu1 = {117.90596, 24.547914};//高速
    public static double[] gaosu2 = {117.923593, 24.546372};//高速

    double[] zhufu1 = {118.143516, 24.498728};//主辅路
    double[] zhufu2 = {118.137996, 24.504249};//主辅路

    public static double[] interval1 = {116.48803979158399, 39.907837223499484, 116.42569988965987, 39.87133430204828};//区间测速1
    public static double[] interval2 = {116.473944, 39.905066, 116.42569988965987, 39.87133430204828};//区间测速2  验证不单独出现1
    public static double[] interval3 = {116.9198904, 36.17764017, 116.9198904, 36.17764017};//区间测速2

    //P20坐标
    public static int[] gaodeP20 = {222343885, 115374134};

    // ============================ TBT ================================
    public static final int HANDLER_ON_NEW_ROUTE = 100000; //算路成功
    public static final int HANDLER_ON_ERROR_ROUTE = 100001; //算路失败
    public static final int HANDLER_ON_lOCINFO = 100002; //POS回调信息
    public static final int HANDLER_ON_SHOWCAMERA = 100003; //POS回调信息
    public static final int HANDLER_ON_ONPLAYTTS = 100004; //语音播报
    public static final int HANDLER_ON_UPDATENAVIINFO = 100005; //更新导航信息
    public static final int HANDLER_ON_UPDATEEXITINFO = 100006; //更新出口信息
    public static final int HANDLER_ON_SHOWCROSSPIC = 100007; //显示路口大图
    public static final int HANDLER_ON_UPDATESOCOL = 100008; //显示路口大图
    public static final int HANDLER_ON_UPDATEEVENT = 100009; //更新导航中的交通事件
    public static final int HANDLER_ON_CRUISE_FAC = 100010; //巡航中的道路设施
    public static final int HANDLER_ON_HIDE_CROSS = 100011; //隐藏放大路口
    public static final int HANDLER_ON_SHOW_MANEUVER = 100012; //显示放大路口
    public static final int HANDLER_ON_UPDATE_PARA = 100013; //主辅路更新
    public static final int HANDLER_ON_SWITCH_PARA = 100014;  //主辅路切换
    public static final int HANDLER_ON_CRUISE_CONGESTION = 100015;  //巡航拥堵信息
    public static final int HANDLER_ON_UPDATE_BAR = 100016;  //巡航拥堵信息
    public static final int HANDLER_ON_SHOWINTERVALCAMERA = 100017;  //区间测速电子眼信息
    public static final int HANDLER_ON_INTERVALCAMERADYNAMICINFO = 100018;  //区间测速电子眼动态信息
    public static final int HANDLER_ON_REROTE = 100019;  //重新算路
    public static final int HANDLER_ON_CONGESTION = 100020;  //onUpdateTMCCongestionInfo
    public static final int HANDLER_ON_LOCATION = 100021;  //地理位置改变
    public static final int HANDLER_ON_SHOWMIXINFO = 100022;//分歧路口
    public static final int HANDLER_ON_HIDELANEINFO = 100023;//隐藏车道线

    // ============================ 搜索 ================================
    public static final int HANDLER_ON_KEYWORD = 200000;  //关键字搜索
    public static final int HANDLER_ON_SUGGESTION = 200001;  //预搜索
    public static final int HANDLER_ON_ALONGWAY = 200002;  //沿途搜
    public static final int HANDLER_ON_DEEPINFO = 200003;  //深度信息
    public static final int HANDLER_ON_DETAILINFO = 200004;  //详细信息
    public static final int HANDLER_ON_NEAREST = 200005;  //逆地理
    public static final int HANDLER_ON_AROUND = 200006;  //周边搜
    public static final int HANDLER_ON_NAVIINFO = 200007;  //子POI到达点
    public static final int HANDLER_ON_CHARGE = 200008;  //充电桩状态

    // ============================ 主图 ================================

    /**
     * 测试网络 设置开放平台REST生成的32位用户key和私钥  默认测试网络
     */
    public static int testAosEnv = ServiceManagerEnum.AosDevelopmentEnv;
    public static String testNetworkKey = "4392896cd079d6bee68c1ec6d8bf9a6c";
    public static String testSecurityCode = "9B:12:D9:06:84:49:38:D4:E5:D8:37:06:E5:22:14:EE:EB:6F:A3:5B:com.example.locationapi";

    /**
     * 正式网络
     */
    public static int aosEnv = ServiceManagerEnum.AosProductionEnv;
    public static String networkKey = "d854b7cd9799cbf7256d80d2bd580c6a";
    public static String securityCode = "c60ad6566855869461218a1eb33104b4";

    public static String key_isehp = "key_isehp";//判断是否作为ehp项目的key
    public static String key_isdownloadehp = "key_isdownloadehp";//判断是否下载ehp的key

    /**
     * 数据、语音相关code
     */
    public static int DownLoadMode = 1;//类型
    public static int DataType = 2;//数据类型
    public static int OperationType = 3;//操作类型
    public static int TaskStatusCode = 4;//任务状态
    public static int OperationErrCode = 5;//错误码
    public static int percentType = 6;//百分比类型 (默认0表示下载; 1表示解压融合进度)

    //跑monkey的时候可以设置为false，关闭初始化和反初始化按钮
    public static boolean isShowInitBntn = true;


    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;
}
