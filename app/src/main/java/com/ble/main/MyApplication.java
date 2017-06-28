package com.ble.main;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ble.main.service.Bleservice;
import com.yolanda.nohttp.NoHttp;

import java.util.List;


public class MyApplication extends Application{
	
	private static final String TAG = MyApplication.class.getSimpleName();
	
	private static MyApplication self = null;
	
	public static final String BLE_SYN_SUCCESS = "com.ble.connectsuc";
	public static final String BLE_CONNECT_LOST = "com.ble.connectlost";  
	public static final String BLE_SERVICE_CANCLE = "service_cancle";  
	
	public static final String SERVICE_WATCH = "com.ble.main";
	
	public final static String MAIN_SERVICE = "com.ble.main.service.Bleservice";
	
	public static MyApplication getInstance(Context context)
	{
		return self;
	}
	 // ** 蓝牙 BLE 初始化相关代码
    @Override
	public void onCreate() {
		super.onCreate();

        NoHttp.init(this);

		self = this ;
		String processName = getProcessName(this,android.os.Process.myPid());
        if (processName != null) 
        {
			boolean defaultProcess = processName.equals(MyApplication.SERVICE_WATCH);
			if (defaultProcess){
				Log.i(TAG, "开始注册服务:"+processName);
				registerServices();
			}
		}
		
	}

    private void registerServices() {
        Intent intent = new Intent(MyApplication.this,Bleservice.class);
        startService(intent);
	}



    /**
     * 用来判断服务是否运行.
//     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public  boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(500);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
//        	Log.e(TAG, serviceList.get(i).service.getClassName().toString());
            if (serviceList.get(i).service.getClassName().toString().equals(className) == true) 
            {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
    static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
    
    
