package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.AddressOptionBase;

public class RouterOption extends AddressOptionBase {
	
	public RouterOption() throws UnknownHostException {
		super((short) 3, false);
		
		name = "Router Option";
	}
	
}
