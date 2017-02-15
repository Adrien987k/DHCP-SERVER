package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.common.DhcpOption;

public class EndOption extends DhcpOption {

	public EndOption() {
		super((short) 255);
		
		name = "End Option";
	}

	@Override
	public byte[] getContent() {
		byte[] result = new byte[]{ (byte) 255 };
		return result;
	}
	
	@Override
	public int getTotalLength(){
		return 1;
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		
	}

}
