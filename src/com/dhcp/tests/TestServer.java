package com.dhcp.tests;

import java.io.IOException;

import com.dhcp.server.ServerCore;

public class TestServer {
	
	public static void main(String[] args) {
		try {
			ServerCore serverCore = new ServerCore("data/config.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
