package com.dhcp.message.options;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dhcp.message.Option;
import com.dhcp.message.common.AddressOptionBase;

/**
 * 
 * @author Adrien
 *
 */
public class IpRequestedOption extends AddressOptionBase {

	public IpRequestedOption() throws UnknownHostException {
		super(Option.IP_REQUESTED, true);
		
		name = "IP resquested Option";
	}
	
	public IpRequestedOption(InetAddress address) throws UnknownHostException {
		this();
		addAddress(address);
	}

}
