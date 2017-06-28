package com.ble.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.example.android.bluetoothlegatt.exception.ConnectException;
import com.example.android.bluetoothlegatt.exception.TimeoutException;
import com.example.android.bluetoothlegatt.exception.TransportException;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.traffic.BleImplement;
import com.example.android.bluetoothlegatt.traffic.BleListener;
import com.example.android.bluetoothlegatt.wapper.CmdFinder;

/**
 * Created by Daniel.Xu on 2017/5/27.
 */

public class TrafficActivity extends Activity {

    private BleImplement mBleImplement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBleImplement = new BleImplement(TrafficActivity.this, new MyBleListener());
        mBleImplement.close();
        /**透传指令提供了一种同步的方式 异步就用 exchangeWithData()这个指令;*/
        byte[] data = {0x00,0x00};
        try {
            byte[] bytes = mBleImplement.exchangeWithData_Sync(data, 5);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        }
    }

    public class MyBleListener extends BleListener {


        /**
         * 上电成功
         */
        public void openCardSuccess() {

        }


        /**
         *
         * @param data  芯片进行数据透传
         */
        public void responseBleCard(byte[] data) {

        }


        /**
         * 设备解绑成功
         */
        public void deviceUnbundSuccess() {

        }


        /**
         * 读取设备信息返回后的回调
         * @param latestDeviceInfo
         */
        public void notifyForDeviceInfo(LPDeviceInfo latestDeviceInfo) {

        }


        /**
         * 连接失败的回调
         */
        public void connectFailed() {

        }


        /**
         * 发送数据失败的回调
         */
        public void sendDataError() {

        }


        /**
         * 连接断开的回调
         */
        public void connectLost() {

        }

        /**
         * 连接成功的回调
         */
        public void connectSuccess(){
        }

    }

}
