package com.example.android.bluetoothlegatt.exception;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BLException extends Exception
{
	private  int  errCode;
	private  Map< String, String >  msg;

	public  BLException( int e ) {
		errCode = e;
		msg = new HashMap< String, String >();
	}
	
	public  void  addMsg( String key, String val ) {
		msg.put( key, val );
	}
	
	public  int  getErrCode() {
		return errCode;
	}
	
	public String getMessage(String key) {
		return msg.get(key);
	}
	
	@Override
	public String getMessage() {
		StringBuilder  sb = new StringBuilder();
		sb.append( BLErrCode.getMessage( errCode ) ).append( '\n' );
		Set< String >  keys = msg.keySet();
		for( Iterator< String > it = keys.iterator(); it.hasNext(); ) {
			String  key = it.next();
			sb.append( key ).append( ':' ).append( msg.get(key) ).append( '\n' );
		}
		return sb.toString();
	}

}
