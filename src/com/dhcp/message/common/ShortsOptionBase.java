package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The common abstract class for all option that contains a list of short
 * 
 * @author Adrien
 *
 */
public class ShortsOptionBase extends EncodableOptionBase<EncodedShort> {
	
	public ShortsOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedShort((short) 0), onlyOneElement);
	}
	
	/**
	 * @param sh The short to add
	 */
	public void addShort(short sh){
		addEncodable(new EncodedShort(sh));
	}
	
	/**
	 * @return The list of all bytes contained in the option
	 */
	@Override
	public List<Short> getElements(){
		List<Short> result = new ArrayList<>();
		for(EncodedShort esh : getEncodables()){
			result.add(esh.getElement());
		}
		return result;
	}
	
}
