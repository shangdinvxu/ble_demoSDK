package com.ble.main.testview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ble.list.BLEListActivity;
import com.ble.main.R;
import com.ble.main.service.Bleservice;
import com.ble.utils.ByteUtils;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.download.DownloadListener;

import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

;

public class DrawActivity extends AppCompatActivity {

    private int persent;

    private static final String TAG = DrawActivity.class.getSimpleName();
    private TextView tishi,choose,banben;
    private Button net_btn,home_btn,link;
    public final static int DEVICE_TYPE_WATCH = 3;
    public final static int DEVICE_VERSION_TYPE = DEVICE_TYPE_WATCH - 1;

    //网络请求队列
    private RequestQueue requestQueue;

    /**
     * 下载队列.
     */
    private static DownloadQueue downloadQueue;

    JSONArray array;

    private byte[] databytes;

    private String version_uid;

    private String address;

    private String version_hex;

    private final int PROGRESS_DIALOG=100;

    private  ProgressDialog progressDialog;



    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    private BLEProvider provider;

    private ProgressDialog dialog_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue= NoHttp.newRequestQueue();

        downloadQueue=NoHttp.newDownloadQueue();

        provider = Bleservice.getInstance(this).getCurrentHandlerProvider();

        bleProviderObserver = new BLEProviderObserverAdapterImpl();

        provider.setBleProviderObserver(bleProviderObserver);

        initView();
        initListener();
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
           dialog_connect.dismiss();

            Toast.makeText(DrawActivity.this, "下载文件失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
            Log.i(TAG,"下载开始");


            String title = "下载";

            String message = "正在下载文件请稍后!";

            dialog_connect  = ProgressDialog.show(DrawActivity.this, title, message);

            dialog_connect.show();

        }

        @Override
        public void onProgress(int what, int progress, long downCount) {
            Log.i(TAG,"正在下载");

        }

        @Override
        public void onFinish(int what, String filePath) {

            dialog_connect.dismiss();
            Log.i(TAG, "下载完成" + filePath);
         //下载完成,显示开始写入固件

            File file=new File(filePath);

            showDialog(file.getName(),true,null);
        }


        @Override
        public void onCancel(int what) {
            Log.i(TAG,"下载取消");
        }

    };


    private void showDialog(String str1,String str2){

        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);

        builder.setMessage(str2);

        builder.setTitle(str1);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始执行写入
                dialog.dismiss();
                }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showDialog(final String filename ,final boolean fromInternet, final byte[] data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);

        builder.setMessage("开始写入文件"+filename+"?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始执行写入
                dialog.dismiss();

                if(fromInternet){

                    beginWrite(filename);

                }else{

             beginOAD(data);


                }


            }
        });
         builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
             }
         });

        builder.create().show();

    }

    private void beginWrite(String filename) {


        String filePath=getOADFileSavedDir(DrawActivity.this)+"/"+filename;


        Log.i(TAG,"filePath="+filePath);

        File file = new File(filePath);

        long fileSize = file.length();
            Log.i(TAG, "file size..." + fileSize);   //SuZhou_LW0515_NoBoost_1228A2.bin
            if (fileSize==0){

                Log.i(TAG, "文件大小是0");

                Toast.makeText(DrawActivity.this, "文件大小是0", Toast.LENGTH_SHORT).show();
            }



        try {
            FileInputStream fi = new FileInputStream(file);

            byte[] buffer = new byte[(int) fileSize];

            int offset = 0;

            int numRead = 0;

            while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

                offset += numRead;

            }

            //确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "+ file.getName());
            }

            //生成头文件

            short hdLen = (short) ((buffer.length + 16 - 1) / 16);//16

            int checkSum = 0;

            for (int i = 0; i < buffer.length; i++)

            {
                checkSum += (buffer[i] & 0xff);

            }

            Log.i(TAG,"address="+address+"version_uid="+version_uid + "version_hex="+version_hex);

            byte[] headerBytes = ByteUtils.getOADHeader(version_uid,checkSum,address,version_hex,hdLen);

            StringBuffer sb = new StringBuffer();

            sb.append("revice Read Data[");
            for (byte b : headerBytes) {
                sb.append(Integer.toHexString((b & 0xFF)) +  " " );
            }
            sb.append("]");

            Log.i(TAG,sb.toString());

            byte[] finalBytes = new byte[buffer.length + headerBytes.length];

            System.arraycopy(headerBytes, 0, finalBytes, 0, headerBytes.length);


            System.arraycopy(buffer, 0, finalBytes, headerBytes.length, buffer.length);

            FileOutputStream fos = new FileOutputStream(filePath);

            fos.write(finalBytes);
            fos.close();
            fi.close();


            beginOAD(finalBytes);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3&&data!=null){
          //从本地列表返回
            String filePath=getOADFileSavedDir(DrawActivity.this)+"/"+data.getStringExtra("name");

            banben.setText(data.getStringExtra("name"));


            Log.i(TAG,"本地文件的路径"+filePath);

            File file = new File(filePath);

            long fileSize = file.length();
            Log.i(TAG, "file size..." + fileSize);
            if (fileSize > Integer.MAX_VALUE)
                Log.i(TAG, "file too big...");

            try {

                FileInputStream fi = new FileInputStream(file);

                byte[] buffer = new byte[(int) fileSize];

                int offset = 0;

                int numRead = 0;

                while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

                    offset += numRead;

                }

                //确保所有数据均被读取
                if (offset != buffer.length) {
                    throw new IOException("Could not completely read file "+ file.getName());
                }

                fi.close();

                showDialog(data.getStringExtra("name"),false,buffer);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }




        }

        if(requestCode==2&&data!=null){
            //获得数据
            String mac=data.getStringExtra("mac");

            provider.setCurrentDeviceMac(mac);

            if(provider.isConnectedAndDiscovered()){
                provider.disConnect();
            }

            Log.i(TAG,"开始连接");
            provider.connect_mac(mac);


            String title = "连接中...";
            String message = "正在连接Mac地址为："+mac+"的设备！";
            dialog_connect  = ProgressDialog.show(DrawActivity.this, title, message);
            dialog_connect.show();



        }

             if(requestCode==1&&data!=null){

           String url=data.getStringExtra("url");

            String filename=data.getStringExtra("filename");

            banben.setText(filename);

            int i=data.getIntExtra("position",1);

            String str = array.get(i) + "";

            JSONObject object = JSON.parseObject(str);

                version_uid=object.get("uid")+"";

                address=object.get("address")+"";

                version_hex=object.get("version_code")+"";


            Log.i(TAG, url);

            Log.i(TAG,"address="+address+"version_uid="+version_uid + "version_hex="+version_hex);
            //开始下载文件

            downLoad(url,filename);
        }


    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case 0x333:

                progressDialog.incrementProgressBy(msg.arg1-persent);

                persent=msg.arg1;

                postDelayed( runnable , 2000 );

                break;
            }
        };
    };



    private ProgressDialog onCreateDialog() {

             //this表示该对话框是针对当前Activity的
        ProgressDialog progressDialog = new ProgressDialog(DrawActivity.this);
                //设置最大值为100
                progressDialog.setMax(100);
                //设置进度条风格STYLE_HORIZONTAL
                progressDialog.setProgressStyle(
                        ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("正在写入");

        progressDialog.setCancelable(false);

        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;

    }

    private LepaoProtocalImpl mLepaoProtocalImpl=new LepaoProtocalImpl();

    Runnable runnable = new Runnable() {
        @SuppressWarnings("static-access")
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            msg.arg1 = mLepaoProtocalImpl.getOAD_percent();
            Log.i(TAG,"progress="+msg.arg1+"");
            handler.sendMessage(msg);
        };
    };

        private void beginOAD(byte[] data) {

        if (provider.isConnectedAndDiscovered())
        {
            Log.i(TAG, "buffer size..."+data.length);

            databytes = data;

            provider.OADDeviceHeadback(DrawActivity.this, databytes); // 第一次传文件头过去   之后的流程继续在蓝牙callback中

            progressDialog=onCreateDialog();

            progressDialog.incrementProgressBy(-progressDialog.getProgress());

            progressDialog.setMax(((databytes.length-17)/16));

            progressDialog.show();

            handler.post(runnable);


        }
        else
        {
            Toast.makeText(DrawActivity.this,"没有链接蓝牙哦",Toast.LENGTH_LONG);
        }

    }

    private void downLoad(String url, String filename) {


        DownloadRequest downloadRequest= NoHttp.createDownloadRequest(url,getOADFileSavedDir(DrawActivity.this),filename,true,false);

        downloadQueue.add(0,downloadRequest,downloadListener);
    }

    //获取文件下载的路径
    public static String getOADFileSavedDir(Context context)

    {
        String dir = null;
        File sysExternalStorageDirectory = Environment.getExternalStorageDirectory();
        if(sysExternalStorageDirectory != null && sysExternalStorageDirectory.exists())
        {
            dir = sysExternalStorageDirectory.getAbsolutePath()+"/.rtring/OAD";
        }

        Log.i(TAG,"路径"+dir);

        return dir;
    }


    private void refreshview() {
        if (provider.isConnectedAndDiscovered()&&tishi!=null) {
            tishi.setVisibility(View.GONE);

            choose.setText(provider.getCurrentDeviceMac());

        } else {

            tishi.setVisibility(View.VISIBLE);

            choose.setText("未选择蓝牙");

        }
    }



    private void initView() {
       /* private TextView tishi,choose,banben;
        private Button net_btn,home_btn; */

        tishi= (TextView) findViewById(R.id.tishi);//蓝牙没有连接的时候提示
        choose= (TextView) findViewById(R.id.choose);//当前连的是哪个蓝牙设备的提示
        banben= (TextView) findViewById(R.id.banben);//你选择了哪个版本
        net_btn= (Button) findViewById(R.id.net);
        home_btn= (Button) findViewById(R.id.home);
        link= (Button) findViewById(R.id.link);

        if(!provider.isConnectedAndDiscovered()){
          //蓝牙没有连接的时候
         tishi.setVisibility(View.VISIBLE);

        }else{

            tishi.setVisibility(View.GONE);
        }

   }
    private void initListener() {

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DrawActivity.this, BLEListActivity.class);
                startActivityForResult(intent,2);
            }
        });



       net_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provider.isConnectedAndDiscovered()) {
                    // 获取网络上的所有版本的名字
                    upload();
                } else
                    Log.i(TAG, "蓝牙未连接！");
            }
        });

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到本地版本的文件
                Intent intent=new Intent(DrawActivity.this,LocalActivity.class);
                startActivityForResult(intent,3);
            }
        });

    }


    //从网络下载固件数据
    private void upload(){
        String url="http://linkloving.com:5080/rtring_s_new-watch/MyControllerJSON";

        final Request<String> request=NoHttp.createStringRequest(url, RequestMethod.POST);
        //请求参数

        JSONObject obj = new JSONObject();
        obj.put("processorId",1018);
        obj.put("jobDispatchId", 6);
        obj.put("actionId", 1);

        obj.put("newData", "{\"device_type\":1}");

        request.setRequestBody(JSON.toJSONString(obj));

        requestQueue.add(1, request, new OnResponseListener<String>() {

            @Override
            public void onStart(int what) {

                Log.i(TAG, "开始获取版本");

                String title = "正在获取";

                String message = "正在获取网络版本!";

                dialog_connect  = ProgressDialog.show(DrawActivity.this, title, message);

                dialog_connect.show();


            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                dialog_connect.dismiss();

                Log.i(TAG, response.toString());

                JSONObject jsonObject = JSONObject.parseObject(response.get());

                Log.i(TAG, jsonObject.getString("returnValue"));

                array = JSON.parseArray(jsonObject.getString("returnValue") + "");

                String[] name = new String[array.size()];

                for (int i = 0; i < array.size(); i++) {

                    String str = array.get(i) + "";

                    JSONObject object = JSON.parseObject(str);

                    name[i] = object.get("file_name") + "";

                    Log.i(TAG, object.get("file_name") + "");

                    //将name数组穿过去
                }

                Intent intent = new Intent(DrawActivity.this, InternetActivity.class);

                intent.putExtra("name", name);

                startActivityForResult(intent, 1);

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                Log.i(TAG, "失败");

                dialog_connect.dismiss();

                Toast.makeText(DrawActivity.this, "获取资源失败！", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish(int what) {

                dialog_connect.dismiss();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

               if (provider.getBleProviderObserver() == null) {
            provider.setBleProviderObserver(bleProviderObserver);
        }

        refreshview();

    }





    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return DrawActivity.this;
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            super.updateFor_handleConnectSuccessMsg();
            Log.i(TAG, "连接成功！");
            refreshview();
            Toast.makeText(DrawActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            dialog_connect.dismiss();

        }



        @Override
          public void updateFor_handleConnectLostMsg() {
            // TODO Auto-generated method stub
            super.updateFor_handleConnectLostMsg();
            Log.i(TAG, "蓝牙断开连接！");
            refreshview();
            Toast.makeText(DrawActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            dialog_connect.dismiss();
        }



        /************************OAD头发送成功*************************/
        @Override
        public void updateFor_notifyCanOAD_D()
        {


        }


        @Override
        public void updateFor_notifyOADheadback(byte[] data) {


            if(data==null){
                if(progressDialog.isShowing())

                    progressDialog.dismiss();

                handler.removeCallbacks(runnable);
                showDialog("OAD头失败","没有返回数据");
                return;
            }

            if(data[0]==0x10){

                Log.i(TAG, "OAD头发送成功");

                if(provider.isConnectedAndDiscovered()){

                    provider.OADDeviceback(DrawActivity.this, databytes);

                }

            }else {
                if(progressDialog.isShowing())

                    progressDialog.dismiss();

                handler.removeCallbacks(runnable);
                showDialog("OAD头失败",ByteUtils.printData(data));
            }

        }
        @Override
        public void updateFor_notifyOADback(byte[] data) {

            if(progressDialog.isShowing())

                progressDialog.dismiss();

            handler.removeCallbacks(runnable);


            if(data==null){

                showDialog("OAD","没有任何数据");

            }

            if(data[0]==0xff){

            }

            else {

                showDialog("OAD",ByteUtils.printData(data));
            }

        }


        /************************OAD头发送成功*************************/

        /************************OAD失败*****************************/
     /*  @Override
        public void updateFor_notifyNOTCanOAD_D() {
            Log.e(TAG, "OAD 失败");

			Toast.makeText(DrawActivity.this, "OAD 升级失败！", Toast.LENGTH_LONG).show();

           if(progressDialog.isShowing())

            progressDialog.dismiss();

            handler.removeCallbacks(runnable);

           }*/

        /************************OAD失败****************************/
        /************************OAD成功****************************/
        /* @SuppressWarnings("static-access")

        @Override
        public void updateFor_notifyOADSuccess_D()
        {

            Log.e(TAG, "OAD升级成功!");

            if(progressDialog.isShowing())

                progressDialog.dismiss();

            handler.removeCallbacks(runnable);
        }*/
        /************************OAD成功****************************/



    }




}
