package com.dhcp.message.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AddressOptionBase extends EncodableOptionBase<EncodedAddress> {
	
	public AddressOptionBase(short code, boolean onlyOneElement) throws UnknownHostException {
		super(code, new EncodedAddress(InetAddress.getByName("0.0.0.0")), onlyOneElement);
	}
	
	public void addAddress(InetAddress address){
		addEncodable(new EncodedAddress(address));
	}
	
}
