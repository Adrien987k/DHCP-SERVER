package com.dhcp.lease;

import java.net.InetAddress;
import java.util.TreeMap;

import com.dhcp.server.ServerCore;
import com.dhcp.util.HardwareAddress;
import com.dhcp.util.ServerLogger;

public class LeaseManager {

	private final TreeMap<InetAddress, Lease> leases = new TreeMap<>();
	
	@SuppressWarnings("unused")
	private ServerLogger logger;
	
	private ServerCore server;
	
	
	public LeaseManager(ServerCore server, ServerLogger logger) {
		this.server = server;
		this.logger = logger;
	}
	public synchronized InetAddress getRandIPAddress(HardwareAddress hardwareAddress) {
			//TODO non terminé
			
			//Lease lease = new Lease(/* addresse ip*/,hardwareAddress,/* duration */);
			//leases.put(lease.getIPAddress(), lease);
			//return lease.getIPAddress();
			
			return null;
		}
	
	public synchronized InetAddress tryOldIPAddressElseRand(InetAddress ipAddress, HardwareAddress hardwareAddress) {
		if(leases.get(ipAddress).isAvailable()) {
			leases.get(ipAddress).bind(hardwareAddress);
			return ipAddress;
		} else {
			return getRandIPAddress(hardwareAddress);
		}
	}
	
	public synchronized void release(InetAddress address) {
		leases.get(address).release(false);
	}
}
