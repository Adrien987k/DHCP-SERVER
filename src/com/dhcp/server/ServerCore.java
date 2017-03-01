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
	private ServerLogger logger = null;
	
	private ExecutorService pool = null;
	private LeaseManager leaseManager = null;
	private boolean stop = false;
	
	@Deprecated
	public ServerCore() throws IOException {
		logger = new ServerLogger();
		logger.systemMessage("Server started");
		pool = Executors.newFixedThreadPool(100);
		start();
	}
	
	public ServerCore(String config) throws IOException {
		logger = new ServerLogger();
		logger.systemMessage("Initialization");
		serverConfig = new ServerConfig(config,logger);
		logger.systemMessage("Server " + serverConfig.getServerName() + " || " + serverConfig.getServerAddress() + " started");
		pool = Executors.newFixedThreadPool(serverConfig.getAddressAvailable());
		leaseManager = new LeaseManager(this,logger);
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
					// TODO Bloque sur l'appel et donc ne traite qu'un message à la fois 
					//pool.execute(new HandlerDHCPMessage(packet,logger,this));
					new Thread(new HandlerDHCPMessage(packet,logger,this)).start();
				} catch (IOException e) {
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		}
		
	}

	public ServerConfig getConfig() {
		return serverConfig;
	}
	
	public LeaseManager getLeaseManager() {
		return leaseManager;
	}
	
	
}
