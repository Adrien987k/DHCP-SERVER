package com.dhcp.message;

import com.dhcp.message.options.EmptyOption;
import com.dhcp.message.options.EndOption;
import com.dhcp.message.options.MessageTypeOption;

public class DhcpOptionFactory {
	
	public static DhcpOption buildDhcpOption(short code) throws InvalidDhcpMessageException {
		switch(code){
			case 0: return new EmptyOption(); 
			/*case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:*/
			case 53: return new MessageTypeOption();
			case 255: return new EndOption();
			default: DhcpMessage.invalidDhcpMessage("message received with unknow code option"); 
		}
		return null;
	}
	
}
