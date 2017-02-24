package com.dhcp.message.options;

import com.dhcp.message.Option;
import com.dhcp.message.common.TimesOptionBase;

public class IPLeaseTimeOption extends TimesOptionBase {
	
	public IPLeaseTimeOption(){
		super(Option.IP_LEASE_TIME, true);
		
		name = "IP Lease Time Option";
	}
	
	public IPLeaseTimeOption(long time){
		this();
		this.addTime(time);
	}
	
	public IPLeaseTimeOption(long days, long hours, long minutes, long seconds){
		this();
		this.addTime(days, hours, minutes, seconds);
	}
	
}
