package com.ble.main;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.ConnectException;
import com.example.android.bluetoothlegatt.exception.TimeoutException;
import com.example.android.bluetoothlegatt.exception.TransportException;
import com.example.android.bluetoothlegatt.proltrol.LPUtil;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.traffic.BleImplement;
import com.example.android.bluetoothlegatt.traffic.BleListener;
import com.example.android.bluetoothlegatt.traffic.ConnectCallback;
import com.example.android.bluetoothlegatt.wapper.CmdFinder;

/**
 * Created by Daniel.Xu on 2017/5/27.
 */

public class TrafficActivity extends Activity {


    private Button connect;
    private BleImplement bleImplement;
    private Button powerOn;
    private Button disconnect;
    private Button reOpen;
    private Button sendData;
    private Button powerOff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        bleImplement = new BleImplement(TrafficActivity.this);
        bleImplement.registerConnectCallback(new ConnectCallback() {
            @Override
            public void setConnectStatus(boolean isConnect) {
                Log.e("test","isConnect"+isConnect);
            }
        });
        initview();
        initListener();

    }

    private void initview() {
        connect = (Button) findViewById(R.id.connect);
        powerOn = (Button) findViewById(R.id.poweron);
        disconnect = (Button) findViewById(R.id.disconnect);
        reOpen = (Button) findViewById(R.id.reopen);
        sendData = (Button) findViewById(R.id.senddata);
        powerOff = (Button) findViewById(R.id.poweroff);


    }

    private void initListener() {
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] open = bleImplement.open("B0:B4:48:F7:3D:25", 6000, new byte[]{});
                    LPUtil.printData(open,"open返回的是");
//                    bleImplement.provider.connect_mac("B0:B4:48:F7:3D:25");
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    Log.e("test","已经超时了");
                }
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleImplement.close();
            }
        });
        reOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bleImplement.reopen(2000);
                } catch (TimeoutException e) {

                }
            }
        });



        powerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bleImplement.powerOn(2000);
                } catch (ConnectException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    Log.e("test","链接超时");
                    e.printStackTrace();
                }
            }
        });

        //sendData 发送透传数据
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bleImplement.exchangeWithData(new byte[]{0x00, 0x00},4000);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    e.printStackTrace();
                } catch (TransportException e) {
                    e.printStackTrace();
                }
            }
        });
        powerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = bleImplement.powerOff();
            }
        });



    }




}
