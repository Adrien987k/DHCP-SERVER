package com.dhcp.message.options;

import com.dhcp.message.AddressOptionBase;

public class RouterOption extends AddressOptionBase {
	
	public RouterOption(){
		super((short) 3);
		
		name = "Router Option";
	}
	
}
