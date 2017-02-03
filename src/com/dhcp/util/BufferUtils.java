package com.dhcp.util;

public class BufferUtils {
	
	public static String resize(String src, int newLength){
		StringBuilder sb = new StringBuilder();
		sb.append(src);
		sb.setLength(newLength);
		return sb.toString();
	}
	
}
