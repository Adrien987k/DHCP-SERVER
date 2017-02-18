package com.dhcp.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;
import com.dhcp.util.ServerLogger;

public class HandlerDHCPMessage extends Thread {

	ServerLogger logger;
	public HandlerDHCPMessage(DatagramPacket packet, ServerLogger logger) {
		this.logger = logger;
		DhcpMessage message = DhcpMessage.parseDhcpPacket(packet.getData());
		handle(message);
	}
	
	private void handle(DhcpMessage message) {
		switch(message.getType()) {
			case DhcpMessage.DHCPDISCOVER: handleDISCOVER(message); break;
			case DhcpMessage.DHCPREQUEST: handleREQUEST(message); break;
			case DhcpMessage.DHCPRELEASE: handleRELEASE(message); break;
			default: throw new IllegalArgumentException("This DHCP message type is not supported yet");
		}
	}
	
	private boolean handleDISCOVER(DhcpMessage message) {
		//TODO non terminé
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype((short) 1);
		response.setLength((short) 6); 
		response.setHops((short) 0);
		
		response.setXid(message.getXid());
		response.setSecs((short) 0);
		response.setFlags(message.getFlags());
		
		try {
			response.setCiaddr(InetAddress.getByAddress(new byte[4]));
			//TODO ajouter l'adresse IP response.setYiaddr();
			response.setSiaddr(InetAddress.getLocalHost());
			response.setGiaddr(message.getGiaddr());
			response.setChaddr(message.getChaddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//..
		return false;
	}

	private boolean handleREQUEST(DhcpMessage message) {
		//TODO non commencé
		return false;
	}
	
	private boolean handleRELEASE(DhcpMessage message) {
		//TODO non commencé
		return false;
	}
	
}
