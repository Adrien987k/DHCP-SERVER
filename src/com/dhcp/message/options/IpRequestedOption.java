package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.OneAddressOptionBase;

public class IpRequestedOption extends OneAddressOptionBase {

	public IpRequestedOption() throws UnknownHostException {
		super((short) 50);
		
		name = "IP resquested Option";
	}

}
