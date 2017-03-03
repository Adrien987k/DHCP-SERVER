package com.dhcp.message.common;

import java.nio.ByteBuffer;

import com.dhcp.message.InvalidDhcpMessageException;

public abstract class DhcpOption {
		
	protected short code = 0;
	protected short length = 0;
	
	protected String name = "";
	
	//protected byte[] content = null;
	
	public DhcpOption(short code){
		this.code = code;
	}
	
	public abstract byte[] getBytes() throws InvalidDhcpMessageException;
	public abstract void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException;
	
	public boolean contentIsValid(){
		boolean isValid = true;
		if(code < 0 || code > 255) isValid = false;
		//if(content == null) isValid = false;
		//if((length != 0) && content.length != length) isValid = false;
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
	
}
