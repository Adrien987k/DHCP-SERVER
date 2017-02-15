package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.AddressOptionBase;

public class ServerIdentifierOption extends AddressOptionBase {
	
	public ServerIdentifierOption() throws UnknownHostException {
		super((short) 54, true);
		
		name = "Server Identifier option";
	}
	
}
