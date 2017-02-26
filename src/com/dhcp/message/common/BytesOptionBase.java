package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

public class BytesOptionBase extends EncodableOptionBase<EncodedByte> {
	
	public BytesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedByte((byte) 0), onlyOneElement);
	}
	
	public void addByte(byte b){
		addEncodable(new EncodedByte(b));
	}
	
	@Override
	public List<Byte> getElements(){
		List<Byte> result = new ArrayList<>();
		for(EncodedByte eb : getEncodables()){
			result.add(eb.getElement());
		}
		return result;
	}
}
