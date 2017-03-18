package com.dhcp.message.options;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dhcp.message.common.AddressOptionBase;

/**
 * 
 * @author Adrien
 *
 */
public class ServerIdentifierOption extends AddressOptionBase {
	
	public ServerIdentifierOption() throws UnknownHostException {
		super((short) 54, true);
		
		name = "Server Identifier option";
	}
	
	public ServerIdentifierOption(InetAddress address) throws UnknownHostException {
		this();
		addAddress(address);
		
	}
	
}
