package com.dhcp.message;

/**
 * 
 * @author Adrien
 *
 */
public class InvalidDhcpMessageException extends Exception {
	
	private static final long serialVersionUID = 6456314443810995633L;

	public InvalidDhcpMessageException(String message){
		super(message);
	}

}
