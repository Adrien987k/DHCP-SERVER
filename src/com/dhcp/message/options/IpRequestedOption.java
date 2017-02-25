package com.dhcp.message.options;

import java.net.UnknownHostException;

import com.dhcp.message.Option;
import com.dhcp.message.common.AddressOptionBase;

public class IpRequestedOption extends AddressOptionBase {

	public IpRequestedOption() throws UnknownHostException {
		super(Option.IP_REQUESTED, true);
		
		name = "IP resquested Option";
	}

}
