package com.dhcp.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.dhcp.util.PropertiesWriter;

public class TestConfigFile {
	public static void main(String[] args) {
		File cfg = new File ("data/config.properties");
		
		try {
			FileReader reader = new FileReader(cfg);
			Properties properties = new Properties();
			properties.load(reader);
			
			PropertiesWriter pw = new PropertiesWriter(null,cfg);
			pw.write(properties);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
}
