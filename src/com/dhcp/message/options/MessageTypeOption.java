package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.common.DhcpOption;
import com.dhcp.util.BufferUtils;

public class MessageTypeOption extends DhcpOption {

	private short type = 0;
	
	public MessageTypeOption() {
		super((short) 53);
		
		length = 1;
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
	
	public void setType(int type){
		this.type = (short) type;
	}
	
	@Override 
	public boolean contentIsValid(){
		return super.contentIsValid() && type > 0 && type < 9;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("TYPE : " + type + "\n");
		return sb.toString();
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
