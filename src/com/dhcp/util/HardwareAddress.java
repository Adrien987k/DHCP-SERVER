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
		HardwareAddress chaddr = new HardwareAddress(address);
		return  chaddr;
	}
	
	public static HardwareAddress parseHardwareAddress(String buffer){
		
		byte[] address = new byte[16];
		
		ByteBuffer bb = ByteBuffer.wrap(buffer.getBytes());
		
		for(int i = address.length -1; i > 0 ; i -= 4) {
			address[i  ] = bb.get();
			address[i-1] = bb.get();
			
			address[i-2] = bb.get();
			address[i-3] = bb.get();
			
			// / ou :bb.getChar();
		}
		
		return new HardwareAddress(address);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for(byte b: address)
			sb.append(String.format("%02X ", b));
			
		return sb.toString();
	}
	
	
}
