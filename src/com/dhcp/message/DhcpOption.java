package com.dhcp.message;

import java.nio.ByteBuffer;

public abstract class DhcpOption {
		
	protected short code = 0;
	protected short length = 0;
	
	protected String name = "";
	
	protected byte[] content = null;
	
	public DhcpOption(short code){
		this.code = code;
	}
	
	public abstract byte[] getContent();
	public abstract void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException;
	
	
	public boolean contentIsValid(){
		boolean isValid = true;
		if(code < 0 || code > 255) isValid = false;
		if(content == null) isValid = false;
		if(content.length != length) isValid = false;
		return isValid;
	}
	
	public short getLength(){
		return length;
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
	
}
