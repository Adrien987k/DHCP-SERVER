package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.DhcpOption;
import com.dhcp.message.InvalidDhcpMessageException;

public class EmptyOption extends DhcpOption {

	public EmptyOption() {
		super((short) 0);
		
		name = "Pad Option";
	}

	@Override
	public byte[] getContent() {
		byte[] result = { (byte) 0}; 
		return result;
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		
	}
	
}
