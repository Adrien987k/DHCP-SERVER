package com.dhcp.message;

public class OneAddressOptionBase extends AddressOptionBase {
	
	public OneAddressOptionBase(short code){
		super(code);
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
