package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The common abstract class for all option that contains a list of bytes
 * 
 * @author Adrien
 *
 */
public class BytesOptionBase extends EncodableOptionBase<EncodedByte> {
	
	public BytesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedByte((byte) 0), onlyOneElement);
	}
	
	/**
	 * @param b The byte to add
	 */
	public void addByte(byte b){
		addEncodable(new EncodedByte(b));
	}
	
	/**
	 * @return The list of all bytes contained in the option
	 */
	@Override
	public List<Byte> getElements(){
		List<Byte> result = new ArrayList<>();
		for(EncodedByte eb : getEncodables()){
			result.add(eb.getElement());
		}
		return result;
	}
}
