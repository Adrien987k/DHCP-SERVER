package com.dhcp.message.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * The common abstract class for all option that contains a list of addresses
 * 
 * @author Adrien
 *
 */
public abstract class AddressOptionBase extends EncodableOptionBase<EncodedAddress> {
	
	public AddressOptionBase(short code, boolean onlyOneElement) throws UnknownHostException {
		super(code, new EncodedAddress(InetAddress.getByName("0.0.0.0")), onlyOneElement);
	}
	
	/**
	 * @param address The address to add
	 */
	public void addAddress(InetAddress address){
		addEncodable(new EncodedAddress(address));
	}
	
	/**
	 * @return The list of all addresses contained in the option
	 */
	@Override
	public List<InetAddress> getElements(){
		List<InetAddress> result = new ArrayList<>();
		for(EncodedAddress address : getEncodables()){
			result.add(address.getElement());
		}
		return result;
	}
	
}
