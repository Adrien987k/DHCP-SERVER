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
public class DNSOption extends AddressOptionBase {
		
	public DNSOption() throws UnknownHostException {
		super(Option.DNS , false);
		
		name = "Domain Name Server Option";
	}
	
	public DNSOption(InetAddress address) throws UnknownHostException {
		this();
		addAddress(address);
	}
	
}
