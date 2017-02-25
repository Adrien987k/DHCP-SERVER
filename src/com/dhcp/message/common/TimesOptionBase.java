package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

public class TimesOptionBase extends EncodableOptionBase<EncodedTime> {
	
	public TimesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedTime(0), onlyOneElement);
	}
	
	public void addTime(long day, long hour, long minute, long second) {
		addEncodable(new EncodedTime(day, hour, minute, second));
	}
	
	public void addTime(long seconds) {
		addEncodable(new EncodedTime(seconds));
	}
	
	@Override
	public List<EncodedTime> getElements(){
		List<EncodedTime> result = new ArrayList<>();
		for(EncodedTime time : getEncodables()){
			result.add(time.getElement());
		}
		return result;
	}
}
