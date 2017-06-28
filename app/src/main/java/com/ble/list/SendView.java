package com.ble.list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.main.R;
import com.ble.main.service.Bleservice;
import com.ble.utils.Preferences;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendView extends Activity {
	private EditText editText;
	private Button send;
	private Button clear;
	private Button refresh;
	private ImageButton history_dropdown_button;
	private TextView log;
	private PopupWindow popView;
	private MyAdapter dropDownAdapter;
	private ListView nameListView = null; 
	private BLEProvider provider;
	private BLEProviderObserverAdapterImpl observerAdapter;
	private boolean isFirst=true;
	private String save ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dataview);
		provider= Bleservice.getInstance(this).getCurrentHandlerProvider();
		observerAdapter = new BLEProviderObserverAdapterImpl();
		init();
		initlistener();
	}

	private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter{

		@Override
		protected Activity getActivity() {
			return SendView.this;
		}

		@Override
		public void updateFor_handleConnectLostMsg() {
			super.updateFor_handleConnectLostMsg();
			Toast.makeText(SendView.this,"ble has disconnect",Toast.LENGTH_LONG).show();
		}

		@Override
		public void updateFor_response_ble(byte[] obj) {
			super.updateFor_response_ble(obj);
			StringBuffer sb = new StringBuffer();
			sb.append("Rec Commond:"+ "[  ");
			for (int i = 0; i < obj.length; i++) {
				sb.append(Integer.toHexString((obj[i] & 0xff)) + " ");
			}
			sb.append("]\n\n");
			Log.d("SendView", sb.toString());
			Log.d("SendView", editText.getText().toString().trim().length()+"");
			Preferences.addHexInfo(SendView.this,editText.getText().toString().trim());
			log.append(sb.toString());
		}

		@Override
		public void updateFor_response_ble_card(byte[] obj) {
			super.updateFor_response_ble_card(obj);
			StringBuffer sb = new StringBuffer();
			sb.append("Rec Commond:"+ "[  ");
			for (int i = 0; i < obj.length; i++) {
				sb.append(Integer.toHexString((obj[i] & 0xff)) + " ");
			}
			sb.append("]\n\n");
			Log.d("SendView", sb.toString());
			Preferences.addHexInfo(SendView.this,editText.getText().toString().trim());
			log.append(sb.toString());
		}
	}

	private void init() {
		editText = (EditText) findViewById(R.id.editText);
		editText.requestFocus();

		InputMethodManager inputManager = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
		inputManager.restartInput(editText);

		send = (Button) findViewById(R.id.send);
		clear = (Button) findViewById(R.id.clear);
		refresh=(Button) findViewById(R.id.refresh);
		history_dropdown_button =(ImageButton) findViewById(R.id.history_dropdown_button);
		log = (TextView) findViewById(R.id.Log);
		nameListView = new ListView(this);
	}

	private void initlistener() {
		/**
		 * 发送命令
		 */
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String value = editText.getText().toString();
				value=value.toLowerCase();
				String[] data_value=value.split(",");
				byte[] send_date = new byte[data_value.length];
				for(int i =0;i<data_value.length;i++){
                    Log.i("String_date---->", data_value[i]);
                    send_date[i] = hexStringToByte(data_value[i]);
                    Log.i("send_date---->", Integer.toHexString(send_date[i]& 0xFF));
                }
				//发送的核心代码
				provider.send_data2ble(SendView.this,send_date);
				StringBuffer sb = new StringBuffer();
				sb.append("Send Commond:"+ "[  ");
				for (int i = 0; i < send_date.length; i++) {
                    sb.append(Integer.toHexString((send_date[i] & 0xff)) + " ");
                }
				sb.append("]\n\n");
				log.append(sb.toString());
			}
		});
		/**
		 * 发送的记录
		 */
		history_dropdown_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					String[] usernames = Preferences.getHexInfo(SendView.this);
					if(isFirst)
					{
					
					if (usernames != null && usernames.length > 0)
					{
						//String[]
						initPopView(usernames);
						if (!popView.isShowing())
						{
							popView.showAsDropDown(editText);
						}
						else
						{
							popView.dismiss();
						}
						usernames=null;
						isFirst=false;
					}
				  }else{

					  if (usernames != null && usernames.length > 0)
					{
						updatePopView(usernames);
						if(!popView.isShowing())
							popView.showAsDropDown(editText);
					}
					else
					{
						popView.dismiss();
						popView = null;
					}
				}
				}
		});
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String value = editText.getText().toString();
				if(value.contains(",")){
					Toast.makeText(SendView.this,"格式无法转换（包含','特殊符号）",Toast.LENGTH_LONG).show();
				}else{
					byte[] send_date = StringtoByte(value);
					//发送的核心代码
					provider.send_data2ble_card(SendView.this,send_date);
					StringBuffer sb = new StringBuffer();
					sb.append("Send Commond:"+ "[  ");
					for (int i = 0; i < send_date.length; i++) {
						sb.append(Integer.toHexString((send_date[i] & 0xff)) + " ");
					}
					sb.append("]\n\n");
					log.append(sb.toString());
				}


//				String[] usernames = Preferences.getHexInfo(SendView.this);
//				if(isFirst){
//					if (usernames != null && usernames.length > 0)
//					{
//						//String[]
//						initPopView(usernames);
//						if (!popView.isShowing())
//						{
//							popView.showAsDropDown(editText);
//						}
//						else
//						{
//							popView.dismiss();
//						}
//						usernames=null;
//						isFirst=false;
//					}
//				}else{
//
//					if (usernames != null && usernames.length > 0)
//					{
//						updatePopView(usernames);
//						if(!popView.isShowing())
//							popView.showAsDropDown(editText);
//					}
//					else
//					{
//						popView.dismiss();
//						popView = null;
//					}
//				}
				
			}
		});
		
		clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				log.setText(null);
			}
		});
	}
	
	
	


	@Override
	protected void onPause() {
		super.onPause();
		save = log.getText().toString();
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		log.setText(save);
		if(provider.getBleProviderObserver() == null){
			provider.setBleProviderObserver(observerAdapter);
		}
	}

	private void initPopView(String[] usernames)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < usernames.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			map.put("drawable", R.mipmap.xicon);
			list.add(map);
			map=null;
		}
		
		dropDownAdapter = new MyAdapter(this, list, R.layout.dropdown_item, new String[] { "name", "drawable" }, new int[] { R.id.textview, R.id.delete });
		nameListView = new ListView(this);
		nameListView.setAdapter(dropDownAdapter);

		popView = new PopupWindow(nameListView, editText.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popView.setFocusable(true);
		popView.setOutsideTouchable(true);
		popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
		dropDownAdapter.notifyDataSetChanged();
		list=null;
//		popView.showAsDropDown(editText,0,0);
	}
	
	private void updatePopView(String[] usernames)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < usernames.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			map.put("drawable", R.mipmap.xicon);
			list.add(map);
		}
		
		dropDownAdapter.setData(list);
		dropDownAdapter.notifyDataSetChanged();
	}
	
	class MyAdapter extends SimpleAdapter
	{

		private List<HashMap<String, Object>> data;
		
		class ViewHolder
		{
			private TextView tv;
			private ImageButton btn;
		}

		public MyAdapter(Context context, List<HashMap<String, Object>> data, int resource, String[] from, int[] to)
		{
			super(context, data, resource, from, to);
			this.data = data;
		}

		@Override
		public int getCount()
		{
			return data.size();
		}

		
		public void setData(List<HashMap<String, Object>> data)
		{
			this.data = data;
		}
		
		@Override
		public Object getItem(int position)
		{
			return position;
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(SendView.this).inflate(R.layout.dropdown_item, null);
				holder.btn = (ImageButton) convertView.findViewById(R.id.delete);
				holder.tv = (TextView) convertView.findViewById(R.id.textview);
				convertView.setTag(holder);
			}
			else
			{
				
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(data.get(position).get("name").toString());
			holder.tv.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					String[] usernames = Preferences.getHexInfo(SendView.this);
					editText.setText(usernames == null ? "" : usernames[position]);
					popView.dismiss();
				}
				
			});
			
			holder.btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String[] usernames = Preferences.getHexInfo(SendView.this);
					if (usernames != null && usernames.length > 0)
					{
						updatePopView(usernames);
						if(!popView.isShowing())
							popView.showAsDropDown(editText);
					}
					else
					{
						popView.dismiss();
						popView = null;
					}
				}
			});
			return convertView;
		}
	}
	
	

	public static byte hexStringToByte(String hex) {
		//int len = (hex.length() / 2); // 除以2是因为十六进制比如a1使用两个字符代表一个byte
		byte result;
		if(hex.length() == 1){
			char[] achar= hex.toCharArray();
			result = (byte)toByte(achar[0]);
			result &= 0xff;
		}else if(hex.length() == 2){
			char[] achar = hex.toCharArray();
			//for (int i = 0; i < 1; i++) {
				// 乘以2是因为十六进制比如a1使用两个字符代表一个byte,pos代表的是数组的位置
				// 第一个16进制数的起始位置是0第二个是2以此类推
				//int pos = i * 2;
				// <<4位就是乘以16 比如说十六进制的"11",在这里也就是1*16|1,而其中的"|"或运算就相当于十进制中的加法运算
				// 如00010000|00000001结果就是00010001
			
				result = (byte) (toByte(achar[0]) << 4 | toByte(achar[1]));
				Log.d("Main", toByte(achar[0])+"");
				result &= 0xff;
			//}
		}
		else{
	      result=0;
		}
		return result;	
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789abcdef".indexOf(c);
		return b;
	}

	private static byte[] StringtoByte(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0;i<bytes.length;i++){
			bytes[i] = (byte) Integer.parseInt(str.substring(i*2,i*2 +2),16);
		}
		return bytes;
	}

}
