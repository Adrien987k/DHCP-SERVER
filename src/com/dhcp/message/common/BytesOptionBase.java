package com.dhcp.message.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.util.BufferUtils;

public class BytesOptionBase extends DhcpOption {
	
	private List<Byte> bytes = new ArrayList<>();
	
	public BytesOptionBase(short code){
		super(code);
	}

	@Override
	public byte[] getContent() throws InvalidDhcpMessageException {
		if(!contentIsValid())
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid : " + name);
		
		ByteBuffer buffer = ByteBuffer.allocate(2 + bytes.size());
		buffer.put((byte) code);
		buffer.put((byte) length);
		
		for(byte b : bytes){
			buffer.put(b);
		}
		
		return buffer.array();
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		
		if(!bytes.isEmpty()) bytes.clear();
		
		for(int i = 0; i < length; i++){
			bytes.add(buffer.get());
		}
	}
	
	public void addByte(byte b){
		bytes.add(b);
		length++;
	}
	
}
