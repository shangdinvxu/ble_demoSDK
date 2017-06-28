package com.ble.utils;

public class ByteUtils {

	private static final int SHORT_LENGTH = 2;
	private static final int INT_LENGTH = 4;

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		// 必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i & 0xFF000000);
		// ，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。虽//然
		// result[0] = (byte)(i & 0xFF000000); 这样不行，但是我们可以这样 result[0] =
		// (byte)((i & //0xFF000000) >>24);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * 将16位的short转换成byte数组
	 * 
	 * @param s
	 *            short
	 * @return byte[] 长度为2
	 */
	public static byte[] shortToByteArray(short s) {
		byte[] result = new byte[2];
		result[1] = (byte) ((s >> 8) & 0xFF);
		result[0] = (byte) (s & 0xFF);

		return result;
	}

	public static byte[] strToBytes(String hexString) {
		if (hexString.length() == 1) {
			hexString = hexString + "0";
		}
		int count = hexString.length() / 2;
		byte[] newByte = new byte[count];
		int j = 0;

		for (int i = 0; i < hexString.length();) {
			newByte[j] = (byte) (Integer.parseInt(hexString.substring(i, i + 2), 16) & 0xff);
			i += 2;
			j++;
		}
		return newByte;
	}

	public static byte[] getOADHeader(String uid, int checkSum, String address, String ver, short hdLen) {
		byte[] headerBytes = new byte[16];
		byte[] emptyBytes = new byte[2];

		byte[] uidBytes = ByteUtils.intToByteArray(Integer.valueOf(uid, 16));// ByteUtils.strToBytes(uid);
		byte[] addressBytes = ByteUtils.intToByteArray(Integer.valueOf(address, 16));
		byte[] verBytes = ByteUtils.intToByteArray(Integer.valueOf(ver, 16));
		byte[] chksumBytes = ByteUtils.intToByteArray(checkSum);
		byte[] hdLenBytes = ByteUtils.shortToByteArray(hdLen);
		int copyPos = 0;
		System.arraycopy(chksumBytes, 0, headerBytes, copyPos, chksumBytes.length);
		copyPos += INT_LENGTH;

		System.arraycopy(verBytes, 0, headerBytes, copyPos, verBytes.length);
		copyPos += SHORT_LENGTH;

		System.arraycopy(hdLenBytes, 0, headerBytes, copyPos, hdLenBytes.length);
		copyPos += SHORT_LENGTH;

		System.arraycopy(addressBytes, 0, headerBytes, copyPos, addressBytes.length);
		copyPos += SHORT_LENGTH;// short

		System.arraycopy(emptyBytes, 0, headerBytes, copyPos, emptyBytes.length);
		copyPos += SHORT_LENGTH;
		System.arraycopy(uidBytes, 0, headerBytes, copyPos, uidBytes.length);

		return headerBytes;
	}

	public static  String printData(byte[] data) {
		StringBuffer sb = new StringBuffer();
		sb.append("错误的数据:"  + "[");
		for (int i = 0; i < data.length; i++) {
			sb.append(Integer.toHexString((data[i] & 0xff)) + " ");
		}
		sb.append("]");
		return sb.toString();
	}

}
