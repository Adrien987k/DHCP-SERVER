package com.dhcp.util;

public class BufferUtils {
	
	public static String resize(String src, int newLength){
		StringBuilder sb = new StringBuilder();
		sb.append(src);
		sb.setLength(newLength);
		return sb.toString();
	}
	
	public static short byteToShort(byte b){
		return (short) Byte.toUnsignedInt(b);
	}
	
	public static int shortToInt(short b){
		return Short.toUnsignedInt(b);
	}
	
	public static long intToLong(int b){
		return Integer.toUnsignedLong(b);
	}
	
}
