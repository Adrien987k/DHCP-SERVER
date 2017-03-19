package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dhcp.lease.LeaseManager;
import com.dhcp.util.ServerLogger;


/**
 * The ServerCore class is the core of the server.
 * The server will try to bind to the port 67 and then handle and respond to any
 * valid DISCOVER, REQUEST or RELEASE message received.
 * @author Arnaud
 *
 */
public class ServerCore extends Thread {

	/**
	 * The server config loaded from the config file.
	 */
	private ServerConfig serverConfig;
	
	/**
	 * The pool of thread which will handle messages.
	 * Note that for the moment this pool is not used because of technical issues.
	 * Any message received will start a new thread until the problem is fixed.
	 */
	@SuppressWarnings("unused")
	private ExecutorService pool = null;
	
	/**
	 * The lease manager which create, store and delete lease attributed to clients.
	 */
	private LeaseManager leaseManager = null;
	
	/**
	 * Indicates if the server must stop.
	 */
	private boolean stop = false;
	
	/**
	 * Create a new server with a lease manager, loading configuration from the config file.
	 * @param config The path of the config file.
	 */
	public ServerCore(String config) throws IOException {
		ServerLogger.systemMessage("Initialization");
		serverConfig = new ServerConfig(config);
		ServerLogger.systemMessage("Server " + serverConfig.getServerName() + " || " + serverConfig.getServerAddress() + " started");
		pool = Executors.newFixedThreadPool(serverConfig.getAddressAvailable());
		leaseManager = new LeaseManager(this);
	}
	
	/**
	 * Start the server.
	 */
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

	/**
	 * @return The configuration of the server.
	 */
	public ServerConfig getConfig() {
		return serverConfig;
	}
	
	/**
	 * @return The lease manager of the server.
	 */
	public LeaseManager getLeaseManager() {
		return leaseManager;
	}
	
	
}
