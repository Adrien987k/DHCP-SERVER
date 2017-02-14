package com.dhcp.message.options;

import com.dhcp.message.OneAddressOptionBase;

public class SubnetMaskOption extends OneAddressOptionBase {
	
	public SubnetMaskOption(){
		super((short) 1);
		
		name = "Subnet Mask Option";
	}
	
}
