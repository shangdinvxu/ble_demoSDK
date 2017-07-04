package com.example.android.bluetoothlegatt.traffic;

import android.app.Activity;
import android.content.Context;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.exception.ConnectException;
import com.example.android.bluetoothlegatt.exception.DeleteDeviceException;
import com.example.android.bluetoothlegatt.exception.TimeoutException;
import com.example.android.bluetoothlegatt.exception.TransportException;
import com.example.android.bluetoothlegatt.proltrol.LPException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;


/**
 * Created by Daniel.Xu on 2017/5/19.
 */

public class BleImplement implements CtticReader{

    private static  final String  apiVersion = "1.0.0" ;
    public final BLEProvider provider;
    private Context context ;
    private ConnectCallback connectCallback;

    public BleImplement (Context context ){
        this.context = context ;
        provider = new BLEProvider(context);
        provider.setBleProviderObserver(new BLEProviderObserverAdapterImpl());

    }

    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return (Activity)context;
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            super.updateFor_handleConnectSuccessMsg();
            if (connectCallback!=null){
                connectCallback.setConnectStatus(true);
            }
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            super.updateFor_handleConnectLostMsg();
            if (connectCallback!=null){
                connectCallback.setConnectStatus(false);
            }
        }
    }



    @Override
    public byte[] open(String address, long timeOut, byte[] scanInfo) throws TimeoutException {
       return provider.open(address,timeOut,scanInfo);
    }

    @Override
    public void close() {
        provider.disConnect();
    }

    @Override
    public boolean reopen(long timeOut) throws TimeoutException {
        return provider.reopen(timeOut);
    }

    @Override
    public void registerConnectCallback(ConnectCallback callback) {
        this.connectCallback =callback;
    }


    @Override
    public String getAPIVersion() {
        return apiVersion;
    }

    @Override
    public byte[] powerOn(long timeOut) throws ConnectException, TimeoutException {
        return provider.powerOn(timeOut);
    }

    @Override
    public byte[] exchangeWithData(byte[] data, long timeOut) throws TimeoutException, ConnectException, TransportException {
       return provider.exchangeWithData(data,timeOut);
    }




    @Override
    public boolean powerOff() {
        return provider.powerOff();
    }

    @Override
    public void deleteDevice(String phoneNum) throws DeleteDeviceException {
        provider.unBoundDevice(context);
    }


}
