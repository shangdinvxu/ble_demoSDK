package com.ble.utils;

import android.content.Context;
import android.content.Intent;

public class ServiceUtils {
	private static final int INDEX_CLOSE = 0Xff;
	private static final int INDEX_OPEN = 0X00;
	
	private static final String INDEX_FLAG = "PAY_APP_MSG";
	private static final String SERVICE_NAME = "com.linkloving.watch.BLE_SERVICE";
	/**
	 * 关闭蓝牙内部服务：
	 * 启动服务：服务为：com.linkloving.watch.BLESERVICE
	 * 此时的场景是：打开支付APP开始扫描手表 连爱APP后台会静默断开蓝牙，禁止重连，取消定时扫描器
	 * 
	 */
   		public static void CLOSE_LINK_BLE(Context context){
			Intent serviceintent = new Intent();
			serviceintent.setAction(SERVICE_NAME);
			serviceintent.putExtra(INDEX_FLAG, INDEX_CLOSE);
			context.startService(serviceintent); //启动服务程序。
   }
   /**
	 * 启动蓝牙内部服务：
	 * 启动服务：服务为：com.linkloving.watch.BLESERVICE
	 * 此时的场景是：使用完毕支付APP，并且退出了支付APP 连爱APP后台会自动连接手表并且开启定时扫描器
	 */
   public static void OPEN_LINK_BLE(Context context){

	   Intent serviceintent = new Intent();
	   serviceintent.setAction(SERVICE_NAME);
	   serviceintent.putExtra(INDEX_FLAG, INDEX_OPEN);
	   context.startService(serviceintent); //启动服务程序。
   }
}
