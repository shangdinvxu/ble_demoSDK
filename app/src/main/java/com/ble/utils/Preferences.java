package com.ble.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;


public class Preferences {
	
	public final static String SHARED_PREFERENCES_SHAREDPREFERENCES = "__hex__";
	
	public static SharedPreferences getAppDefaultSharedPreferences(Context context, boolean canWrite) throws Exception 
	{
		if(context == null)
		{
			throw new Exception("context 为空");
		}
		return context.getSharedPreferences(
				SHARED_PREFERENCES_SHAREDPREFERENCES, canWrite?Context.MODE_WORLD_WRITEABLE:Context.MODE_WORLD_READABLE);
	}
	
	/**
	 * �洢������Ϣ.
	 * @param activity
	 * @return
	 */
	public static void addHexInfo(Context activity, String data)
	{
		ArrayList<String> list = new ArrayList<String>();
		String[] kkk;
		if(!getHexInfo(activity)[0].equals("")){
			kkk=getHexInfo(activity);
			list.add(data+"|");
			System.out.println(list.toString());
			for(int i =0;i<kkk.length;i++){
				list.add(kkk[i]+"|");
			}
			SharedPreferences nameSetting;
			try {
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				String[] value_data =new String[list.size()] ;
				for(int i =0;i<list.size();i++){
					 value_data[i] = list.get(i);
				}
				String str="";
				for(int i=0;i<value_data.length;i++){	
			                 str+=(String)value_data[i];
				}	
				Log.i("Preferences", "Str:"+str);
				namePref.putString(SHARED_PREFERENCES_SHAREDPREFERENCES,str);
				
				namePref.commit();
			} catch (Exception e) {
				Log.i("Preferences", "addHexInfo�����ȡPreferencesȨ��ʧ�ܣ�");
			}
		}else{
			SharedPreferences nameSetting;
			try {
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				namePref.putString(SHARED_PREFERENCES_SHAREDPREFERENCES, data+"|");
				namePref.commit();
			} catch (Exception e) {
				Log.i("Preferences", "addHexInfo�����ȡPreferencesȨ��ʧ�ܣ�");
			}
		}
	}
	
	/**
	 * ��ȡ�洢��Ϣ.
	 * 
	 * @param activity
	 * @return
	 */
	public static String[]  getHexInfo(Context activity)
	{
		
		String[] data_value = null;
			SharedPreferences nameSetting;
			try {
				nameSetting = getAppDefaultSharedPreferences(activity, false);
				String str_savedata = nameSetting.getString(SHARED_PREFERENCES_SHAREDPREFERENCES, "");
				data_value=str_savedata.split("\\|");
				String mk="";
				for(String m:data_value){
					mk += m;
				}
				System.out.println("mk:"+mk);
			} catch (Exception e) {
				Log.i("Preferences", "getHexInfo�����ȡPreferencesȨ��ʧ�ܣ�");
			}
			return data_value;
	}
	
	/**
	 * remove
	 * @param activity
	 */
	public static void removeHexInfo(Context activity)
	{
			SharedPreferences nameSetting;
			try 
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				namePref.putString(SHARED_PREFERENCES_SHAREDPREFERENCES, "");
				namePref.commit();
			} catch (Exception e) {
				Log.i("Preferences", "removeHexInfo�����ȡPreferencesȨ��ʧ�ܣ�");
			}
		
		}
	}
	
	
