package com.ble.main.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ble.main.MyApplication;
import com.ble.main.service.Bleservice;
import com.example.android.bluetoothlegatt.BLEProvider;

import java.io.UnsupportedEncodingException;

public class PhoneReceiver extends BroadcastReceiver {
	public static final String TAG = PhoneReceiver.class.getSimpleName();

	private BLEProvider provider;
	public static TelephonyManager manager;
 
	private int seq;
	
    Context context;
    char[] array_phone = { 0, 0, 0, 0, 0 };
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if(MyApplication.getInstance(context)==null){
			//APP还未启动的时候  获取userEntity会null
			return;
		}
		provider =Bleservice.getInstance(context).getCurrentHandlerProvider();
//		String Ansc_str = Integer.toBinaryString(deviceSetting.getANCS_value());
		String Ansc_str = "11111";
		 char[] charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
		 System.arraycopy(charr, 0, array_phone, 5 - charr.length, charr.length);
		 if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){  //拨打号码
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		}else{
			//*  查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
		    //*  如果我们想要监听电话的拨打状况，需要这么几步 :
			//*  第一：获取电话服务管理器TelephonyManager manager = this.getSystemService(TELEPHONY_SERVICE);
			//*  第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new MyPhoneStateListener(),
			//*  PhoneStateListener.LISTEN_CALL_STATE);这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要 
			//*  监听的状态改变事件，初次之外，还有很多其他事件哦。
			//*  第三步：通过extends PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
			//*  第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
						manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
						manager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
	
	
	PhoneStateListener listener=new PhoneStateListener(){

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
		 switch(state){
				 case TelephonyManager.CALL_STATE_IDLE:
					   
					    //移除来电
					    if(provider.isConnectedAndDiscovered() && array_phone[3] == '1' && NotificationCollectorService.phoneinidle_count==0 && NotificationCollectorService.phoneinhook_count!=1){
					    	 Log.i(TAG, "挂断");  
					    	 NotificationCollectorService.phoneinhook_count=0;
					    	 NotificationCollectorService.phoneincall_count=0;
					    	 NotificationCollectorService.phoneinidle_count++;
					    	provider.setNotification_remove_incall(context,NotificationCollectorService.incallphone,NotificationCollectorService.incallphone_title,NotificationCollectorService.incallphone_title);
					    	 /**为确保万一*/
					    	 try {
								new Thread().sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					    	 Log.i(TAG, "incallphone_title:"+NotificationCollectorService.incallphone_title); 
						    provider.setNotification_misscall(context, (byte)seq,NotificationCollectorService.incallphone_title,NotificationCollectorService.incallphone_title);
						    seq=0;
					    }
						
					    break;
					    
					   case TelephonyManager.CALL_STATE_OFFHOOK:
						   
						//移除来电
						if(provider.isConnectedAndDiscovered() && array_phone[3] == '1' && NotificationCollectorService.phoneinhook_count==0 ){
						Log.i(TAG, "接听");
						NotificationCollectorService.phoneincall_count=0;
						NotificationCollectorService.phoneinhook_count++;
						provider.setNotification_remove_incall(context,NotificationCollectorService.incallphone,NotificationCollectorService.incallphone_title,NotificationCollectorService.incallphone_title);
						seq=0;
						}
					    break;
					    
					    
					   case TelephonyManager.CALL_STATE_RINGING:
						 Log.i(TAG, "provider.isConnectedAndDiscovered():"+provider.isConnectedAndDiscovered());
						 Log.i(TAG, "array_phone[3] == 1"+(array_phone[3] == '1'));
						 Log.i(TAG, "phoneincall_count==0:"+(NotificationCollectorService.phoneincall_count==0));
						 
					    //输出来电号码
					    if(provider.isConnectedAndDiscovered() && array_phone[3] == '1' && NotificationCollectorService.phoneincall_count==0){
					    	Log.i(TAG, "响铃:来电号码"+incomingNumber);
					    	
					    	String name = getContactNameByPhoneNumber(context,incomingNumber);
					    	Log.i(TAG, "响铃:来电人"+name);
					    	NotificationCollectorService.phoneincall_count++;
					    	NotificationCollectorService.phoneinidle_count=0;
					    	NotificationCollectorService.phoneinhook_count=0;		
					    	try {
					    		NotificationCollectorService.incallphone_title=CutString.stringtobyte(name, 24);
					    		NotificationCollectorService.incallphone_text =CutString.stringtobyte(name, 84);
					            provider.setNotification_PHONE(context, (byte)seq, NotificationCollectorService.incallphone_title, NotificationCollectorService.incallphone_text);
					            NotificationCollectorService.incallphone=(byte)seq;
					            seq++;
					           
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
					    }
					    break;
				 }
		 super.onCallStateChanged(state, incomingNumber);
//			}
		}
		
	};
	
	/*
     * 根据电话号码取得联系人姓名
     */
    public static String getContactNameByPhoneNumber(Context context, String address) {
    	Cursor c = context.getContentResolver().query(Uri.withAppendedPath(
                PhoneLookup.CONTENT_FILTER_URI, address), new String[] {
                PhoneLookup._ID,
                PhoneLookup.NUMBER,
                PhoneLookup.DISPLAY_NAME,
                PhoneLookup.TYPE, PhoneLookup.LABEL }, null, null,   null );

    	if(c==null ||c.getCount() == 0) {
           //没找到电话号码  就返回原号码
    		return address;
        } else if (c.getCount() > 0) {
            c.moveToFirst();
            return  c.getString(2); //获取姓名
        }
    	return address;
    }

}
