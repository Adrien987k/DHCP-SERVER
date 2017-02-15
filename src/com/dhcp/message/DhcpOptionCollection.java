package com.dhcp.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dhcp.message.common.DhcpOption;
import com.dhcp.message.options.EndOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.util.BufferUtils;

public class DhcpOptionCollection {
	
	private Map<Short, DhcpOption> options = new HashMap<>();
	
	public DhcpOptionCollection(){
		
	}
	
	public byte[] getOptionsBytes() throws InvalidDhcpMessageException {
		if(!isValidOptionCollection()) 
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid collection of options");
		
		ByteBuffer buffer = ByteBuffer.allocate(
				(options.containsKey((short) 255)) ? totalLength() : totalLength() + 1);
		
		for(DhcpOption option : options.values()) {
			buffer.put(option.getContent());
		}
		if(!options.containsKey((short) 255)){
			buffer.put(new EndOption().getContent());
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
		if(options.containsKey((short) 53)){
			MessageTypeOption msgTypeOpt = (MessageTypeOption) options.get((short) 53);
			return msgTypeOpt.getType();			
		} else {
			return 0;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(DhcpOption option : options.values()) sb.append(option.toString());
		return sb.toString();
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
