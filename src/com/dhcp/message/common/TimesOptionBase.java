package com.dhcp.message.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The common abstract class for all option that contains a list of encodedTime
 * 
 * @author Adrien
 *
 */
public class TimesOptionBase extends EncodableOptionBase<EncodedTime> {
	
	public TimesOptionBase(short code, boolean onlyOneElement){
		super(code, new EncodedTime(0), onlyOneElement);
	}
	
	/**
	 * Add a time in days, hours, minutes and seconds to this option
	 */
	public void addTime(long day, long hour, long minute, long second) {
		addEncodable(new EncodedTime(day, hour, minute, second));
	}
	
	/**
	 * Add a time in seconds to this option
	 */
	public void addTime(long seconds) {
		addEncodable(new EncodedTime(seconds));
	}
	
	/**
	 * @return The list of all encodedTime contained in this option
	 */
	@Override
	public List<EncodedTime> getElements(){
		List<EncodedTime> result = new ArrayList<>();
		for(EncodedTime time : getEncodables()){
			result.add(time.getElement());
		}
		return result;
	}
}
