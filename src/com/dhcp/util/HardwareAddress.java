package com.dhcp.util;

import java.nio.ByteBuffer;

public class HardwareAddress {
	
	private byte[] address = new byte[16];
	
	public HardwareAddress(byte[] address){
		this.address = address;
	}
	
	public byte[] getBytes(){
		return address;
	}
	
	public static HardwareAddress parseHardwareAddress(ByteBuffer buffer){
		byte[] address = new byte[16];
		buffer.get(address);
		return new HardwareAddress(address);
	}
	
	public static HardwareAddress parseHardwareAddress(String buffer){
		
		//TODO
		byte[] address = new byte[16];
		
		return new HardwareAddress(address);
	}
	
	
}
