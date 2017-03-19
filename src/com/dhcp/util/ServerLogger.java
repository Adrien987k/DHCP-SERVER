package com.dhcp.util;

import java.util.GregorianCalendar;

/**
 * The ServerLogger class provides an interface to write everything that happend
 * during the running of the server.
 * @author Arnaud
 *
 */
public class ServerLogger {
	
	/**
	 * The calendar to add a time information.
	 */
	private static GregorianCalendar calendar = new GregorianCalendar();
	
	public final static int SEVERITY_LOW = 0;
	public final static int SEVERITY_MEDIUM = 1;
	public final static int SEVERITY_HIGH = 2;
	
	/**
	 * Write the current time on the logger.
	 */
	private static void displayTime(){
		System.out.print(calendar.getTime());
	}
	
	/**
	 * Write the message as a system message.
	 * @param message The message to write.
	 */
	public static void systemMessage(String message) {
		displayTime();
		System.out.println(" -- SYSTEM MESSAGE -- " + message);
	}
	
	/**
	 * Write the message and the annotation that a DHCP message is received.
	 * @param message The message to write.
	 */
	public static void messageReceived(String message){
		displayTime();
		System.out.println(" -- MESSAGE RECEIVED -- " + message);
	}
	
	/**
	 * Write the message and the annotation that a DHCP message is sent.
	 * @param message The message to write.
	 */
	public static void messageSent(String message){
		displayTime();
		System.out.println(" -- MESSAGE SENT -- " + message);
	}
	
	/**
	 * Write that a DHCP message has been sent.
	 */
	public static void messageSent(){
		displayTime();
		messageSent("");
	}
	
	/**
	 * Write the message as an error message.
	 * @param severity TODO
	 * @param message The message to write.
	 */
	public static void error(int severity, String message){
		displayTime();
		switch(severity){
		case SEVERITY_LOW: System.out.println("ERROR: SEVERITY LOW: " + message);
		case SEVERITY_MEDIUM: System.out.println("ERROR: SEVERITY MEDIUM: " + message);
		case SEVERITY_HIGH: System.out.println("ERROR: SEVERITY HIGH: " + message);
		}
	}
	
}
