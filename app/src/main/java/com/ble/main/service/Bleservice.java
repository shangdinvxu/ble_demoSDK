package com.ble.main.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ble.main.MyApplication;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;

public class Bleservice extends Service{
	
	private static final String TAG = Bleservice.class.getSimpleName();
	private static final String ALARM_KEYER_ACTION = "com.linkloving.alarm_keyer_action";
	/** BLE蓝牙服务封装 */
	private BLEProvider provider;
	private static Bleservice self = null;
	
	private long timer;
//	private BroadcastReceiver mBroadcastReceiver;
	
	
	
	public static Bleservice getInstance(Context context)
	{
		return self;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		self = this;
		Log.i(TAG, "Bleservice服务已经开启!即将初始化蓝牙......");
		initBLEProvider();
		
//		if(android.os.Build.MODEL.startsWith("HUAWEI MT7") || android.os.Build.MODEL.startsWith("Galaxy Note4"))
//		{
//			 Timer timer=new Timer();        //每分钟更新一下蓝牙状态
//			 timer.schedule(new TimerTask()
//			 {
//				@Override
//				public void run()
//				{
//					Log.e(TAG, "正在Timer扫描...");
//					scanAndreconnect();
//				}
//			 }, 0,60000);
//		}
//		else
//		{
//			 Intent intent = new Intent(ALARM_KEYER_ACTION);
//		     PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
//		     long firsttime = SystemClock.elapsedRealtime();
//		     timer = System.currentTimeMillis()/1000;
//		     AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
//		     am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firsttime, 30000,sender);
//			 mBroadcastReceiver = new BroadcastReceiver(){
//		      @Override
//			  public void onReceive(Context context, Intent intent)
//		      {
//		    	  scanAndreconnect();
//			  }
//			    };
//			    IntentFilter filter = new IntentFilter(ALARM_KEYER_ACTION);
//			    this.registerReceiver(mBroadcastReceiver, filter);
//		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand......");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "Bleservice服务被停止......");
		provider.unregisterReciver();
//		unregisterReceiver(mBroadcastReceiver);
		broadcastUpdate(MyApplication.BLE_SERVICE_CANCLE, Bleservice.this);
		
	}

	private BLEProvider initBLEProvider() {
		
		LepaoProtocalImpl lpi = new LepaoProtocalImpl();
		
	    provider = new BLEProvider(this){
	    	
	    };
	    
	    provider.setProviderHandler(new BLEHandler(Bleservice.this){
	    	
	    	@Override
	 		protected BLEProvider getProvider()
	 		{
	 			return provider;
	 		}

			@Override
			protected void handleConnectSuccessMsg() {
				super.handleConnectSuccessMsg();
				Log.i(TAG, "连接成功!");
				broadcastUpdate(MyApplication.BLE_SYN_SUCCESS, Bleservice.this);
			}

			@Override
			protected void handleConnectLostMsg() {
				super.handleConnectLostMsg();
				Log.i(TAG, "连接断开!");
				broadcastUpdate(MyApplication.BLE_CONNECT_LOST, Bleservice.this);
			}
	 			
	        });
	    return provider;
	       
	}
	
	private void broadcastUpdate(final String action, Context context)
	{
		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}
	
	
	public BLEProvider getCurrentHandlerProvider()
	{
		return provider;
	}
	
	public void setProvider(BLEProvider provider)
	{
		this.provider = provider;
	}
	
	private void scanAndreconnect()
	{
		if(provider.getCurrentDeviceMac()!=null){
			Log.e(TAG, "系统当前时间:"+System.currentTimeMillis()/1000+"  扫描器当前时间:"+timer+"  差值:"+(System.currentTimeMillis()/1000-timer));
			if(System.currentTimeMillis()/1000-timer>5){
				timer = System.currentTimeMillis()/1000;
				if (!provider.isConnectedAndDiscovered()){
					provider.resetDefaultState();
					provider.scanForConnnecteAndDiscovery();
				}
				else
					provider.keepstate(Bleservice.this);
			}else
				timer = System.currentTimeMillis()/1000;
    	  }
	}
	
	/**
	 * 释放蓝牙相关的所有资源.
	 */
	public void releaseBLE()
	{
		if(provider.isConnectedAndDiscovered()){
			provider.resetDefaultState();
			provider.release();
		}

	}
	

}
