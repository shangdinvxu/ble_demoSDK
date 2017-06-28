package com.ble.list;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ble.main.R;
import com.ble.main.service.Bleservice;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;

import java.io.UnsupportedEncodingException;

public class RenameView extends Activity{
	public static final String TAG = RenameView.class.getSimpleName();
	private EditText edit;
	private TextView yue;
	private Button btn_ok;
	private Button get_count;
	private Button back;
	private BLEProvider provider;
	private BLEProviderObserverAdapter observerAdapter;
	private LPDeviceInfo deviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_rename);
		provider=Bleservice.getInstance(this).getCurrentHandlerProvider();
		deviceInfo = new LPDeviceInfo();
		deviceInfo.customer=LPDeviceInfo.DATANG_ZHENJIANG;
		observerAdapter = new BLEProviderObserverAdapter(){

			@Override
			protected Activity getActivity() {
				return RenameView.this;
			}

			@Override
			public void updateFor_notifyForSetNameSucess() {
				super.updateFor_notifyForSetNameSucess();
				Log.e(TAG, "name设置成功");
			}

			@Override
			public void updateFor_notifyForSetNameFail() {
				super.updateFor_notifyForSetNameFail();
				Log.e(TAG, "name设置失败");
			}

			@Override
			public void updateFor_OpenSmc(boolean isSuccess) {
				super.updateFor_OpenSmc(isSuccess);
				if(isSuccess){
					provider.AIDSmartCard(RenameView.this, deviceInfo);
				}
			}

			@Override
			public void updateFor_AIDSmc(boolean isSuccess) {
				super.updateFor_AIDSmc(isSuccess);
				if(isSuccess){
					provider.readCardBalance(RenameView.this, deviceInfo);
				}
			}

			@Override
			public void updateFor_GetSmcBalance(Integer obj) {
				super.updateFor_GetSmcBalance(obj);
				yue.setText(obj/100+"."+obj%100);
			}
			
			
		};
		
		provider.setBleProviderObserver(observerAdapter);
		back = (Button) findViewById(R.id.back);
		edit = (EditText) findViewById(R.id.name);
		yue = (TextView) findViewById(R.id.yue);
		btn_ok = (Button) findViewById(R.id.submit);
		get_count = (Button) findViewById(R.id.get_count);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = edit.getText().toString();
				deviceInfo.userNickname = name;
				Log.e(TAG, deviceInfo.userNickname);
				provider.set_name(RenameView.this, deviceInfo);
			}
		});
		
		get_count.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				provider.openSmartCard(RenameView.this);
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(provider.getBleProviderObserver() == null){
			provider.setBleProviderObserver(observerAdapter);
		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		provider.closeSmartCard(RenameView.this);
	}

	public static byte[] stringtobyte(String orignal, int size)   
	           throws UnsupportedEncodingException {   
	       // 原始字符不为null，也不是空字符串   
	       if (orignal != null && !"".equals(orignal)) {   
	           // 将原始字符串转换为utf-8编码格式   
	           orignal = new String(orignal.getBytes(), "utf-8");   
//	           // 要截取的字节数大于0，且小于原始字符串的字节数   
	           if (size > 0 && size < orignal.getBytes("utf-8").length) {   
	               char c;   
	               int k =0;
	               for (int i = 0; i < orignal.length(); i++) {   
	                	   //遇到的是英文或者数字
	                	   if(k+1>size){
	                     	  orignal=orignal.substring(0,i);
	                     	  System.out.println(orignal);
	                     	  break; 
	                       }
	                	   else
	                		   k++;
	               }   
	           }   
	       }   
	       return orignal.getBytes("utf-8");   
	   }
	

}
