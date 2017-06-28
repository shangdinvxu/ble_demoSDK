package com.example.android.bluetoothlegatt.traffic;

import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;

/**
 * Created by Daniel.Xu on 2017/5/27.
 */

public class BleListener {

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
