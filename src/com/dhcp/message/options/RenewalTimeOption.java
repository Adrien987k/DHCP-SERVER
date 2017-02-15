package com.dhcp.message.options;

import com.dhcp.message.common.TimesOptionBase;

public class RenewalTimeOption extends TimesOptionBase{
	
	public RenewalTimeOption(){
		super((short) 58, true);
		
		name = "Renewal Time Option";
	}
	
}
