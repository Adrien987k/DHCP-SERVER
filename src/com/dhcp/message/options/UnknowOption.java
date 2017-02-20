package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.common.DhcpOption;
import com.dhcp.util.BufferUtils;

public class UnknowOption extends DhcpOption {
	
	public UnknowOption(short code){
		super(code);
		
		name = "Unknow Option";
	}

	@Override
	public byte[] getContent() {
		byte[] result = new byte[]{ (byte) code , (byte) length};
		return result;
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		
		for(int i = 0; i < length; i++){
			buffer.get();
		}
		
	}
	
}
