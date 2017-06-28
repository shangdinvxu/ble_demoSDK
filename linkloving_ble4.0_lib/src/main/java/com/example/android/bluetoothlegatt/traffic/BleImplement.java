package com.example.android.bluetoothlegatt.traffic;

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


/**
 * Created by Daniel.Xu on 2017/5/19.
 */

public class BleImplement implements CtticReader {
    private static  final String  apiVersion = "1.0.0" ;
    private final BLEProvider provider;
    private Context context ;

    public BleImplement (Context context, final BleListener bleListener ){
        this.context = context ;
        provider = new BLEProvider(context);
        provider.setProviderHandler(new BLEHandler(context) {
            @Override
            protected BLEProvider getProvider() {
                return provider;
            }

            @Override
            protected void handleConnectSuccessMsg() {
                super.handleConnectSuccessMsg();
                bleListener.connectSuccess();
            }

            @Override
            protected void handleConnectLostMsg() {
                super.handleConnectLostMsg();
                bleListener.connectLost();
            }

            @Override
            protected void handleSendDataError() {
                super.handleSendDataError();
                bleListener.sendDataError();
            }


            @Override
            protected void handleConnectFailedMsg() {
                super.handleConnectFailedMsg();
                bleListener.connectFailed();
            }


            @Override
            protected void notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
                super.notifyFor0x13ExecSucess_D(latestDeviceInfo);
                bleListener. notifyForDeviceInfo(latestDeviceInfo);
            }


            @Override
            protected void notifyForDeviceUnboundSucess_D() {
                super.notifyForDeviceUnboundSucess_D();
                bleListener.deviceUnbundSuccess();
            }

            @Override
            protected void notifyFor_response_ble_card(byte[] data) {
                super.notifyFor_response_ble_card(data);
                bleListener.responseBleCard(data);
            }

            @Override
            protected void notifyForOpenSmc(Object obj) {
                super.notifyForOpenSmc(obj);
                bleListener.openCardSuccess();
            }
        });
    }



    @Override
    public void open(String address, long timeOut, byte[] scanInfo) throws TimeoutException {
        provider.connect_mac(address);
    }

    @Override
    public void close() {
        provider.disConnect();
    }

    @Override
    public void reopen(long timeOut) throws TimeoutException {
        provider.connect();
    }


    @Override
    public String getAPIVersion() {
        return apiVersion;
    }

    @Override
    public void powerOn(long timeOut) throws ConnectException, TimeoutException {
        provider.openSmartCard(context);
    }

    @Override
    public void exchangeWithData(byte[] data, long timeOut) throws TimeoutException, ConnectException, TransportException {
        provider.send_data2ble_card(context,data);
    }

    public byte[] exchangeWithData_Sync(byte[] data, long timeOut) throws TimeoutException, ConnectException, TransportException {
        LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
        byte[] senddata = null ;
        try {
            senddata = lepaoProtocal.senddata(data);
        } catch (BLException e) {
            e.printStackTrace();
        } catch (LPException e) {
            e.printStackTrace();
        }
        return senddata;
    }


    @Override
    public void powerOff() {
        provider.closeSmartCard(context);
    }

    @Override
    public void deleteDevice(String phoneNum) throws DeleteDeviceException {
        provider.unBoundDevice(context);
    }
}
