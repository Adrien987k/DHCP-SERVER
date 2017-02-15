package com.dhcp.message.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.util.BufferUtils;

public abstract class EncodableOptionBase<E extends Encodable> extends DhcpOption {
	
	protected List<E> encodables = new ArrayList<>();
	private E instance;
	
	public EncodableOptionBase(short code, E instance){
		super(code);
		this.instance = instance;
	}
	
	@Override
	public byte[] getContent() throws InvalidDhcpMessageException {
		if(!contentIsValid())
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid : " + name);
		
		ByteBuffer buffer = ByteBuffer.allocate(2 +
				encodables.size() * instance.getLength());
		;
		buffer.put((byte) code);
		buffer.put((byte) length);
		
		for(E e : encodables){
			buffer.put(e.getBytes());
		}
		
		return buffer.array();
	}
	
	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		int elementLength = instance.getLength();
		
		
		if(length % elementLength != 0)
			DhcpMessage.invalidDhcpMessage("dhcp message received with invalid length option : " + name);
		
		int numberElement = length / elementLength;
		byte[] elementBuffer = new byte[elementLength];
		for(int i = 0; i < numberElement; i++){
			buffer.get(elementBuffer);
			encodables.add(instance.parseEncodable(elementBuffer));
		}
	}
	
	public void addEncodable(E encodable){
		encodables.add(encodable);
		length += instance.getLength();
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid() && (length % instance.getLength() == 0);
	}
	
}
