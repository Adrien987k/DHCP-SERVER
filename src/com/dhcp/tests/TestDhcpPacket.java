package com.dhcp.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;

public class TestDhcpPacket {
	public static void main(String[] args) {
		boolean stop = false;
		int port = 67;
		byte[] messageTest = new byte[] {1,1,0,0,1,1};
		
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					DatagramSocket ds1234 = new DatagramSocket(1234);
					try {
						byte[] handler;
						ds1234.receive(new DatagramPacket((handler = new byte[messageTest.length]), messageTest.length));
						System.out.println("Received: " + handler.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		DatagramSocket ds;
		for(int i = 0; i < 5; i++) {
		try {
			ds = new DatagramSocket();
			ds.setBroadcast(true);
			
			
			DatagramPacket message=new DatagramPacket(messageTest
					,messageTest.length
					,InetAddress.getByName("google.com")
					,1234);
			System.out.println("message created " + message.toString());
			ds.send(message);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		}	
		/*
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
		}*/
	}
}
