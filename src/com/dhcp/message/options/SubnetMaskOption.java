package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.OneAddressOptionBase;

public class SubnetMaskOption extends OneAddressOptionBase {
	
	public SubnetMaskOption() throws UnknownHostException {
		super((short) 1);
		
		name = "Subnet Mask Option";
	}
	
}
