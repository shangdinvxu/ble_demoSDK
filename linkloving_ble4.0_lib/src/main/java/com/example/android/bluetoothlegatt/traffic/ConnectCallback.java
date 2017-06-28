package com.example.android.bluetoothlegatt.traffic;

/**
 * Created by Daniel.Xu on 2017/5/19.
 */

public interface ConnectCallback {
    /**
     删除设备后台状态信息
     参数
     isConnect：true 设备连上了
     false 设备断开了
     返回
     无
     */
    public void setConnectStatus(boolean isConnect) ;
}
