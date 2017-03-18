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

/**
 * Represent a coherent option collection
 * 
 * @author Adrien
 *
 */
public class DhcpOptionCollection {
	
	/**
	 * Contain all the options of this collection, the key is the code of the option
	 */
	private Map<Short, DhcpOption> options = new HashMap<>();
	
	public DhcpOptionCollection(){
		
	}
	
	/**
	 * @return a byte array corresponding of the encoded date of this option collection
	 * @throws InvalidDhcpMessageException
	 */
	public byte[] getOptionsBytes() throws InvalidDhcpMessageException {
		if(!isValidOptionCollection()) 
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid collection of options");
		
		ByteBuffer buffer = ByteBuffer.allocate(
				options.containsKey(Option.END) ? totalLength() : totalLength() + 1);
		
		for(DhcpOption option : options.values()) {
			buffer.put(option.getBytes());
		}
		if(!options.containsKey(Option.END)){
			buffer.put(new EndOption().getBytes());
		}
		
		return buffer.array();
	}
	
	/**
	 * @param buffer The ByteBuffer containing all the options data
	 * @return A new DhcpCollection containing all the date of the buffer
	 * @throws InvalidDhcpMessageException
	 */
	public static DhcpOptionCollection parseDhcpOptions(ByteBuffer buffer) throws InvalidDhcpMessageException {
		DhcpOptionCollection options = new DhcpOptionCollection();
		
		short code = 0;
		do {
			code = BufferUtils.byteToShort(buffer.get());
			
			DhcpOption option = DhcpOptionFactory.buildDhcpOption(code);

			option.parseDhcpOption(buffer);
			options.addOption(option);
			
		} while(code != Option.END && buffer.hasRemaining());
			
		if(!options.isValidOptionCollection()) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with incoherent collection of options");
		
		return options;
	}
	
	/**
	 * Add an option to this collection
	 * 
	 * @param option The option to add
	 * @return True if the option have been added, false otherwise
	 */
	public boolean addOption(DhcpOption option){
		if(option == null){
			return false;
		}
		if(!option.contentIsValid()) return false;
		options.put(option.getCode(), option);
		return true;
	}
	
	/**
	 * @return The number of bytes of this collection
	 */
	public int totalLength(){
		int result = 0;
		for(DhcpOption option : options.values()) result += option.getTotalLength();
		return result;
	}
	
	/**
	 * @return The type of the message type option (53) of this collection, 0 if this option is not present 
	 */
	public int getDhcpMessageType(){
		if(options.containsKey(Option.MESSAGE_TYPE)){
			MessageTypeOption msgTypeOpt = (MessageTypeOption) options.get(Option.MESSAGE_TYPE);
			return msgTypeOpt.getType();			
		} else {
			return 0;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(DhcpOption option : options.values()) sb.append(option.toString());
		return sb.toString();
	}
	
	/**
	 * @return True if this collection is valid, false otherwise
	 */
	public boolean isValidOptionCollection(){
		boolean containsDhcpMessageType = false;
		boolean allOptionAppearsOneTime = true;
		boolean allOptionAreValid = true;
		
		List<Short> optionsCodes = new ArrayList<>();
		
		if(options.containsKey(Option.MESSAGE_TYPE)) containsDhcpMessageType = true;

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
	
	/**
	 * 
	 * @param code The code of the option to get
	 * @return The DhcpOption corresponding to the code, null if this option is not present
	 */
	public DhcpOption getByCode(short code) {
		return options.get(code);
	}
	
}
