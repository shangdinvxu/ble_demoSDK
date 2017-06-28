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

import java.io.File;

public class LocalActivity extends AppCompatActivity {

    private static final String TAG = LocalActivity.class.getSimpleName();
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        mListView= (ListView) findViewById(R.id.listView_local);

        //获取本地文件

        String str=getOADFileSavedDir(LocalActivity.this);

        if(str!=null){

            File file=new File(getOADFileSavedDir(LocalActivity.this));



            File [] array=file.listFiles();

            final String [] name=new String[array.length];

            for (int i=0;i<array.length;i++){

                name[i]= array[i].getName();

            }


            mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name));

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //将文件名返回
                    Intent intent=new Intent();

                    intent.putExtra("name",name[position]);

                    setResult(RESULT_OK,intent);

                    finish();


                }
            });

        }



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

        Log.i(TAG, "路径" + dir);

        return dir;
    }
}
