package com.dhcp.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
						DatagramPacket receive = new DatagramPacket((handler = new byte[messageTest.length]), messageTest.length);
						ds1234.receive(receive);
						System.out.println("-- RECIEVED -- ");
						
						byte[] buffer = receive.getData();
						for(byte b: buffer) {
							System.out.print(b);
						}
						//System.out.println(DhcpMessage.parseDhcpPacket(buffer));
							
						//System.out.println(DhcpMessage.parseDhcpPacket(receive.getData()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				return;
			}
		});
		
		DatagramSocket ds;
		for(int i = 0; i < 5; i++) {
		try {
			ds = new DatagramSocket();
			ds.setBroadcast(true);
			
			DatagramPacket message=new DatagramPacket(messageTest
					,messageTest.length
					,InetAddress.getByName("169.254.255.255")
					//,InetAddress.getByName("169.254.244.39")
					,1234);
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
