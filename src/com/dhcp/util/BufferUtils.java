package com.dhcp.util;

/**
 * 
 * @author Adrien
 *
 */
public class BufferUtils {
	
	/**
	 * Resize a String to a new length
	 * 
	 * @param src The string to resize
	 * @param newLength The new length
	 * @return The new string resized
	 */
	public static String resize(String src, int newLength){
		StringBuilder sb = new StringBuilder();
		sb.append(src);
		sb.setLength(newLength);
		return sb.toString();
	}
	
	/**
	 * Cast a byte that encoded a short into a short
	 * Used when you have to represent an unsigned byte by a short 
	 */
	public static short byteToShort(byte b){
		return (short) Byte.toUnsignedInt(b);
	}
	
	/**
	 * Cast a short that encoded a int into a int
	 * Used when you have to represent an unsigned short by a int
	 */
	public static int shortToInt(short b){
		return Short.toUnsignedInt(b);
	}
	
	/**
	 * Cast a int that encoded a long into a long
	 * Used when you have to represent an unsigned int by a long
	 */
	public static long intToLong(int b){
		return Integer.toUnsignedLong(b);
	}
	
}
