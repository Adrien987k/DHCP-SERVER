package com.dhcp.util;

public class BufferUtils {
	
	public static String resize(String src, int newLength){
		StringBuilder sb = new StringBuilder();
		sb.append(src);
		sb.setLength(newLength);
		return sb.toString();
	}
	
	public static short byteToShort(byte b){
		return (short) (Byte.toUnsignedInt(b));
	}
	
	public static int shortToInt(byte b){
		return (Byte.toUnsignedInt(b));
	}
	
	public static long intToLong(byte b){
		return (Byte.toUnsignedLong(b));
	}
	
	
}
