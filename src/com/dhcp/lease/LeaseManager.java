package com.dhcp.lease;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.dhcp.server.HandlerDHCPMessage;
import com.dhcp.server.ServerCore;
import com.dhcp.util.HardwareAddress;
import com.dhcp.util.ServerLogger;
import com.dhcp.util.TransactionID;

public class LeaseManager {

	private final HashMap<InetAddress, Lease> leases = new HashMap<>();
	
	private ServerCore server;
	
	
	public LeaseManager(ServerCore server) {
		this.server = server;
		
		leases.putAll(getServer().getConfig().getStaticLeases());
	}
	
	private ServerCore getServer() { return server; }
	private HashMap<InetAddress,Lease> getLeases() { return leases; }
	
	
	public synchronized InetAddress getRandIPAddress(HardwareAddress hardwareAddress) {
			InetAddress ipAddress = null;
			
			if(getLeases().size() < getServer().getConfig().getAddressAvailable()) {
				if( (ipAddress = findAvailableIpAddress()) != null ) {
					Lease lease = new Lease(ipAddress,hardwareAddress,getServer().getConfig().getLeaseDuration());
					getLeases().put(lease.getIPAddress(), lease);
					return lease.getIPAddress();
				}	
			}
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "No available IP Address for Hardware Address " + hardwareAddress);
			return null;
		}
	
	public synchronized InetAddress tryAddressElseRand(InetAddress ipAddress, HardwareAddress hardwareAddress) {
		
		if(getLeases().get(ipAddress) != null) {
			if(getLeases().get(ipAddress).isAvailable()) {
					getLeases().get(ipAddress).bind(hardwareAddress);
					ServerLogger.systemMessage("Lease " + getLeases().get(ipAddress) + " has been allocated with the IP Address '" + ipAddress + "' for the Hardware address '" + hardwareAddress + "'");
					return ipAddress;
			} else if(getLeases().get(ipAddress).getHardwareAddress().toString().equals(hardwareAddress.toString())) { 
				ServerLogger.systemMessage("Lease " + getLeases().get(ipAddress) + " has been extended with the IP Address '" + ipAddress + "' for the Hardware address '" + hardwareAddress + "'");
				return ipAddress;
			} else {
				ServerLogger.systemMessage("The IP address '" + ipAddress+"' for Hardware Address '" + hardwareAddress + "' is not availbale");
				return getRandIPAddress(hardwareAddress);
			}
		} else {
			getLeases().put(ipAddress, new Lease(ipAddress,hardwareAddress,getServer().getConfig().getLeaseDuration()));
			ServerLogger.systemMessage("Lease " + getLeases().get(ipAddress) + " has been allocated with the IP Address '" + ipAddress + "' for the Hardware address '" + hardwareAddress + "'");
			return ipAddress;
		}
		
	}
	
	public synchronized void release(InetAddress address) {
		getLeases().get(address).release(false);
	}
	
	private synchronized InetAddress findAvailableIpAddress() {
		InetAddress ipAddress = getServer().getConfig().getIpAddressBandStart();
		
		ByteBuffer addressByteBuffer = ByteBuffer.wrap(ipAddress.getAddress());
		byte[] addressArray = addressByteBuffer.array();
		

		try {
			for(int i = 0; i < getServer().getConfig().getAddressAvailable() ; i++) {
			
				addressArray[3]  += 1;
				ipAddress = InetAddress.getByAddress(addressArray);
				
					System.out.println("IP ADDRESS -> "+ ipAddress);
				
				
				if(!getLeases().containsKey(ipAddress)
						&& !HandlerDHCPMessage.addressPreSelected.containsValue(ipAddress)) {
					ServerLogger.systemMessage("IP Address selected: " + ipAddress);
					return ipAddress;
				}
			}
		} catch (UnknownHostException e) {
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "UnknowHostException when trying to find available ip address");
		}
		ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Cannot find available address");
		return null;
	}
	
	public synchronized InetAddress findAvailableIpAddress(TransactionID xid) {
		InetAddress ipAddress = findAvailableIpAddress();
		if(ipAddress != null)
			HandlerDHCPMessage.addressPreSelected.put(xid, ipAddress);
		return ipAddress;
	}
}
