package com.dhcp.message.common;

/**
 * An object that represent an encoded byte
 * 
 * @author Adrien
 *
 */
public class EncodedByte implements Encodable {
	
	public byte b;
	
	public EncodedByte(byte b){
		this.b = b;
	}
	
	@Override
	public byte[] getBytes() {
		return new byte[] { b };
	}
	
	@Override
	public int getLength(){
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EncodedByte parseEncodable(byte[] buffer) {
		return new EncodedByte(buffer[0]);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Byte getElement(){
		return b;
	}
	
	@Override
	public String toString(){
		return b + " ";
	}
	
}
