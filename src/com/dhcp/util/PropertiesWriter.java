package com.dhcp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.dhcp.lease.Lease;

public class PropertiesWriter {

	FileWriter cfg;
	File file;
	ServerLogger logger;
	
	public PropertiesWriter(ServerLogger logger, File file) {
		this.logger = logger;
		this.file = file;
		try {
			this.cfg = new FileWriter(file);
		} catch (FileNotFoundException e) {
			logger.systemMessage("Can not find the config file: " + file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void write(Properties properties) {
		
		try {
			
			cfg.write("# -- server informations"+"\n"+"\n");
			
			cfg.write("#server name"+"\n");
			cfg.write("serverName="+properties.getProperty("serverName")+"\n"+"\n");

			cfg.write("#ip address properties"+"\n");
			cfg.write("ipAddress="+properties.getProperty("ipAddress")+"\n");
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
			e.printStackTrace();
		}
		
	}
	
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
