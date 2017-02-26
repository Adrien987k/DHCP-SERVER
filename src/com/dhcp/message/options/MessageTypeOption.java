package com.dhcp.message.options;

import java.nio.ByteBuffer;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.Option;
import com.dhcp.message.common.ShortsOptionBase;

public class MessageTypeOption extends ShortsOptionBase {
	
	public MessageTypeOption() {
		super(Option.MESSAGE_TYPE, true);
		
		name = "DHCP Message Type";
	}
	
	public MessageTypeOption(int type){
		this();
		addShort((short) type);
	}
	
	public int getType(){
		return getElements().get(0);
	}
	
	public void setType(int type){
		getElements().set(0, (short) type);
	}
	
	@Override 
	public boolean contentIsValid(){
		short type = getElements().get(0);
		return super.contentIsValid() && type > 0 && type < 9;
	}
	
	@Override
	public String toString(){
		short type = getElements().get(0);
		StringBuilder sb = new StringBuilder();
		sb.append("CODE : " + code);
		sb.append(" NAME : " + name);
		sb.append(" LENGTH : " + length + "\n");
		sb.append("TYPE : " + DhcpMessage.findStringOfType(type) + "\n");
		return sb.toString();
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		super.parseDhcpOption(buffer);
		short type = getElements().get(0);
		if(type < (short) 1 || type > (short) 8) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with unknow type");
	}
	
}
