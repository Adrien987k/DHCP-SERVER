package com.dhcp.util;

import java.nio.ByteBuffer;

/**
 * The HardwareAddress class represents an hardware address of a client.
 * @author MrkJudge
 *
 */
public class HardwareAddress {
	
	/**
	 * The hardware address.
	 */
	private byte[] address = new byte[16];
	
	/**
	 * Create a hardware address with the specfied address.
	 * @param address The byte array containing the address.
	 */
	public HardwareAddress(byte[] address){
		this.address = address;
	}
	
	/**
	 * Return a byte array with the hardware address
	 * @return
	 */
	public byte[] getBytes(){
		return address;
	}
	
	
	/**
	 * Create and return a new HardwareAddress object with the specfied address.
	 * @param buffer The byte buffer containing the hardware address.
	 * @return The hardware address from the byte buffer.
	 */
	public static HardwareAddress parseHardwareAddress(ByteBuffer buffer){
		byte[] address = new byte[16];
		buffer.get(address);
		HardwareAddress chaddr = new HardwareAddress(address);
		return  chaddr;
	}
	
	
	/**
	 * Create and return a new HardwareAddress object with the specfied address.
	 * @param buffer The string containing the hardware address.
	 * @return The hardware address from the string.
	 */
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
	
	
	/**
	 * Return a string of the hardware address.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for(byte b: address)
			sb.append(String.format("%02X ", b));
			
		return sb.toString();
	}
	
	
}
