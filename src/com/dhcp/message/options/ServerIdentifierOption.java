package com.dhcp.message.options;

import com.dhcp.message.OneAddressOptionBase;

public class ServerIdentifierOption extends OneAddressOptionBase {
	
	public ServerIdentifierOption(){
		super((short) 54);
		
		name = "Server Identifier option";
	}
	
}
