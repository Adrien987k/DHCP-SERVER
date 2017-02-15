package com.dhcp.message.options;

import com.dhcp.message.common.TimesOptionBase;

public class IPLeaseTimeOption extends TimesOptionBase {
	
	public IPLeaseTimeOption(){
		super((short) 51, true);
		
		name = "IP Lease Time Option";
	}
	
}
