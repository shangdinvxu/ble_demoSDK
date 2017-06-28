package com.ble.main.testview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ble.main.R;
import com.ble.main.service.Bleservice;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.ConnectBLEFactoryImpl;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;

public class LingnantongActivity extends AppCompatActivity {

    private RelativeLayout layout;

    private Button connect;  //连接蓝牙设备
    private Button ble_state;//状态
    private Button send_data;//发送数据
    private Button poweron;//上电
    private Button poweroff;//下电
    private Button disconnect;//断开连接

    private BLEProvider provider;
    private ConnectBLEFactoryImpl connectFactoryImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lingnantong);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "岭南通测试页面", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initView();
        initListener();
//        initData();
    }



    private void initView() {
        layout = (RelativeLayout) findViewById(R.id.layout);
        connect = (Button) findViewById(R.id.connect);
        ble_state = (Button) findViewById(R.id.ble_state);
        send_data = (Button) findViewById(R.id.send_data);
        poweron = (Button) findViewById(R.id.poweron);
        poweroff = (Button) findViewById(R.id.poweroff);
        disconnect = (Button) findViewById(R.id.disconnect);
    }

    private void initListener() {
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View View_ =view;

                connectFactoryImpl.connection(LingnantongActivity.this,"B0:B4:48:CE:56:62", new ConnectReturnImpl() {
                    @Override
                    public void connectResult(boolean isConnect, String mac) {
                        if(isConnect){
                            Snackbar.make(layout, "蓝牙连接成功....MAC:"+mac, Snackbar.LENGTH_LONG) .setAction("Action", null).show();
                            Log.e("LingnantongActivity","蓝牙连接成功....MAC:"+mac);
                        }
                        else{
                            Snackbar.make(layout, "蓝牙连接失败....MAC:"+mac, Snackbar.LENGTH_LONG) .setAction("Action", null).show();
                            Log.e("LingnantongActivity","蓝牙连接失败....MAC:"+mac);
                        }
                    }
                });

            }
        });

        ble_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = (Boolean) connectFactoryImpl.getConnectState();
                Snackbar.make(view, state+"", Snackbar.LENGTH_LONG) .setAction("Action", null).show();
                Log.e("LingnantongActivity",state+"");
            }
        });

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] request_data = {0x5f,0}; //这里可以自定义
				byte[] response_data = connectFactoryImpl.transmit(request_data);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < response_data.length; i++)
				{
					sb.append(Integer.toHexString((response_data[i] & 0xff)) + " ");
				}
                Snackbar.make(view, sb.toString()+"", Snackbar.LENGTH_LONG) .setAction("Action", null).show();
				Log.e("LingnantongActivity",sb.toString()+"");
            }
        });

        poweron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = (Boolean) connectFactoryImpl.powerOn();
				Log.e("LingnantongActivity",state+"");
                Snackbar.make(view, "上电:"+state, Snackbar.LENGTH_LONG) .setAction("Action", null).show();
            }
        });

        poweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = (Boolean) connectFactoryImpl.powerOff();
				Log.e("LingnantongActivity",state+"");
                Snackbar.make(view, "下电:"+state, Snackbar.LENGTH_LONG) .setAction("Action", null).show();
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = (Boolean) connectFactoryImpl.closeConnection();
                Log.e("LingnantongActivity",state+"");
                Snackbar.make(view, "断开连接:"+state, Snackbar.LENGTH_LONG) .setAction("Action", null).show();
            }
        });
    }

//    private void initData() {
//        provider = Bleservice.getInstance(this).getCurrentHandlerProvider();
//        Log.e("LingnantongActivity",provider+"");
//        connectFactoryImpl = new ConnectBLEFactoryImpl(provider);
//    }
}
