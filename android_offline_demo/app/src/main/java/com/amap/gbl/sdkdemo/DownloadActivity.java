package com.amap.gbl.sdkdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.autonavi.gbl.common.IThreadObserver;
import com.autonavi.gbl.data.MapDataService;
import com.autonavi.gbl.data.model.Area;
import com.autonavi.gbl.data.model.AreaType;
import com.autonavi.gbl.data.model.CityDownLoadItem;
import com.autonavi.gbl.data.model.DataType;
import com.autonavi.gbl.data.model.DownLoadMode;
import com.autonavi.gbl.data.model.OperationErrCode;
import com.autonavi.gbl.data.model.OperationType;
import com.autonavi.gbl.data.model.TaskStatusCode;
import com.autonavi.gbl.data.observer.intfc.IDownloadObserver;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.autonavi.gbl.data.model.DataType.*;
import static com.autonavi.gbl.data.model.DownLoadMode.*;
import static com.autonavi.gbl.data.model.OperationErrCode.*;
import static com.autonavi.gbl.data.model.OperationType.*;
import static com.autonavi.gbl.data.model.TaskStatusCode.*;


public class DownloadActivity extends FragmentActivity implements View.OnClickListener,IDownloadObserver {
    public final static String TAG = DownloadActivity.class.getSimpleName();
   
    private MapDataService mIMapDataService;
    private int mDownloadMode;
    private int nowAreaType;
    private int[] adcodeDiyLst;
    private Button btn_citys_choosed;
    private Button btn_download_city_map;
    private Button btn_pause_download;
    private Button btn_cancel_download;
    private Button btn_delete_download;
    private Button btn_select_type;
    private TextView tv_data;
    private TextView tv_onOperated;
    private TextView tv_onDownLoadStatus;
    private TextView tv_onPercent;
    private TextView tv_data_1;
    private EditText editText;
    private EditText et_download_citys_code, et_download_only;
    private int currentAdCode = -999;//当前城市adcode，如北京：110000
    private int currentUrCode = -999;//当前城市urcode，如北京：1，研发无法提供精确对应值，只表示>0即可
    private  List<String> mCityNameList = new ArrayList<String>();
    private String[] mMultiChoiceItems;
    private boolean[] checkedItemsFlag;

    private Map<String, Integer> mCityMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadMode = DOWNLOAD_MODE_NET;
        setContentView(R.layout.activity_download);
        String path = new String("");
        mIMapDataService = (MapDataService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.MapDataSingleServiceID);
        if(null != mIMapDataService)
        {
            mIMapDataService.addDownloadObserver(mDownloadMode, this);
        }

        initView();
        initCityData();

    }

    @Override
    protected void onDestroy() {
        mIMapDataService.removeDownloadObserver(mDownloadMode, this);
        super.onDestroy();
    }

    /**
     * 初始化城市基本数据
     */
    private void initCityData() {
        mCityMap.clear();

        initCityData(mDownloadMode, AreaType.AREA_TYPE_COUNTRY);
        initCityData(mDownloadMode, AreaType.AREA_TYPE_DIRECT);
        initCityData(mDownloadMode, AreaType.AREA_TYPE_CITY);
        initCityData(mDownloadMode, AreaType.AREA_TYPE_SPECIAL);

        int length = mCityNameList.size();
        if(0 < length)
        {
            mMultiChoiceItems = new String[length];
            checkedItemsFlag = new boolean[length];

            int i=0;
            for(String cityName : mCityNameList)
            {
                mMultiChoiceItems[i] = cityName;
                checkedItemsFlag[i]= false;
                i++;
            }
        }
    }

    private void initCityData(int downloadMode, int areaType) {

        int [] adcodeLst = mIMapDataService.getAdcodeList(downloadMode,areaType);

        if(null != adcodeLst && 0 < adcodeLst.length)
        {

            for (int i=0;i<adcodeLst.length;i++)
            {
                Integer adcode = new Integer(adcodeLst[i]);
                Area area = new Area();
                mIMapDataService.getArea(downloadMode,adcode,area);
                String cityName = new String(area.name);
                mCityMap.put(cityName,adcode);
                mCityNameList.add(cityName);
            }
        }
    }


    private void initView() {
        et_download_citys_code = (EditText) findViewById(R.id.et_download_citys_code);
        tv_data_1 = (TextView) findViewById(R.id.tv_data_1);
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_onOperated = (TextView) findViewById(R.id.tv_onOperated);
        tv_onDownLoadStatus = (TextView) findViewById(R.id.tv_onDownLoadStatus);
        tv_onPercent = (TextView) findViewById(R.id.tv_onPercent);

        btn_citys_choosed =  (Button) findViewById((R.id.btn_citys_choosed));
        btn_citys_choosed.setOnClickListener(this);
        btn_download_city_map = (Button) findViewById((R.id.btn_download_city_map));
        btn_download_city_map.setOnClickListener(this);
        btn_delete_download = (Button) findViewById((R.id.btn_delete_download));
        btn_delete_download.setOnClickListener(this);
        btn_cancel_download = (Button) findViewById((R.id.btn_cancel_download));
        btn_cancel_download.setOnClickListener(this);
        btn_pause_download = (Button) findViewById((R.id.btn_pause_download));
        btn_pause_download.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_citys_choosed:

                if(null != mMultiChoiceItems && null != checkedItemsFlag)
                {
                    new AlertDialog.Builder(this).setMultiChoiceItems(mMultiChoiceItems, checkedItemsFlag, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            checkedItemsFlag[which] = isChecked;
                            Object checkedItem = lw.getAdapter().getItem(which);
                            dialog.dismiss();
                            String text = checkedItem.toString().trim();
                            //设置当前城市值
                            currentAdCode = mCityMap.get(text);
                            currentUrCode =  mIMapDataService.getUrcode(currentAdCode);
                            tv_data.setText("您选择了:" + (String) checkedItem + ", adcode:" + currentAdCode+"，urcode:"+currentUrCode);
                            List<String>  choosedCityNameList = getChoosedCityNameList();
                            if (choosedCityNameList != null && choosedCityNameList.size() != 0) {
                                et_download_citys_code.setText(choosedCityNameListToString(choosedCityNameList));
                            }
                        }
                    }).create().show();
                }

                break;
            case R.id.btn_download_city_map://下载:
            case R.id.btn_pause_download:   //暂停
            case R.id.btn_cancel_download:  //取消
            case R.id.btn_delete_download:  //删除

                int opreationType = OPERATION_TYPE_START;
                if(v.getId() == R.id.btn_pause_download)
                {
                    Log.i(TAG, "onClick: btn_pause_download: ");
                    opreationType = OperationType.OPERATION_TYPE_PAUSE;
                }
                else if(v.getId() == R.id.btn_cancel_download)
                {
                    Log.i(TAG, "onClick: btn_cancel_download: ");
                    opreationType = OperationType.OPERATION_TYPE_CANCEL;
                }
                else if(v.getId() == R.id.btn_delete_download)
                {
                    Log.i(TAG, "onClick: btn_delete_download: ");
                    opreationType = OperationType.OPERATION_TYPE_DELETE;
                }else
                {
                    Log.i(TAG, "onClick: btn_download_city_map: ");
                    opreationType = OPERATION_TYPE_START;
                }

                List<String>  choosedCityNameList = getChoosedCityNameList();
                if (choosedCityNameList != null && choosedCityNameList.size() != 0) {
                    int [] choosedCityAdcodeList = getChoosedCityAdcodeList(choosedCityNameList);
                    if(null != choosedCityAdcodeList)
                    {
                        String strCityStatus = getChooseCityListShowText();
                        tv_onDownLoadStatus.setText(strCityStatus);
                        mIMapDataService.operate(mDownloadMode, opreationType, choosedCityAdcodeList);
                    }
                    else
                    {
                        Log.i(TAG, "choosedCityAdcodeList is null");
                    }

                } else {
                    Log.i(TAG, "choosedCityNameList is null");
                    et_download_citys_code.setText("请选择待操作的城市");
                }
                break;

        }
        Log.i(TAG, "nowDownloadMode=" + mDownloadMode);
    }

    private List<String> getChoosedCityNameList() {

        List<String>  choosedCityNameList = new ArrayList<String>();

        if(null != mMultiChoiceItems && null != checkedItemsFlag)
        {
            int i = 0;
            for(boolean flag:checkedItemsFlag)
            {
                if(flag)
                {
                    choosedCityNameList.add(mMultiChoiceItems[i]);
                }
                i++;
            }
        }

        return choosedCityNameList;
    }

    private int [] getChoosedCityAdcodeList( List<String>  choosedCityNameList) {

        int [] choosedCityAdcodeList = null;

        if(null != choosedCityNameList && 0 < choosedCityNameList.size())
        {
            int i = 0;
            choosedCityAdcodeList = new int[choosedCityNameList.size()];
            for(String item:choosedCityNameList)
            {
                if(null != item)
                {
                    choosedCityAdcodeList[i] = mCityMap.get(item);
                }
                i++;
            }
        }
        return choosedCityAdcodeList;
    }

    private String choosedCityNameListToString( List<String>  choosedCityNameList) {

        String stringName = new String();

        if(null != choosedCityNameList && 0 < choosedCityNameList.size())
        {
            int i = 0;
            for(String item:choosedCityNameList)
            {
                if(null != item)
                {
                    stringName += item;
                    stringName += ",";
                }
            }
        }
        return stringName;
    }

    private String switchOperationTypeToString(@OperationType.OperationType1 final int opType)
    {
        String desc = new String("");
        switch (opType)
        {
            case OPERATION_TYPE_START:
                desc = "执行开始";
                break;
            case OPERATION_TYPE_PAUSE:
                desc = "执行暂停";
                break;
            case OPERATION_TYPE_CANCEL:
                desc = "执行取消";
                break;
            case OPERATION_TYPE_DELETE:
                desc = "执行删除";
                break;
        }

        return desc;
    }


    private String switchTaskStatusCodeToString(boolean isDataUsed, @TaskStatusCode.TaskStatusCode1 final int taskCode) {
        String desc = new String("");

        switch (taskCode) {
            case TASK_STATUS_CODE_READY:
                desc = "待下载";
                if(isDataUsed)
                {
                    desc = "待更新";
                }
                break;
            case TASK_STATUS_CODE_WAITING:
                desc = "等待中";
                break;
            case TASK_STATUS_CODE_PAUSE:
                desc = "暂停";
                break;
            case TASK_STATUS_CODE_DOING:
            case TASK_STATUS_CODE_DONE:
                desc = "下载中";
                if(isDataUsed)
                {
                    desc = "更新中";
                }
                break;
            case TASK_STATUS_CODE_CHECKING:
                desc = "校验中";
                break;
            case TASK_STATUS_CODE_CHECKED:
                desc = "校验完成";
                break;
            case TASK_STATUS_CODE_UNZIPPING:
                desc = "解压中";
                break;
            case TASK_STATUS_CODE_UNZIPPED:
                desc = "解压完成";
                break;
            case TASK_STATUS_CODE_SUCCESS:
                desc = "已下载";
                break;
            case TASK_STATUS_CODE_ERR:
            case TASK_STATUS_CODE_MAX:
                desc = "重试";
                break;
        }

        return desc;
    }
    private String switchOperationErrCodeToString(@OperationErrCode.OperationErrCode1 final int opErrCode)
    {
        String desc = new String("");
        switch (opErrCode)
        {
            case OPERATION_ERR_CODE_CORRECT:
                desc = "正常";
                break;
            case OPERATION_ERR_CODE_NET_NONE:
                desc = "无网络连接";
                break;
            case OPERATION_ERR_CODE_NET_DNS_TIMEOUT:
                desc = "DNS解析域名超时";
                break;
            case OPERATION_ERR_CODE_NET_CONNECT_TIMEOUT:
                desc = "连接服务端超时";
                break;
            case OPERATION_ERR_CODE_NET_SEND_FAILED:
                desc = "发送请求失败";
                break;
            case OPERATION_ERR_CODE_NET_RECV_FAILED:
                desc = "网络应答过程失败";
                break;
            case OPERATION_ERROR_CODE_NET_DATA_INVALID:
                desc = "网络应答数据内容异常";
                break;
            case OPERATION_ERR_CODE_CHECK_INVALID:
                desc = "校验错误";
                break;
            case OPERATION_ERR_CODE_UNZIP_INVALID:
                desc = "解压错误";
                break;
            case OPERATION_ERR_CODE_MERGER_INVALID:
                desc = "融合错误";
                break;
            case OPERATION_ERR_CODE_SPACE_UNENOUTH:
                desc = "磁盘空间不足";
                break;
            case OPERATION_ERR_CODE_FILE_OPEN_INVALID:
                desc = "文件打开错误";
                break;
            case OPERATION_ERR_CODE_FILE_RENAME_INVALID:
                desc = "文件重命名错误";
                break;
            case OPERATION_ERR_CODE_FILE_READ_INVALID:
                desc = "文件读错误";
                break;
            case OPERATION_ERR_CODE_FILE_WRITE_INVALID:
                desc = "文件写错误";
                break;
            case OPERATION_ERR_CODE_FILE_MOVE_INVALID:
                desc = "文件移动错误";
                break;
            case OPERATION_CODE_UNKONW_INVALID:
                desc = "未知错误";
                break;
            case OPERATION_CODE_MAX:
                desc = "枚举最大边界";
                break;
        }
        return desc;
    }

    private String getChooseCityListShowText()
    {
        String strCityStatus = new String("");
        for (int choosedId : getChoosedCityAdcodeList(getChoosedCityNameList())) {
            int adcode = choosedId;
            Area area = new Area();
            mIMapDataService.getArea(mDownloadMode,adcode,area);
            strCityStatus += area.name;
            strCityStatus += ",";
            CityDownLoadItem downloadItem = new CityDownLoadItem();
            mIMapDataService.getCityDownLoadItem(mDownloadMode,adcode,downloadItem);
            String strTaskCode =  switchTaskStatusCodeToString (downloadItem.bIsDataUsed, downloadItem.taskState);
            strCityStatus += strTaskCode;
            strCityStatus += ",";
            strCityStatus += "进度";
            strCityStatus += downloadItem.percent;
            strCityStatus += "%";
            strCityStatus += "\n";
        }
        return strCityStatus;
    }

    @Override
    public void onOperated(@DownLoadMode.DownLoadMode1 final int downLoadMode, @DataType.DataType1 final int dataType,
                           @OperationType.OperationType1 final int opType, final int[] opreatedIdList) {
        if (opreatedIdList != null && opreatedIdList.length > 0) {
            for (int operatedId : opreatedIdList)
            {
                Log.d(TAG, "OnOperated: downLoadMode = " + downLoadMode + "; dataType = " + dataType + "; opType = "
                        + opType + "; operatedIdList = " + operatedId);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String strMode = new String(downLoadMode == DOWNLOAD_MODE_NET?"网络下载":"U盘更新");
                String strDataType = new String(dataType == DATA_TYPE_MAP?"地图数据":"语音数据");
                String strOpType = switchOperationTypeToString(opType);

                tv_onOperated.setText(strMode + "," + strDataType + "," + strOpType + "\n");

                String strCityStatus = getChooseCityListShowText();
                tv_onDownLoadStatus.setText(strCityStatus);

                if(OperationType.OPERATION_TYPE_CANCEL <= opType)
                {
                    tv_onPercent.setText("");
                }
            }
        });
    }

    @Override
    public void onDownLoadStatus(@DownLoadMode.DownLoadMode1 final int downLoadMode, @DataType.DataType1 final int dataType,
                                 final int id, @TaskStatusCode.TaskStatusCode1 final int taskCode, @OperationErrCode
            .OperationErrCode1 final int opCode) {
        Log.d(TAG, "onDownLoadStatus: downLoadMode = " + downLoadMode + "; dataType = " + dataType + "; taskCode = " +
                taskCode + "; opCode = " + opCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

               String strCityStatus = getChooseCityListShowText();
               tv_onDownLoadStatus.setText(strCityStatus);
            }
        });
    }

    @Override
    public void onPercent(@DownLoadMode.DownLoadMode1 final int downLoadMode, @DataType.DataType1 final int dataType, final int id,
                          final int percentType, final float percent) {
        Log.d(TAG, "OnPercent: downLoadMode = " + downLoadMode + "; dataType = " + dataType + "; modeId = " + id + ";" +
                " percentType = " + percentType + "; percent = " + percent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String strMode = new String(downLoadMode == DOWNLOAD_MODE_NET?"网络下载":"U盘更新");
                String strDataType = new String(dataType == DATA_TYPE_MAP?"地图数据":"语音数据");
                Area area = new Area();
                int adcode = id;
                mIMapDataService.getArea(downLoadMode,adcode,area);
                String strPercentType = new String(0 == percentType?"下载进度":"解压进度");
                tv_onPercent.setText(strMode + "," + strDataType + ","+area.name+","+strPercentType + String.valueOf(percent)+ "%");
            }
        });
    }


    IThreadObserver mThreadObserver = new IThreadObserver() {
        @Override
        public void threadCallback(long threadHandle, byte flag) {
        }
    };

}
