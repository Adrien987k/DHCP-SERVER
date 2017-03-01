package com.dhcp.util;

public class ServerLogger {
	
	public void systemMessage(String s) {
		System.out.println(" -- SYSTEM MESSAGE -- \n" + s + "\n");
	}
	
	public void messageReceived(String s){
		System.out.println(" -- MESSAGE RECEIVED -- \n" + s + "\n");
	}
	
	public void messageSent(String s){
		System.out.println(" -- MESSAGE SENT -- \n" + s + "\n");
	}
}
