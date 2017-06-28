package com.ble.main.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ble.main.MyApplication;
import com.ble.main.service.Bleservice;
import com.example.android.bluetoothlegatt.BLEProvider;

import java.io.UnsupportedEncodingException;

public class SMSBroadcastReceiver extends BroadcastReceiver {
	 
	 public static final String TAG = "SMSBroadcastReceiver";
	 public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	 public static final char SMS='1';
	 private BLEProvider provider;
	 private static int seq=0;
	 private Context context;
	 private char[] array_phone = { 0, 0, 0, 0, 0 };
	 
	 
	 private void connectble0x02(Context context) {
			/************************测试(开机启动)***************************/
			Log.e(TAG, "收到短信了");
			Intent serviceintent = new Intent();  
			serviceintent.setAction("com.linkloving.watch.BLE_SERVICE");
			serviceintent.setPackage(context.getPackageName());
			serviceintent.putExtra("PAY_APP_MSG", 0x02);
			context.startService(serviceintent); //启动服务程序。
			/************************测试(开机启动)***************************/
		}
	 
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		 this.context = context;
		 provider = Bleservice.getInstance(context).getCurrentHandlerProvider();
//		 String Ansc_str = Integer.toBinaryString(deviceSetting.getANCS_value());
		String Ansc_str = "11111";
		 char[] charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
		 System.arraycopy(charr, 0, array_phone, 5 - charr.length, charr.length);
		 if(SMS_RECEIVED_ACTION.equals(intent.getAction()) ){
			 connectble0x02(context);
			if(provider.isConnectedAndDiscovered() && array_phone[2] == SMS ){
				 SmsMessage msg = null;
		           Bundle bundle = intent.getExtras();
		           if (bundle != null) {
		               Object[] pdusObj = (Object[]) bundle.get("pdus");
		               for (Object p : pdusObj) {
		                   msg= SmsMessage.createFromPdu((byte[]) p);
		                   String msgTxt =msg.getMessageBody();
		                   String senderNumber = msg.getOriginatingAddress();
		                   Log.e(TAG, "发送人："+senderNumber+"  短信内容："+msgTxt);
		                   Log.e(TAG, "seq的值："+seq);
    		     		   try {
							provider.setNotification_MSG(context, (byte)seq, CutString.stringtobyte(PhoneReceiver.getContactNameByPhoneNumber(context, senderNumber), 24), CutString.stringtobyte(msgTxt, 84));
							seq++;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}	
		                   return;
		           }
		           return;
		       }
			}
			
	           
		}
	}
	
	
}
