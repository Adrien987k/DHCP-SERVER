package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.OneAddressOptionBase;

public class ServerIdentifierOption extends OneAddressOptionBase {
	
	public ServerIdentifierOption() throws UnknownHostException {
		super((short) 54);
		
		name = "Server Identifier option";
	}
	
}
