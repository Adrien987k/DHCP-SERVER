package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dhcp.lease.Lease;
import com.dhcp.util.ServerLogger;


public class ServerCore extends Thread {

	private boolean stop = false;
	private ServerLogger logger = null;
	private ExecutorService pool = null;
	
	private static TreeMap<InetAddress, Lease> leases = new TreeMap<>();
	
	public ServerCore() throws IOException {
		logger = new ServerLogger();
		logger.systemMessage("Server started");
		pool = Executors.newFixedThreadPool(100);
		start();
	}
	
	@Override
	public void run() {
		try(DatagramSocket ds67 = new DatagramSocket(67)) {
			ds67.setSoTimeout(1000);
			byte[] buffer = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while(!stop) {
				ds67.setBroadcast(true);
				try {
					ds67.receive(packet);
					pool.execute(new Thread(new HandlerDHCPMessage(packet,logger)));
				} catch (IOException e) {
					
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		}
	}

	public static synchronized InetAddress getRandIPAddress(InetAddress hardwareAddress) {
		//TODO non terminé
		
		//Lease lease = new Lease(/* addresse ip*/,hardwareAddress,/* duration */);
		//leases.put(lease.getIPAddress(), lease);
		//return lease.getIPAddress();
		
		return null;
	}
	
	public static synchronized InetAddress tryOldIPAddressElseRand(InetAddress ipAddress, InetAddress hardwareAddress) {
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
