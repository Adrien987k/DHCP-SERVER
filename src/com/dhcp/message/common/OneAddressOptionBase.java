package com.dhcp.message.common;

import java.net.UnknownHostException;

public class OneAddressOptionBase extends AddressOptionBase {
	
	public OneAddressOptionBase(short code) throws UnknownHostException {
		super(code);
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid()
			   && length == 4
			   && encodables.size() == 1;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("ADDRESS : " + 
		(encodables.isEmpty() ? "" : encodables.get(0).toString()));
		sb.append("\n");
		return sb.toString();
	}
	
}
