package com.dhcp.util;

public class ServerLogger {
	
	public void systemMessage(String s) {
		System.out.println(" -- SYSTEM MESSAGE -- " + s);
	}
	
	public void messageReceived(String s){
		System.out.println(" -- MESSAGE RECEIVED -- \n" + s);
	}
}
