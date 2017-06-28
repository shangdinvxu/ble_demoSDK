package com.ble.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.main.R;
import com.ble.main.service.Bleservice;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;

import java.util.ArrayList;
import java.util.List;

public class BLEListActivity extends Activity 
{
	private static final String TAG = BLEListActivity.class.getSimpleName();
	private BLEListProvider listProvider;
	private BLEProvider provider;
	private BLEListHandler handler;
	
	private ListView mListView;
	private List<DeviceVO> macList = new ArrayList<DeviceVO>();
	
	private macListAdapter mAdapter ;
	
	
	private Button backBtn;

	private Button refresh;



	BLEHandler.BLEProviderObserverAdapter observerAdapter=null;
			
     @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.activity_ble_list);

    	/*observerAdapter=new BLEProviderObserverAdapter(){

			@Override
			protected Activity getActivity() {
				return BLEListActivity.this;
			}
			
			@Override
			public void updateFor_handleConnectLostMsg() {
				Log.e(TAG, "连接失败！");
				Toast.makeText(BLEListActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
			if(dialog_connect!=null || dialog_connect.isShowing())
					dialog_connect.dismiss();
				finish();
		}

			@Override
			public void updateFor_handleConnectSuccessMsg() {
				super.updateFor_handleConnectSuccessMsg();
				Log.i(TAG, "连接成功！");
				if(dialog_connect!=null || dialog_connect.isShowing())
				dialog_connect.dismiss();
				finish();
			}
			
			
    		
    	};*/

    	/*provider=Bleservice.getInstance(this).getCurrentHandlerProvider();

    	provider.setBleProviderObserver(observerAdapter);*/

    	handler = new BLEListHandler(BLEListActivity.this) 
    	{
			@Override
			protected void handleData(BluetoothDevice device) 
			{
				for(DeviceVO v:macList)
				{
					if(v.mac.equals(device.getAddress()))
					     return;
				}
				DeviceVO vo = new DeviceVO();
				vo.mac = device.getAddress();
				vo.name = device.getName();
				vo.bledevice=device;

				macList.add(vo);

				mAdapter.notifyDataSetChanged();
			}
		};
    	
    	listProvider = new BLEListProvider(this, handler);

    	mAdapter = new macListAdapter(this, macList);
    	
    	initView();

    	listProvider.scanDeviceList();
    }
     

     
     private void initView()
     {
    	 mListView = (ListView) findViewById(R.id.ble_list);
    	 mListView.setAdapter(mAdapter);
    	 mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)

			{

				//将选择的mac地址传过去
				Intent intent=new Intent();
				intent.putExtra("mac",macList.get(index).mac);
				//provider.stopScan(0);
				setResult(RESULT_OK,intent);
				finish();

				/*	provider.setCurrentDeviceMac(macList.get(index).mac);

				provider.setmBluetoothDevice(macList.get(index).bledevice);

				if(provider.isConnectedAndDiscovered()){
					provider.disConnect();
				}

				provider.connect();

				String title = "连接中...";
				String message = "正在连接Mac地址为："+macList.get(index).mac+"的设备！";
				dialog_connect  = ProgressDialog.show(BLEListActivity.this, title, message);
				dialog_connect.show();*/
				
			}
		});
    	 
    	 backBtn = (Button) findViewById(R.id.button1);
    	 backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	 refresh = (Button) findViewById(R.id.refresh);
    	 refresh.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 macList.clear();
				 mAdapter.notifyDataSetChanged();
				 listProvider.scanDeviceList();
			}
		});
     }
     
     class DeviceVO
     {
    	 public String mac;
    	 public String name;
    	 public BluetoothDevice bledevice;

     }
     
     class macListAdapter extends CommonAdapter<DeviceVO>
     {
    	 public class ViewHolder
    	 {
    		 public TextView mac;
    		 public TextView name;

    	 }
    	 ViewHolder holder;

		public macListAdapter(Context context, List<DeviceVO> list) {
			super(context, list);
		}
		
		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent) {
			convertView = inflater.inflate(R.layout.list_item_ble_list, parent,false);
		    holder = new ViewHolder();
			holder.mac = (TextView) convertView.findViewById(R.id.activity_sport_data_detail_sleepSumView);
			holder.name = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView,
				ViewGroup parent) {
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(int position, View convertView,
				ViewGroup parent) {
			holder.mac.setText(list.get(position).mac);
			holder.name.setText(list.get(position).name);
			return convertView;
		}
    	 
     }
     
     	@Override
		protected void onResume() {
		super.onResume();
	/*	if(provider!=null){

			provider=Bleservice.getInstance(this).getCurrentHandlerProvider();

			provider.setBleProviderObserver(observerAdapter);
		}*/


 	}
     	
     	@Override
     	protected void onDestroy() {
     		super.onDestroy();
     		/*provider=Bleservice.getInstance(this).getCurrentHandlerProvider();
 			if(provider != null)
 				provider.setBleProviderObserver(null);*/
 	}
     
     
     
}
