package com.dhcp.message.options;

import com.dhcp.message.AddressOptionBase;

public class DNSOption extends AddressOptionBase {
		
	public DNSOption(){
		super((short) 6 );
		
		name = "Domain Name Server Option";
	}
	
}
