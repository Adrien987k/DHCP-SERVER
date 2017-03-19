package com.dhcp.server;

import java.io.IOException;

import com.dhcp.util.ServerLogger;

/**
 * The DhcpServer class is a re-implementation of a real DHCP server.
 * This class will start the server with the config file located at \'data/config\'.
 * @author Arnaud
 *
 */
public class DhcpServer {
	
	public static void main(String[] args) {
		try {
			ServerCore serverCore = new ServerCore("data/config.properties");
			serverCore.startServer();
		} catch (IOException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, Server might not be connect to the network");
		}
	}
	
}
