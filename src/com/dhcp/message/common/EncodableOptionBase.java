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
	private int encodableLength;
	
	private boolean onlyOneElement = false;
	
	public EncodableOptionBase(short code, E instance, boolean onlyOneElement){
		super(code);
		this.instance = instance;
		this.encodableLength = instance.getLength();
		this.onlyOneElement = onlyOneElement;
	}
	
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
		
		if(length % encodableLength != 0)
			DhcpMessage.invalidDhcpMessage("dhcp message received with invalid length option : " + name);
		
		int numberEncodable = length / encodableLength;
		byte[] elementBuffer = new byte[encodableLength];
		for(int i = 0; i < numberEncodable; i++){
			buffer.get(elementBuffer);
			encodables.add(instance.parseEncodable(elementBuffer));
		}
	}
	
	public void addEncodable(E encodable){
		if(onlyOneElement && encodables.size() >= 1) return;
		encodables.add(encodable);
		length += instance.getLength();
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid() && (length % encodableLength == 0)
				&& (onlyOneElement ? length == encodableLength : true);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		for(E e : encodables){
			sb.append(e);
		}
		return sb.toString();
	}
	
}
