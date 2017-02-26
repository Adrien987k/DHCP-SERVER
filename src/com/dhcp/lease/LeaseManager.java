package com.dhcp.lease;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.TreeMap;

import com.dhcp.server.ServerCore;
import com.dhcp.util.HardwareAddress;
import com.dhcp.util.ServerLogger;

public class LeaseManager {

	private final HashMap<InetAddress, Lease> leases = new HashMap<>();
	//TODO Sauvegarder les nouveaux baux dans le fichier de config
	//TODO déterminer si c'est static ou dynamique
	
	
	@SuppressWarnings("unused")
	private ServerLogger logger;
	
	private ServerCore server;
	
	
	public LeaseManager(ServerCore server, ServerLogger logger) {
		this.server = server;
		this.logger = logger;
		
		leases.putAll(getServer().getConfig().getStaticLeases());
	}
	
	
	public synchronized InetAddress getRandIPAddress(HardwareAddress hardwareAddress) {
			InetAddress ipAddress = null;
			
			if(getLeases().size() < getServer().getConfig().getAddressAvailable()) {
				if( (ipAddress = findAvailableIpAddress()) != null ) {
					Lease lease = new Lease(ipAddress,hardwareAddress,getServer().getConfig().getLeaseDuration());
					getLeases().put(lease.getIPAddress(), lease);
					return lease.getIPAddress();
				}
			}
			
			return null;
		}
	
	public synchronized InetAddress tryAddressElseRand(InetAddress ipAddress, HardwareAddress hardwareAddress) {
		
		if(getLeases().get(ipAddress) != null) {
			if(getLeases().get(ipAddress).isAvailable()) {
					getLeases().get(ipAddress).bind(hardwareAddress);
					return ipAddress;
			} else {
				return getRandIPAddress(hardwareAddress);
			}
		} else {
			getLeases().put(ipAddress, new Lease(ipAddress,hardwareAddress,getServer().getConfig().getLeaseDuration())); 
			return ipAddress;
		}
		
	}
	
	public synchronized void release(InetAddress address) {
		getLeases().get(address).release(false);
	}
	
	private ServerCore getServer() { return server; }
	private HashMap<InetAddress,Lease> getLeases() { return leases; }
	
	private synchronized InetAddress findAvailableIpAddress() {
		InetAddress ipAddress = getServer().getConfig().getIpAddressBandStart();
				
		for(int i = 0; i < getServer().getConfig().getAddressAvailable() ; i++) {
			ipAddress.getAddress()[3] += i;
			if(!getLeases().containsKey(ipAddress)) {
				return ipAddress;
			}
		}
		
		return null;
	}
}
