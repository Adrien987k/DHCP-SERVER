package com.dhcp.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dhcp.message.options.EmptyOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.util.BufferUtils;

public class DhcpOptionCollection {
	
	private Map<Short, DhcpOption> options = new HashMap<>();
	
	private DhcpOptionCollection(){
		
	}
	
	public byte[] getOptionsBytes() throws InvalidDhcpMessageException {
		if(!isValidOptionCollection()) 
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid collection of options");
		
		ByteBuffer buffer = ByteBuffer.allocate(totalLength());
		
		for(DhcpOption option : options.values()) {
			buffer.put(option.getContent());
		}
		if(options.get(options.size() - 1).getCode() != (short) 255){
			buffer.put(new EmptyOption().getContent());
		}
		
		return buffer.array();
	}
	
	public static DhcpOptionCollection parseDhcpOptions(ByteBuffer buffer) throws InvalidDhcpMessageException {
		DhcpOptionCollection options = new DhcpOptionCollection();
		
		short code = 0;
		do {
			code = BufferUtils.byteToShort(buffer.get());
			
			DhcpOption option = DhcpOptionFactory.buildDhcpOption(code);
			option.parseDhcpOption(buffer);
			
			options.addOption(option);
		} while(code != (short) 255 && buffer.hasRemaining());
			
		if(!options.isValidOptionCollection()) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with incoherent collection of options");
		
		return options;
	}
	
	public boolean addOption(DhcpOption option){
		if(!option.contentIsValid()) return false;
		options.put(option.getCode(), option);
		return true;
	}
	
	public int totalLength(){
		int result = 0;
		for(DhcpOption option : options.values()) result += option.getTotalLength();
		return result;
	}
	
	public int getDhcpMessageType(){
		MessageTypeOption msgTypeOpt = (MessageTypeOption) options.get((short) 53);
		return msgTypeOpt.getType();
	}
	
	private boolean isValidOptionCollection(){
		boolean containsDhcpMessageType = false;
		boolean allOptionAppearsOneTime = true;
		boolean allOptionAreValid = true;
		
		List<Short> optionsCodes = new ArrayList<>();
		
		if(options.containsKey(new Short((short) 53))) containsDhcpMessageType = true;

		for(DhcpOption option : options.values()){
			if(!option.contentIsValid()) allOptionAreValid = false;
		}
		
		for(Short sh : options.keySet()){			
			if(optionsCodes.contains(sh)) allOptionAppearsOneTime = false;
			else optionsCodes.add(sh);
		}
		
		return containsDhcpMessageType && allOptionAppearsOneTime 
			   && allOptionAreValid;
	}
	
}
