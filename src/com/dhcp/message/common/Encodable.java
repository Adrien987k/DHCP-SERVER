package com.dhcp.message.common;

/**
 * The common interface to all the encodable objects into an option
 * 
 * @author Adrien
 *
 */
public interface Encodable {
	
	/**
	 * @return The byte array containing the data of the object
	 */
	public byte[] getBytes();
	
	/**
	 * @return The number of bytes of this object
	 */
	public int getLength();
	
	/**
	 * @param buffer A byte array containing the data of an encoded object
	 * @return The encoded object contained in the buffer
	 */
	public <E extends Encodable> E parseEncodable(byte[] buffer);
	
	/**
	 * @return The element witch is encoded in the Encoded object
	 */
	public <F extends Object> F getElement();
	
}
