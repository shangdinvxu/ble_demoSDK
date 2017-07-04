package com.example.android.bluetoothlegatt.traffic;

import com.example.android.bluetoothlegatt.exception.ConnectException;
import com.example.android.bluetoothlegatt.exception.DeleteDeviceException;
import com.example.android.bluetoothlegatt.exception.TimeoutException;
import com.example.android.bluetoothlegatt.exception.TransportException;


/**
 * Created by Daniel.Xu on 2017/5/19.
 */

public interface CtticReader {

    /**
     *
     该方法用于连接设备，需要上层传递设备地址和连接超时等待，并需要抛出连接超时的
     异常。
     参数
     address: 设备地址，一般为设备的 MAC 地址；
     timeOut: 最长连接等待时间，单位毫秒
     scanInfo: 蓝牙设备的广播数据，Android 扫描蓝牙设备时返回的广播数据原始信
     息，public void onLeScan(BluetoothDevice device,int rssi,byte[] scanRecord)函数
     的 scanRecord 参数。
     */
    public byte[] open(String address, long timeOut, byte[] scanInfo)
            throws TimeoutException;

    /**
     * 该方法用于关闭设备。
     参数
     无
     返回
     无
     */
    public void close() ;


    /***
     *
     该方法用于使用上次成功连接的设备信息，进行重新连接。
     参数
     timeOut: 最长连接等待时间，单位毫秒
     返回
     若重新连接成功，返回 true；否则返回失败
     异常
     TimeOutException: 上电超时异常
     */
    public boolean reopen(long timeOut) throws TimeoutException;


    /***
     *
     该方法用于注册设备回调函数。
     参数
     callback: ConnectCallback 回调函数，由上层传入
     返回
     无
     */
    public void registerConnectCallback(ConnectCallback callback);


//    /***
//     *
//     该方法用于绑定设备
//     参数
//     type: 绑定类型（枚举类，目前支持 PRESS_BUTTON（按钮绑定方式）、
//     KNOCK_BAND（敲击手环方式）和 AUTH_CODE（输入认证码方式））
//     data: 绑定数据
//     phoneNum: 用户注册手机号
//     返回
//     若绑定成功，返回 true；否则返回失败
//     异常
//     ConnectException: 设备连接处理异常；
//     TimeoutException: 命令执行超时异常；
//     */
//    public boolean bind(BindType type, String data, String phoneNum)
//            throws ConnectException ,TimeoutException;


    /***
     * 该方法用于获取当前 API 版本
     参数
     无
     返回
     5
     API 版本号，如 1.0.0.1
     * @return
     */
    public String getAPIVersion() ;


//    /**
//     *
//     该方法用于获取当前连接的设备固件版本
//     参数
//     无
//     返回
//     设备固件版本号，如 1.0.0.1
//     异常
//     ConnectException：设备连接异常
//     */
//    public String getDeviceVersion() throws ConnectException;


//    /***
//     * 该方法用于获取当前连接的设备 id 号
//     参数
//     无
//     返回
//     设备 id 号，其实就是 pamid
//     异常
//     ConnectException: 设备连接异常;
//     TimeOutException，上电超时异常
//     */
//    public byte[] getDeviceId() throws ConnectException,
//    TimeoutException;


    /***
     *
     该方法用于 SE 芯片的上电操作
     参数
     6
     timeOut: 上电超时时间，单位毫秒
     返回
     上电成功后的 atr
     异常
     ConnectException: 设备连接异常；
     TimeOutException，上电超时异常
     */
    public byte[] powerOn(long timeOut) throws ConnectException,
            TimeoutException;





    /***
     *
     该方法用于与 SE 芯片进行数据透传
     参数
     data: 透传数据；
     timeOut: 发送超时时间，单位毫秒
     返回
     SE 返回数据
     异常
     TimeoutException: 命令执行超时异常；
     ConnectException: 设备连接处理异常；
     TransportException: 命令执行异常；
     */
    public byte[] exchangeWithData(byte[] data, long timeOut) throws
            TimeoutException, ConnectException, TransportException;


    /**
     *
     该方法用于 SE 芯片的下电操作
     参数
     无
     返回
     若下电成功，返回 true；否则返回失败
     */
    public boolean powerOff();


    /**
     * 删除设备后台状态信息
     参数
     phoneNum：用户手机号
     返回
     删除是否成功
     异常
     DeleteDeviceException: 删除设备异常；
     */
    public void deleteDevice(String phoneNum) throws
            DeleteDeviceException;


}
