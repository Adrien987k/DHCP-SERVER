package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.common.AddressOptionBase;

public class DNSOption extends AddressOptionBase {
		
	public DNSOption() throws UnknownHostException {
		super((short) 6 );
		
		name = "Domain Name Server Option";
	}
	
}
