package com.dhcp.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

import com.dhcp.lease.Lease;
import com.dhcp.util.HardwareAddress;
import com.dhcp.util.ServerLogger;

/**
 * The ServerConfig class provides an interface to access to the server configuration.
 * The config is loaded from a config file.
 * The config provides required information for the good working of the server.
 * It contains vital information about addresses to attribute or netMask.
 * @author Arnaud
 *
 */
public class ServerConfig {

	/**
	 * The name of the server.
	 */
	private String serverName;
	
	/**
	 * The net mask of the server.
	 * Message sent by broadcast will be sent according to this mask.
	 */
	private InetAddress netMask;
	
	/**
	 * The IP address of the server.
	 */
	private InetAddress serverAddress;
	
	/**
	 * The router address to give.
	 */
	private InetAddress routerAddress;
	
	/**
	 * The start of the band of IP addresses.
	 */
	private InetAddress ipAddressBandStart;
	
	/**
	 * The end of the band of IP addresses.
	 */
	private InetAddress ipAddressBandEnd;
	
	/**
	 * The default duration of future lease.
	 */
	private long leaseDuration;
	
	/**
	 * The number of addresses available.
	 */
	private int addressAvailable;
	
	/**
	 * A hash map containing all static leases.
	 */
	private final HashMap<InetAddress,Lease> staticLeases = new HashMap<>();
	
	/**
	 * Create a server config according to the config file.
	 * @param config The path of the config file.
	 */
	public ServerConfig(String config) {
		ServerLogger.systemMessage("Loading config from " + config);
		File cfg = new File (config);
	
		try {
			FileReader reader = new FileReader(cfg);
			Properties properties = new Properties();
			properties.load(reader);
			
			this.serverName = properties.getProperty("serverName");
			this.netMask= InetAddress.getByName(properties.getProperty("netMask"));
			this.serverAddress = InetAddress.getByName(properties.getProperty("serverAddress"));
			this.routerAddress = InetAddress.getByName(properties.getProperty("routerAddress"));
			this.ipAddressBandStart = InetAddress.getByName(properties.getProperty("ipAddressBandStart"));
			this.ipAddressBandEnd = InetAddress.getByName(properties.getProperty("ipAddressBandEnd"));
			
			this.leaseDuration = Long.parseLong(properties.getProperty("leaseDuration"));
			this.addressAvailable = Integer.parseInt(properties.getProperty("addressAvailable"));
			
			for(int i = 1; i <= Integer.parseInt(properties.getProperty("staticLeaseAllocated")); i ++) {
				staticLeases.put(InetAddress.getByName(properties.getProperty("lease"+i+".ipAddress")),
									new Lease(InetAddress.getByName(properties.getProperty("lease"+i+".ipAddress"))
											,HardwareAddress.parseHardwareAddress(properties.getProperty("lease"+i+".hardwareAddress"))
											,Long.parseLong(properties.getProperty("lease"+i+".duration"))));
			}
			
		} catch (FileNotFoundException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Could not find file \"" + config + "\"");
		} catch (IOException e ) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, Server might not be connect to the network");
		}
		
	}

	/**
	 * @return The server name.
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @return The net mask.
	 */
	public InetAddress getNetMask() {
		return netMask;
	}
	
	/**
	 * @return The IP address of the server.
	 */
	public InetAddress getServerAddress() {
		return serverAddress;
	}

	/**
	 * @return The router address.
	 */
	public InetAddress getRouterAddress() {
		return routerAddress;
	}
	
	/**
	 * @return The start of the band of IP addresses.
	 */
	public InetAddress getIpAddressBandStart() {
		return ipAddressBandStart;
	}

	/**
	 * @return The end of the band of IP addresses.
	 */
	public InetAddress getIpAddressBandEnd() {
		return ipAddressBandEnd;
	}

	/**
	 * @return The default lease duration.
	 */
	public long getLeaseDuration() {
		return leaseDuration;
	}

	/**
	 * @return The number of addresses available.
	 */
	public int getAddressAvailable() {
		return addressAvailable;
	}
	
	/**
	 * @return The hash map with static leases.
	 */
	public HashMap<InetAddress,Lease> getStaticLeases() {
		return staticLeases;
	}

}
