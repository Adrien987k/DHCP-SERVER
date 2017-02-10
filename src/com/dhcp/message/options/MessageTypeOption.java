package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.DhcpOption;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.util.BufferUtils;

public class MessageTypeOption extends DhcpOption {

	private short type = 0;
	
	public MessageTypeOption() {
		super((short) 53);
		
		name = "DHCP Message Type";
	}

	@Override
	public byte[] getContent() {
		byte[] result = new byte[]{ (byte) 53, (byte) 1, (byte) type };
		return result;
	}
	
	public int getType(){
		return type;
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		if(length != 1) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with option type length different of 1");
		type = BufferUtils.byteToShort(buffer.get());
		if(length < (short) 1 || length > (short) 8) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with unknow type");
	}
	
}
