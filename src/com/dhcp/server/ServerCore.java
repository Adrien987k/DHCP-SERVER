package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dhcp.lease.LeaseManager;
import com.dhcp.util.ServerLogger;


public class ServerCore extends Thread {

	private ServerConfig serverConfig;
	
	@SuppressWarnings("unused")
	private ExecutorService pool = null;
	private LeaseManager leaseManager = null;
	private boolean stop = false;
	
	public ServerCore(String config) throws IOException {
		ServerLogger.systemMessage("Initialization");
		serverConfig = new ServerConfig(config);
		ServerLogger.systemMessage("Server " + serverConfig.getServerName() + " || " + serverConfig.getServerAddress() + " started");
		pool = Executors.newFixedThreadPool(serverConfig.getAddressAvailable());
		leaseManager = new LeaseManager(this);
	}
	
	public void startServer(){
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
					new Thread(new HandlerDHCPMessage(packet, this)).start();
				} catch (IOException e) {
					ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, Server might not be connect to the network");
				}
			}
		} catch (SocketException se) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Socket Exception");
		}
		
	}

	public ServerConfig getConfig() {
		return serverConfig;
	}
	
	public LeaseManager getLeaseManager() {
		return leaseManager;
	}
	
	
}
