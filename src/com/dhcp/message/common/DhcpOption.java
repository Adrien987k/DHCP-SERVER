package com.dhcp.message.common;

import java.nio.ByteBuffer;

import com.dhcp.message.InvalidDhcpMessageException;

/**
 * Represent one Dhcp Option
 * 
 * @author Adrien
 *
 */
public abstract class DhcpOption {
		
	protected short code = 0;
	protected short length = 0;
	
	protected String name = "";
	
	public DhcpOption(short code){
		this.code = code;
	}
	
	/**
	 * @return A byte array containing all the encoded data of this option
	 * @throws InvalidDhcpMessageException
	 */
	public abstract byte[] getBytes() throws InvalidDhcpMessageException;
	
	/**
	 * Get the data of a buffer and fill this option with it
	 * 
	 * @param buffer The encoded data to collect
	 * @throws InvalidDhcpMessageException
	 */
	public abstract void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException;
	
	/**
	 * @return True if this option is valid
	 */
	public boolean contentIsValid(){
		boolean isValid = true;
		if(code < 0 || code > 255) isValid = false;
		return isValid;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("CODE : " + code);
		sb.append(" NAME : " + name);
		sb.append(" LENGTH : " + length + "\n");
		return sb.toString();
	}
	
	/*GETTERS AND SETTERS*/
	
	public short getContentLength(){
		return length;
	}
	
	public int getTotalLength(){
		return length + 2;
	}
	
	public short getCode(){
		return code;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	/*END OF GETTERS AND SETTERS*/
	
}
