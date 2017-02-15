package com.dhcp.message.options;

import com.dhcp.message.common.TimesOptionBase;

public class RebindingTimeOption extends TimesOptionBase {

	public RebindingTimeOption(){
		super((short) 59, true);
		
		name = "Rebinding Time Option";
	}
	
}
