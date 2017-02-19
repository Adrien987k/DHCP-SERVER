package com.dhcp.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.dhcp.message.DhcpMessage;

public class TestDhcpPacket {
	public static void main(String[] args) {
		boolean stop = false;
		int port = 67;
		
		try(DatagramSocket ds = new DatagramSocket(port)) {
			ds.setSoTimeout(1000);
			while(!stop) {
					
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
				
				ds.setBroadcast(true);
				try {
					ds.receive(packet);
					System.out.println(DhcpMessage.parseDhcpPacket(packet.getData()).toString());
					
				} catch (IOException e) {
					
				}
				
//				pool.execute(new Thread(new HandlerDHCPMessage(packet,logger)));
			}
		} catch (SocketException se) {
			se.printStackTrace();
		}
	}
}
