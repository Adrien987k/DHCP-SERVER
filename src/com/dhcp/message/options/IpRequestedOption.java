package com.dhcp.message.options;

import com.dhcp.message.AddressOptionBase;

public class IpRequestedOption extends AddressOptionBase {

	public IpRequestedOption() {
		super((short) 50);
		
		name = "IP resquested Option";
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid()
			   && length == 4
			   && addresses.size() == 1;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("ADDRESS : " + 
		(addresses.isEmpty() ? "" : addresses.get(0).toString()));
		sb.append("\n");
		return sb.toString();
	}

}
