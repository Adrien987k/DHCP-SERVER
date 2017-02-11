package com.dhcp.tests;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;

public class TestDhcpMessage {
		
	public static void main(String[] args) {
		
		DhcpMessage dhcpMsg = new DhcpMessage();
		dhcpMsg.setOp((short) 1); 
		dhcpMsg.setHtype((short) 1); 
		dhcpMsg.setHlen((short) 6); 
		dhcpMsg.setHops((short) 0); 
		
		dhcpMsg.setXid( 4294967295L); //Unsigned int max value
		dhcpMsg.setSecs(65535); //Unsigned short max value
		dhcpMsg.setFlags(12345); 
		
		try {
			dhcpMsg.setCiaddr(InetAddress.getByName("0.0.0.0"));
			dhcpMsg.setYiaddr(InetAddress.getByName("255.255.255.255")); 
			dhcpMsg.setSiaddr(InetAddress.getByName("192.90.0.4"));
			dhcpMsg.setGiaddr(InetAddress.getByName("20.251.4.157"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		byte[] chaddr = new byte[]{ (byte) 0, (byte) 5, (byte) 234, (byte) 4,
									(byte) 255, (byte) 123, (byte) 0, (byte) 0,
									(byte) 0, (byte) 0, (byte) 0, (byte) 0,
									(byte) 0, (byte) 0, (byte) 0, (byte) 0,
				                  };
		dhcpMsg.setChaddr(chaddr); 
		
		dhcpMsg.setSname("bonjour"); 
		dhcpMsg.setFile("file");
		
		dhcpMsg.setLength(240); // Without option 53
		
		System.out.println(dhcpMsg);
		System.out.println();
		
		byte[] bufferMsg = dhcpMsg.getDhcpMessageBytes(); 
		
		int cpt = 0;
		for(byte b : bufferMsg){
			System.out.print(b + " ");
			cpt++;
			if(cpt == 100){
				System.out.println();
				cpt = 0;
			}
		}
		System.out.println("\n");
		
		DhcpMessage dhcpMsgResult = DhcpMessage.parseDhcpPacket(bufferMsg);
		
		System.out.println(dhcpMsgResult);
	}
	
}
