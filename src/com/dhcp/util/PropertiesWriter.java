package com.dhcp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.dhcp.lease.Lease;

/**
 * The PropertiesWriter class re-write the config file with the appropriate presentation.
 * This writer makes the config file easy to read.
 * @author MrkArnaud
 *
 */
public class PropertiesWriter {

	/**
	 * The file writer.
	 */
	FileWriter cfg;
	
	/**
	 * The file to write the config into.
	 */
	File file;
	
	/**
	 * The link to the server logger.
	 */
	ServerLogger logger;
	
	
	/**
	 * Create a new writer to be able to write in the specified file.
	 * @param logger The server logger.
	 * @param file The path of the file to write into.
	 */
	public PropertiesWriter(ServerLogger logger, File file) {
		this.logger = logger;
		this.file = file;
		try {
			this.cfg = new FileWriter(file);
		} catch (FileNotFoundException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Can not find the config file: " + file.getPath());
		} catch (IOException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, cannot read properties file");
		}
	}
	
	/**
	 * Write properties into the file.
	 * @param properties Properties to write into.
	 */
	public synchronized void write(Properties properties) {
		
		try {
			
			cfg.write("# -- server informations"+"\n"+"\n");
			
			cfg.write("#server name"+"\n");
			cfg.write("serverName="+properties.getProperty("serverName")+"\n"+"\n");

			cfg.write("#ip address properties"+"\n");
			cfg.write("netMask="+properties.getProperty("netMask")+"\n");
			cfg.write("serverAddress="+properties.getProperty("serverAddress")+"\n");
			cfg.write("routerAddress="+properties.getProperty("routerAddress")+"\n");
			cfg.write("ipAddressBandStart="+properties.getProperty("ipAddressBandStart")+"\n");
			cfg.write("ipAddressBandEnd="+properties.getProperty("ipAddressBandEnd")+"\n");
			cfg.write("addressAvailable="+properties.getProperty("addressAvailable")+"\n"+"\n");
			
			cfg.write("#default lease duration (in seconds)"+"\n");
			cfg.write("leaseDuration="+properties.getProperty("leaseDuration")+"\n"+"\n");
			
			cfg.write("# -- static lease"+"\n");
			cfg.write("staticLeaseAllocated="+properties.getProperty("staticLeaseAllocated")+"\n"+"\n");
			
			for(int i = 1; i <= Integer.parseInt(properties.getProperty("staticLeaseAllocated")); i++) {
				cfg.write("#static lease "+i+"\n");
				cfg.write("lease"+i+".ipAddress="+properties.getProperty("lease"+i+".ipAddress")+"\n");
				cfg.write("lease"+i+".hardwareAddress="+properties.getProperty("lease"+i+".hardwareAddress")+"\n");
				cfg.write("lease"+i+".duration="+properties.getProperty("lease"+i+".duration")+"\n"+"\n");
			}
			
			cfg.close();
			
		} catch (IOException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, cannot read properties file");
		}
		
	}
	
	/**
	 * Add a new static lease to the config file.
	 * @param lease The static lease to add.
	 */
	public synchronized void writeLease(Lease lease) {
		try {
			
			FileReader reader = new FileReader(file);
			Properties properties = new Properties();
			properties.load(reader);
			
			int staticLeaseAllocated = Integer.parseInt(properties.getProperty("staticLeaseAllocated"));
			staticLeaseAllocated++;
			properties.setProperty("lease"+staticLeaseAllocated+".ipAddress=",lease.getIPAddress().getHostAddress());
			properties.setProperty("lease"+staticLeaseAllocated+".hardware=",lease.getHardwareAddress().toString());
			properties.setProperty("lease"+staticLeaseAllocated+".duration=",Long.toString(lease.getDuration()));
			properties.setProperty("staticLeaseAllocated", Integer.toString(staticLeaseAllocated));
			
			write(properties);
			
		} catch (FileNotFoundException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Cannot find preperties file");
		} catch (IOException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, Cannot write properties file");
		}
	}
	
}
