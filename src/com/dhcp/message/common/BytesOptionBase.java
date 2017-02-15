package com.dhcp.message.common;

public class BytesOptionBase extends EncodableOptionBase<EncodedByte> {
	
	public BytesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedByte((byte) 0), onlyOneElement);
	}
	
	public void addByte(byte b){
		addEncodable(new EncodedByte(b));
	}
	
}
