package com.dhcp.util;

public class TransactionID {

	private long xid;
	
	public TransactionID(long xid) {
		this.xid = xid;
	}
	
	public long getXid() { return xid; }
}
