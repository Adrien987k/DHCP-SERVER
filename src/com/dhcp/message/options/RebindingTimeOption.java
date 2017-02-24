package com.dhcp.message.options;

import com.dhcp.message.Option;
import com.dhcp.message.common.TimesOptionBase;

public class RebindingTimeOption extends TimesOptionBase {

	public RebindingTimeOption(){
		super(Option.REBINFING_TIME, true);
		
		name = "Rebinding Time Option";
	}
	
	public RebindingTimeOption(long time){
		this();
		this.addTime(time);
	}
	
	public RebindingTimeOption(long days, long hours, long minutes, long seconds){
		this();
		this.addTime(days, hours, minutes, seconds);
	}
	
}
