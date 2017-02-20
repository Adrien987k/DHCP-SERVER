package com.dhcp.lease;

import java.net.InetAddress;
import java.util.TreeMap;

import com.dhcp.util.HardwareAddress;

public class LeaseManager {

	private static TreeMap<InetAddress, Lease> leases = new TreeMap<>();
	
	public static synchronized InetAddress getRandIPAddress(HardwareAddress hardwareAddress) {
			//TODO non terminé
			
			//Lease lease = new Lease(/* addresse ip*/,hardwareAddress,/* duration */);
			//leases.put(lease.getIPAddress(), lease);
			//return lease.getIPAddress();
			
			return null;
		}
	
	public static synchronized InetAddress tryOldIPAddressElseRand(InetAddress ipAddress, HardwareAddress hardwareAddress) {
		if(leases.get(ipAddress).isAvailable()) {
			leases.get(ipAddress).bind(hardwareAddress);
			return ipAddress;
		} else {
			return getRandIPAddress(hardwareAddress);
		}
	}
	
	public static synchronized void release(InetAddress address) {
		leases.get(address).release(false);
	}
}
