package com.dhcp.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.util.BufferUtils;

public abstract class AddressOptionBase extends DhcpOption {
	
	protected List<InetAddress> addresses = new ArrayList<>();
	
	public AddressOptionBase(short code){
		super(code);
	}
	
	@Override
	public byte[] getContent() throws InvalidDhcpMessageException {
		if(!contentIsValid()) 
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid : " + name);
		
		ByteBuffer buffer = ByteBuffer.allocate(2 + (addresses.size() * 4));
		buffer.put((byte) code);
		buffer.put((byte) length);
		
		for(InetAddress address : addresses){
			buffer.put(address.getAddress());
		}
		
		return buffer.array();
	}
	
	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		if(length % 4 != 0) 
			DhcpMessage.invalidDhcpMessage("dhcp message received with address option of invalid length");
		
		int numberAddress = length / 4;
		byte[] addressBuffer = new byte[4];
		for(int i = 0; i < numberAddress; i++){
			buffer.get(addressBuffer);
			try {
				addresses.add(InetAddress.getByAddress(addressBuffer));
			} catch (UnknownHostException e) {
				DhcpMessage.invalidDhcpMessage("dhcp message received with invalid address in an address option");
			}
		}
	}
	
	public void addAdrress(InetAddress address){
		addresses.add(address);
		length += 4;
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid() && (length % 4 == 0); 
	}
	
}
