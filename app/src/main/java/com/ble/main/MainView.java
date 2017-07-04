package com.ble.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.list.BLEListActivity;
import com.ble.list.RenameView;
import com.ble.list.SendView;
import com.ble.main.service.Bleservice;
import com.ble.main.testview.DrawActivity;
import com.ble.main.testview.LingnantongActivity;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LPException;
import com.example.android.bluetoothlegatt.proltrol.LPUtil;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.proltrol.WatchRequset;
import com.example.android.bluetoothlegatt.proltrol.WatchResponse;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.traffic.BleImplement;
import com.example.android.bluetoothlegatt.traffic.BleListener;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainView extends AppCompatActivity {

    private static final String TAG = MainView.class.getSimpleName();

    /**********
     * 界面布局类
     *********/
    private CoordinatorLayout layout;
    private Button scan, sync, clock, longSit, power, notifica,aid,afteraid,afteraid2;
    private Button opencard, sportData;
    private Button senddata;
    private Button function_test; //设置名称
    private Button test_lingnantong; //岭南通充值
    private Button view_btn; //图形绘制界面
    private TextView status;
    private Toolbar toolbar;
    /*********
     * 界面布局类
     *********/
    private BLEProviderObserverAdapter bleProviderObserver;

    private BLEProvider provider;

	/*private Blereciver blereciver;*/

    private ProgressDialog dialog_connect ;
    private AlertDialog dialog_bound;
    public static final int RESULT_OTHER = 1000;
    public static final int RESULT_BACK = 999;
    public static final int RESULT_FAIL = 998;
    public static final int RESULT_NOCHARGE = 997;
    public static final int RESULT_DISCONNECT = 996;

    private int button_txt_count = 40;
    private Object[] button_txt = {button_txt_count};

    public static final int REFRESH = 0x123;
    public static final int sendcount_MAX = 15;
    private int sendcount = 0;
    public static final int sendcount_time = 2000;
    private Timer timer;
    private TextView textview;
    private byte seq = 0;
    public static final LepaoProtocalImpl LEPAO_PROTOCAL = new LepaoProtocalImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        dialog_connect = new ProgressDialog(MainView.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "onCreate");

        provider = Bleservice.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        initview();
        initBluetooth();
        initListener();

     /*   byte[] bytes = new byte[300];
        provider.send_dataTimeoutWithTimes(MainView.this,bytes,20);*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        refreshview();
        if (provider.getBleProviderObserver() == null) {
            provider.setBleProviderObserver(bleProviderObserver);
        }


    }

    protected void onDestroy() {
        super.onDestroy();
        if (provider.getBleProviderObserver() == bleProviderObserver)
            provider.setBleProviderObserver(null);

        Bleservice.getInstance(this).releaseBLE();
        stopservice();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null) {

            String mac = data.getStringExtra("mac");

            provider.setCurrentDeviceMac(mac);

            if (provider.isConnectedAndDiscovered()) {
                provider.disConnect();
            }

            Log.i(TAG, "开始连接");
//            provider.connect_mac(mac);

            String title = "连接中...";
            String message = "正在连接Mac地址为：" + mac + "的设备！";
            dialog_connect = ProgressDialog.show(MainView.this, title, message);
            dialog_connect.show();

            dialog_bound = new AlertDialog.Builder(MainView.this)
                    .setTitle(R.string.portal_main_isbounding)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    })
                    .setCancelable(false).create();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                timer = new Timer(); // 每分钟更新一下蓝牙状态
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        boundhandler.post(butttonRunnable);
                        button_txt_count--;
                        Log.e(TAG, "Timer开始了");
                        if (button_txt_count < 0) {
                            timer.cancel();
                        }
                    }
                }, 0, 1000);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.close_ble) {
            if (provider.isConnectedAndDiscovered()) {
                Bleservice.getInstance(this).releaseBLE();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initBluetooth() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
//判断是否需要 向用户解释，为什么要申请该权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initview() {
        layout = (CoordinatorLayout) findViewById(R.id.layout);
        scan = (Button) findViewById(R.id.scan);
        function_test = (Button) findViewById(R.id.function_test);
        senddata = (Button) findViewById(R.id.senddata);
        status = (TextView) findViewById(R.id.status);
        test_lingnantong = (Button) findViewById(R.id.test_lingnantong);
        view_btn = (Button) findViewById(R.id.view_btn);
        opencard = (Button) findViewById(R.id.kaika);
        sportData = (Button) findViewById(R.id.SportData);
        sync = (Button) findViewById(R.id.sync);
        clock = (Button) findViewById(R.id.clock);
        longSit = (Button) findViewById(R.id.longSit);
        notifica = (Button) findViewById(R.id.notifica);
        power = (Button) findViewById(R.id.power);
        aid = (Button) findViewById(R.id.aid);
        afteraid = (Button) findViewById(R.id.afteraid);
        afteraid2 = (Button) findViewById(R.id.afteraid2);


    }



    private void initListener() {
        refreshview();

        scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainView.this, BLEListActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        senddata.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (provider.isConnectedAndDiscovered()) {
                    Intent intent = new Intent(MainView.this, SendView.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(layout, getString(R.string.ble_not_connect), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
                }
            }
        });

        function_test.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Snackbar.make(layout, getString(R.string.no_permission), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
                Intent intent = new Intent(MainView.this, RenameView.class);
                startActivity(intent);
            }
        });

        test_lingnantong.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainView.this, LingnantongActivity.class);
                startActivity(intent);
            }
        });

        view_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainView.this, DrawActivity.class);
                startActivity(intent);
            }
        });

        opencard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                try {
//                    lepaoProtocal.opencard();
                    lepaoProtocal.closeSmartCard();
                    lepaoProtocal.openSmartCard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        aid.setOnClickListener(new OnClickListener() {
            /**
             * @param v
             */
            @Override
            public void onClick(View v) {
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                try {
//00a404 0008a0 00 00 06 32 01 01 05
                    byte[] aids =  {0x00, (byte) 0xa4, 0x04, 0x00, 0x08, 0x52, 0x65, 0x73, 0x70, 0x2e, 0x61, 0x70, 0x70};
                    byte[] senddata = lepaoProtocal.senddata(aids);
                    LPUtil.printData(senddata,"发aid返回的是++++++++++++++");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        afteraid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                00b095 00 00
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                try {
                    byte[] afteraid ={0x00, 0x02, 0x00, 0x00, (byte) 0xFF};
                    byte[] senddata = lepaoProtocal.senddata(afteraid);
                    LPUtil.printData(senddata,"发送afteraid返回的是++++++++++++++");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        afteraid2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                00b095 00 00
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                try {
                    byte[] afteraid2 ={0x00, 0x03, 0x00, 0x00, (byte) 0xff,
                            0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                            0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff,
                              0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff}
                            ;
                    byte[] senddata = lepaoProtocal.senddata(afteraid2);
                    LPUtil.printData(senddata,"发送afteraid2返回的是++++++++++++++");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        sportData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                try {
                    lepaoProtocal.getSportDataNew(0xff, 0x7f, 0);
                } catch (BLException e) {
                    e.printStackTrace();
                } catch (LPException e) {
                    e.printStackTrace();
                }
            }
        });
        /*********************************Demo*********************************/
        //同步设备数据
        sync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
                lpDeviceInfo.userId = "111111";
                provider.getAllDeviceInfoNew(MainView.this, lpDeviceInfo);
                //见下面回调的updateFor_notifyFor0x13ExecSucess_D
            }
        });
        //闹钟
        clock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //"127"代表每天都提醒 clock1Switch 1是开
            //	String clock1 = clockoneHr+":"+clockoneMin+"-"+"127"+"-"+clock1Switch;

                LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
                lpDeviceInfo.alarmTime1_H = 10;
                lpDeviceInfo.alarmTime1_M = 10;
                lpDeviceInfo.frequency1 = 127;
                provider.SetClock(MainView.this, lpDeviceInfo);




            }
        });
        //久坐
        longSit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
                lpDeviceInfo.longsit_step = 1000;
                lpDeviceInfo.startTime1_H = 10;
                lpDeviceInfo.startTime1_M = 10;
                lpDeviceInfo.endTime1_H = 10;
                lpDeviceInfo.endTime1_M = 20;
                lpDeviceInfo.startTime2_H = 11;
                lpDeviceInfo.startTime2_M = 23;
                lpDeviceInfo.endTime2_H = 11;
                lpDeviceInfo.endTime2_M = 25;
                provider.SetLongSit(MainView.this, lpDeviceInfo);*/
                byte[] bytes ={(byte)0x95,(byte)0x00,(byte)0xA4,(byte)0x04,
                        (byte)0x00,(byte)0x06,(byte)0x11,(byte)0x22,
                        (byte)0x33,(byte)0x44,(byte)0x55,(byte)0x81
                };
                provider.send_dataTimeoutWithTimes(MainView.this,bytes,20);
            }
        });
        //消息提醒
        notifica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String notif = ""+1+1+1+1+1 ;
                Log.e("notify---------",notif);
                int i = Integer.parseInt(notif, 2);
                byte[] bytes = new byte[2];
                bytes[0] = (byte)((0xff&i));
                bytes[1] = (byte) (0xff&(i>>8));
                LPUtil.printData(bytes,"notify---------");
                provider.setNotification(MainView.this,bytes);
            }
        });
        //节能
        power.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
//                lpDeviceInfo.deviceStatus = 1;
//                provider.SetPower(MainView.this, lpDeviceInfo);
//                00a404
// 0008a0
// 00000003454944
                byte[] open = {(byte)0x00,(byte)0xA4,(byte)0x04,(byte)0x00,(byte)0x08,
                        (byte)0xA0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03
                        ,(byte)0x45,(byte)0x49,(byte)0x44};
                LepaoProtocalImpl lepaoProtocal = new LepaoProtocalImpl();
                WatchRequset watchRequset = new WatchRequset();
                WatchResponse watchResponse = null ;
                try {
                    watchRequset.appendByte((byte)(seq+=2)).appendByte(open).makeCheckSum();
                    LPUtil.printData(watchRequset.getData(),"发送的指令是:");
                    watchResponse  = lepaoProtocal.sendData2BLEWithTime(watchRequset, 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LPUtil.printData(watchResponse.getData(),"接收的指令是:");

            }
        });




        /******************************************************************/


    }

    Runnable butttonRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = REFRESH;
            boundhandler.sendMessage(msg);
        }

        ;
    };


    Runnable boundRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            boundhandler.sendMessage(msg);
        }

        ;
    };

    Handler boundhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x333:
                    provider.requestbound(MainView.this);
                    break;
                case REFRESH:
                    String second_txt = MessageFormat.format(getString(R.string.bound_scan_sqr), button_txt);
                    if (button_txt_count == 0) {
                        if (dialog_bound != null && dialog_bound.isShowing()) {
                            dialog_bound.dismiss();
                        }
                        Log.e(TAG, "REFRESH");
                        setResult(RESULT_FAIL);
                        finish();
                    }
                    break;
            }
        }

        ;
    };


    private void refreshview() {
        if (provider.isConnectedAndDiscovered()) {
            //是连接不是绑定,绑定成功要按手表上面的是.
            status.setText("已经连接");
        } else {
            status.setText("未连接");
        }
    }

    private void stopservice() {
        Intent stopIntent = new Intent(this, Bleservice.class);
        Log.e(TAG, "App结束后,停止服务!");
        stopService(stopIntent);//停止服务
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Builder builder = new Builder(MainView.this);
                builder.setTitle("提示");
                builder.setMessage("你确定要退出吗？");
                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            arg0.cancel();
                        } else if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
                            MainView.this.finish();
                        }
                    }
                };
                builder.setPositiveButton("取消", dialog);
                builder.setNegativeButton("确定", dialog);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }
        return false;
    }


    private class BLEProviderObserverAdapterImpl extends BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return MainView.this;
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            super.updateFor_handleConnectSuccessMsg();
            Log.i(TAG, "连接成功！");
            refreshview();
            dialog_connect.dismiss();
            Snackbar.make(layout, getString(R.string.connect_success), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
        }

        @Override
        public void updateFor_BoundContinue() {
            super.updateFor_BoundContinue();
            if (sendcount < sendcount_MAX) {
                boundhandler.postDelayed(boundRunnable, sendcount_time);
                sendcount++;
            } else {
                Log.e("BLEListActivity", "已经发送超出15次");
                provider.clearProess();
                setResult(RESULT_FAIL);
                finish();
            }
        }

        @Override
        public void updateFor_BoundSucess() {
            provider.SetDeviceTime(MainView.this);
            if (dialog_bound != null && dialog_bound.isShowing()) {
                if (timer != null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            //获取成功
            startBound();
        }

        private void startBound() {

        }


        @Override
        public void updateFor_handleConnectLostMsg() {
            // TODO Auto-generated method stub
            super.updateFor_handleConnectLostMsg();
            Log.i(TAG, "断开连接！");
            refreshview();
            dialog_connect.dismiss();
            Snackbar.make(layout, getString(R.string.ble_disconnect), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
        }

        //设置之后的回调
        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
            Log.e("MainView",latestDeviceInfo.toString()+"updateFor_notifyFor0x13ExecSucess_D");

        }

        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
            Log.e("MainView","updateFor_notifyForSetClockSucess");

        }


        @Override
        public void updateFor_notifyForSetPowerSucess() {
            super.updateFor_notifyForSetPowerSucess();
            Log.e("MainView","updateFor_notifyForSetPowerSucess");

        }

        //设置超时时间返回的数据.
        @Override
        public void updataFor_notifyForSendDataSixtyTimeout(byte[] data) {
            Log.e("MainView","updataFor_notifyForSendDataSixtyTimeout");
            LPUtil.printData(data,"接收的数据是:");
        }



        @Override
        public void notifyForSetNOTIFYFail() {
            super.notifyForSetNOTIFYFail();
            Log.e("MainView","notifyForSetNOTIFYFail");
        }


        @Override
        public void updateFor_notifyForLongSitSucess() {
            super.updateFor_notifyForLongSitSucess();
            Log.e("MainView","updateFor_notifyForLongSitSucess");
        }

        @Override
        public void updateFor_notifyForLongSitFail() {
            super.updateFor_notifyForLongSitFail();
            Log.e("MainView","updateFor_notifyForLongSitFail");
        }
    }
}
