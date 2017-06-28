package com.ble.main.testview;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ble.main.R;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;

import java.io.File;

public class InternetActivity extends AppCompatActivity {

    private ListView listView;
    String [] name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
        Intent mIntent=getIntent();
         name=mIntent.getStringArrayExtra("name");



        listView= (ListView) findViewById(R.id.listView_internet);

        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //去网络下载数据

                String url = "http://linkloving.com:5080/rtring_s_new-watch/BLEFirmwareDownloadController?action=adu&name="+name[position];

                //返回主界面

                Intent intent=new Intent();

                intent.putExtra("url",url);

                intent.putExtra("filename",name[position]);

                intent.putExtra("position",position);

                setResult(RESULT_OK,intent);

                finish();

            }
        });


    }








}
