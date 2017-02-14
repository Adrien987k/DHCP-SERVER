package com.dhcp.message.options;

import com.dhcp.message.OneAddressOptionBase;

public class IpRequestedOption extends OneAddressOptionBase {

	public IpRequestedOption() {
		super((short) 50);
		
		name = "IP resquested Option";
	}

}
