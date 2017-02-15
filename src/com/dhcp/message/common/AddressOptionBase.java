package com.dhcp.message.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.util.BufferUtils;

public abstract class AddressOptionBase extends EncodableOptionBase<EncodedAddress> {
	
	public AddressOptionBase(short code) throws UnknownHostException {
		super(code, new EncodedAddress(InetAddress.getByName("0.0.0.0")));
	}
	
}
