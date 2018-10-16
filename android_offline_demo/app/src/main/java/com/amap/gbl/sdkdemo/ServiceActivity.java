package com.amap.gbl.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.autonavi.gbl.data.MapDataService;
import com.autonavi.gbl.data.model.DataType;
import com.autonavi.gbl.data.model.DownLoadMode;

import com.autonavi.gbl.data.model.OperationErrCode;
import com.autonavi.gbl.data.observer.intfc.IDataListObserver;
import com.autonavi.gbl.servicemanager.ServiceMgr;
import com.autonavi.gbl.servicemanager.model.ServiceManagerEnum;

import static com.autonavi.gbl.data.model.DownLoadMode.*;


public class ServiceActivity extends FragmentActivity {

    public static final String TAG = FragmentActivity.class.getSimpleName();

    private Intent mIntent;
    private Button buttonDownloadTypeNet;
    private Button buttonDownloadTypeUsb;

    private DataListObserver dataListObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service);

        if (null == dataListObserver)
        {
            dataListObserver = new DataListObserver();
        }

        buttonDownloadTypeNet =  (Button) findViewById(R.id.button_download_type_net);
        buttonDownloadTypeNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = new String("");
                checkDataListAndUpdatedStatus(DOWNLOAD_MODE_NET,path, dataListObserver);
            }
        });
        // 按钮不显示
        buttonDownloadTypeUsb =  (Button) findViewById(R.id.button_download_type_usb);

    }

    @Override
    protected void onDestroy() {
        MapDataService mapDataService = (MapDataService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.MapDataSingleServiceID);

        if(null != mapDataService)
        {
            mapDataService.abortRequestDataListCheck(DOWNLOAD_MODE_NET);
            //mapDataService.abortRequestDataListCheck(DOWNLOAD_MODE_USB);
        }
        super.onDestroy();
    }

    private  class DataListObserver implements IDataListObserver
    {
        public void onRequestDataListCheck(@DownLoadMode.DownLoadMode1 int var1, @DataType.DataType1 int var2, @OperationErrCode.OperationErrCode1 int var3)
        {
            mIntent = new Intent(ServiceActivity.this, DownloadActivity.class) ;
            startActivity(mIntent);
        }
    };

    /**
     * 检查当前已下载的数据是否需要更新
     */
    private void checkDataListAndUpdatedStatus(@DownLoadMode.DownLoadMode1 int downLoadMode, String path, IDataListObserver pObserver) {
        MapDataService mapDataService = (MapDataService) ServiceMgr.getServiceMgrInstance().getBLService(ServiceManagerEnum.MapDataSingleServiceID);

        if(null != mapDataService)
        {
            int nRet = mapDataService.requestDataListCheck(downLoadMode,path,pObserver);
            if (0 < nRet)
            {
                // 发起成功，等待回调通知
            }
            else
            {
                // 发起请求失败
            }
        }
    }
}
