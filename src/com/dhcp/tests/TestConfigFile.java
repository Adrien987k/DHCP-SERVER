package com.dhcp.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TestConfigFile {
	public static void main(String[] args) {
		File cfg = new File ("data/config.properties");
		
		try {
			FileReader reader = new FileReader(cfg);
			Properties properties = new Properties();
			properties.load(reader);
			
			System.out.println("Server name: " + properties.getProperty("serverName"));
			System.out.println("IP address: " + properties.getProperty("ipAddress"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
}
