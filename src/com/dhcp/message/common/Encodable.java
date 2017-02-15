package com.dhcp.message.common;

public interface Encodable {
	
	public byte[] getBytes();
	
	public int getLength();
	
	public <E extends Encodable> E parseEncodable(byte[] buffer); 
	
}
