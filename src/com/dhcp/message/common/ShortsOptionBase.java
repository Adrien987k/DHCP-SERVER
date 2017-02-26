package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

public class ShortsOptionBase extends EncodableOptionBase<EncodedShort> {
	
	public ShortsOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedShort((short) 0), onlyOneElement);
	}
	
	public void addShort(short sh){
		addEncodable(new EncodedShort(sh));
	}
	
	@Override
	public List<Short> getElements(){
		List<Short> result = new ArrayList<>();
		for(EncodedShort esh : getEncodables()){
			result.add(esh.getElement());
		}
		return result;
	}
	
}
