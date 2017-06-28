package com.example.android.bluetoothlegatt.wapper;

import android.util.Log;

import com.example.android.bluetoothlegatt.proltrol.LepaoCommand;

public class CmdFinder 
{
	private static final String TAG = CmdFinder.class.getSimpleName();
	
	private byte[] cmd = null;
	
	private int cmdLen;
	
	private static CmdFinder instence;
	
	public interface OnCmdFindedListener
	{
		public void onCmdFinded(byte[] cmd);
	}
	
	private static OnCmdFindedListener onCmdFindedListener;
	
	private CmdFinder()
	{
		
	}
	
	public synchronized static CmdFinder getInstence(OnCmdFindedListener listener)
	{
		onCmdFindedListener = listener;
		if(instence == null)
		{
			instence = new CmdFinder();
		}
		return instence;
	}
    
	public void appendData(byte[] data)
	{
		if(cmd == null)
		{
//			OwnLog.i(TAG, ".............cmd null...............");
			if(data[0] == LepaoCommand.COMMAND_HEAD)
			{
				cmdLen = data[1]&0xFF;

				if(data[3]== (byte) 0x95) {
					if((data[2]&(byte)0x01)==(byte)0x01) {
						cmdLen += 256;
					}
				}

				if(data[3]== (byte) 0x91) {
					if((data[2]&(byte)0x01)==(byte)0x01) {
						cmdLen += 256;
					}
				}
//				OwnLog.i(TAG, ".............cmd start...............");
				cmd = data;
			}
			else 
			{
//			  OwnLog.e(TAG, ".............not cmd...............");
			   return;	
			}
		}
		else
		{
		     cmd = byteMerger(cmd, data);
		}
//		OwnLog.d(TAG, ".............cmd len..............."+cmdLen+".............real len............"+cmd.length);
		if(cmdLen <= cmd.length)
		{
			byte[] resultCmd = byteCut(cmd, cmdLen);
			if(onCmdFindedListener != null)
			{
				onCmdFindedListener.onCmdFinded(resultCmd);
			}
			Log.e("open", ".............cmd len..............."+cmdLen+".............real len............"+cmd.length);
			cmd = null ;
		}
	}
	
	public void clearData(boolean canclear){
		if(canclear){
			cmd = null ;
		}
	}
	
    //java 合并两个byte数组  
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }
    
    public static byte[] byteCut(byte[] src,int len)
    {
    	if(src.length <= len)
    	{
    		return src;
    	}
    	
    	byte[] temp = new byte[len];
    	System.arraycopy(src, 0, temp, 0, len);
    	return temp;
    }
	

}
