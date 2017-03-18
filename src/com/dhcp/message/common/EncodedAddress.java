package com.dhcp.message.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * An object that represent an Encoded Address
 * 
 * @author Adrien
 *
 */
public class EncodedAddress implements Encodable {
		
	public InetAddress address;
	
	public EncodedAddress(InetAddress address){
		this.address = address;
	}

	/**
	 * @return the bytes of the address
	 */
	@Override
	public byte[] getBytes() {
		return address.getAddress();
	}
	
	@Override
	public int getLength() {
		return 4;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EncodedAddress parseEncodable(byte[] buffer) {
		InetAddress address = null; 
		try {
			address = InetAddress.getByAddress(buffer);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return new EncodedAddress(address);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public InetAddress getElement(){
		return address;
	}
	
	@Override
	public String toString(){
		return "ADDRESS : " + address.toString() + "\n";
	}
	
}

