package com.dhcp.message;

import java.net.UnknownHostException;

import com.dhcp.message.common.DhcpOption;
import com.dhcp.message.options.DNSOption;
import com.dhcp.message.options.EmptyOption;
import com.dhcp.message.options.EndOption;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.IpRequestedOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.RebindingTimeOption;
import com.dhcp.message.options.RenewalTimeOption;
import com.dhcp.message.options.RouterOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.message.options.SubnetMaskOption;
import com.dhcp.message.options.UnknowOption;

public class DhcpOptionFactory {
	
	public static DhcpOption buildDhcpOption(short code) {
		try{
			switch(code){
			case 0: return new EmptyOption(); 
			case 1: return new SubnetMaskOption();
			case 3: return new RouterOption();
			/*case 2:
			case 4:
			case 5: */
			case 6: return new DNSOption();
			case 50: return new IpRequestedOption();
			case 51: return new IPLeaseTimeOption();
			case 53: return new MessageTypeOption();
			case 54: return new ServerIdentifierOption();
			case 58: return new RenewalTimeOption();
			case 59: return new RebindingTimeOption();
			case 255: return new EndOption();
			default: return new UnknowOption(code);
		}
		} catch(UnknownHostException e){
			
		}
		return null;
	}
	
}
