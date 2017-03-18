package com.dhcp.util;

import java.util.GregorianCalendar;

public class ServerLogger {
	
	private static GregorianCalendar calendar = new GregorianCalendar();
	
	public final static int SEVERITY_LOW = 0;
	public final static int SEVERITY_MEDIUM = 1;
	public final static int SEVERITY_HIGH = 2;
	
	private static void displayTime(){
		System.out.print(calendar.getTime());
	}
	
	public static void systemMessage(String message) {
		displayTime();
		System.out.println(" -- SYSTEM MESSAGE -- " + message);
	}
	
	public static void messageReceived(String message){
		displayTime();
		System.out.println(" -- MESSAGE RECEIVED -- " + message);
	}
	
	public static void messageSent(String message){
		displayTime();
		System.out.println(" -- MESSAGE SENT -- " + message);
	}
	
	public static void messageSent(){
		displayTime();
		messageSent("");
	}
	
	public static void error(int severity, String message){
		displayTime();
		switch(severity){
		case SEVERITY_LOW: System.out.println("ERROR: SEVERITY LOW: " + message);
		case SEVERITY_MEDIUM: System.out.println("ERROR: SEVERITY MEDIUM: " + message);
		case SEVERITY_HIGH: System.out.println("ERROR: SEVERITY HIGH: " + message);
		}
	}
	
}
