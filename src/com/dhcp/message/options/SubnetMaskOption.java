package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.Option;
import com.dhcp.message.common.AddressOptionBase;

public class SubnetMaskOption extends AddressOptionBase {
	
	public SubnetMaskOption() throws UnknownHostException {
		super(Option.SUBNET_MASK, true);
		
		name = "Subnet Mask Option";
	}
	
}
