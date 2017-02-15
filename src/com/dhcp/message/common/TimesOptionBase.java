package com.dhcp.message.common;

public class TimesOptionBase extends EncodableOptionBase<EncodedTime> {
	
	public TimesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedTime(0), onlyOneElement);
	}
	
}
