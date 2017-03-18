package com.dhcp.message.common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.util.BufferUtils;

/**
 * The common abstract class for all OptionBase classes
 * That mean all the option witch contains a list of element
 * Contains a collection of encodable objects
 * 
 * @author Adrien
 *
 * @param <E>
 */
public abstract class EncodableOptionBase<E extends Encodable> extends DhcpOption {
	
	/**
	 * The collection of encodable objects
	 */
	protected List<E> encodables = new ArrayList<>();
	
	/**
	 * An instance of E used in order to use static methods of the object E
	 */
	private E instance;
	
	/**
	 * The length of one object of the collection
	 */
	private int encodableLength;
	
	/**
	 * Indicate if this option can store more than one element
	 */
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
	
	/**
	 * @return The encodable list
	 */
	public List<E> getEncodables(){
		return encodables;
	}
	
	/**
	 * @return A list of all the elements stored in the option (not encoded)
	 */
	public abstract List<? extends Object> getElements();
	
	/**
	 * @return A byte array containing all the data of this option
	 */
	@Override
	public byte[] getBytes() throws InvalidDhcpMessageException {
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
	
	/**
	 * Parse the ByteBuffer in parameter and fill this option with all the data in it
	 */
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
	
	/**
	 * Add an encoded object to this option
	 * 
	 * @param encodable The encodable to add
	 */
	public void addEncodable(E encodable){
		if(onlyOneElement && encodables.size() >= 1) return;
		encodables.add(encodable);
		length += instance.getLength();
	}
	
	/**
	 * @return True if this option is valid
	 */
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
