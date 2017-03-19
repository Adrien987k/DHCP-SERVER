package com.dhcp.util;

/**
 * The TransactionID class represents an ID received from a client.
 * This ID will be saved and re-use to identify the current transaction with the client.
 * @author Arnaud
 *
 */
public class TransactionID {

	/**
	 * The id of the transaction.
	 */
	private long xid;
	
	/**
	 * Create a transaction ID with the specified ID.
	 * @param xid The ID of the transaction.
	 */
	public TransactionID(long xid) {
		this.xid = xid;
	}
	
	/**
	 * @return The ID of the transaction.
	 */
	public long getXid() { return xid; }
}
