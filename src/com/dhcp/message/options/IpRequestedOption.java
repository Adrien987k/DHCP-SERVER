package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.AddressOptionBase;

public class IpRequestedOption extends AddressOptionBase {

	public IpRequestedOption() throws UnknownHostException {
		super((short) 50, true);
		
		name = "IP resquested Option";
	}

}
