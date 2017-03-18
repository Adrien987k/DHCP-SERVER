package com.dhcp.message.options;

import com.dhcp.message.Option;
import com.dhcp.message.common.TimesOptionBase;

/**
 * 
 * @author Adrien
 *
 */
public class RenewalTimeOption extends TimesOptionBase{
	
	public RenewalTimeOption(){
		super(Option.RENEWAL_TIME, true);
		
		name = "Renewal Time Option";
	}
	
	public RenewalTimeOption(long time){
		this();
		this.addTime(time);
	}
	
	public RenewalTimeOption(long days, long hours, long minutes, long seconds){
		this();
		this.addTime(days, hours, minutes, seconds);
	}
	
}
