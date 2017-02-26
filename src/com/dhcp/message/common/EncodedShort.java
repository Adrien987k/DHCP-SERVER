package com.dhcp.message.common;

import com.dhcp.util.BufferUtils;

public class EncodedShort implements Encodable {
	
	public short sh;
	
	public EncodedShort(short sh){
		this.sh = sh;
	}
	
	
	@Override
	public byte[] getBytes(){
		return new byte[] { (byte) sh };
	}
	
	@Override 
	public int getLength(){
		return 1;
	}
	
	@Override
	public EncodedShort parseEncodable(byte[] buffer){
		return new EncodedShort(BufferUtils.byteToShort(buffer[0]));
	}
	
	@Override
	public Short getElement(){
		return sh;
	}
	
	@Override
	public String toString(){
		return sh + " ";
	}
	
}
