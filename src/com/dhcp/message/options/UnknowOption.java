package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.common.DhcpOption;

public class UnknowOption extends DhcpOption {
	
	public UnknowOption(short code){
		super(code);
		
		name = "Unknow Option";
	}

	@Override
	public byte[] getContent() {
		byte[] result = new byte[]{ (byte) code };
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
