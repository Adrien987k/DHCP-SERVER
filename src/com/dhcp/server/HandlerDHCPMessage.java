package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.DhcpOptionCollection;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.util.ServerLogger;

public class HandlerDHCPMessage extends Thread {

	ServerLogger logger;
	
	//TODO: déterminer cette addresse
	InetAddress ciAddressSelected;
	
	
	public HandlerDHCPMessage(DatagramPacket packet, ServerLogger logger) {
		this.logger = logger;
		DhcpMessage message = DhcpMessage.parseDhcpPacket(packet.getData());
		handle(message);
	}
	
	private void handle(DhcpMessage message) {
		System.out.println(message);
		
		switch(message.getType()) {
			case DhcpMessage.DHCPDISCOVER: handleDISCOVER(message); break;
			case DhcpMessage.DHCPREQUEST: handleREQUEST(message); break;
			case DhcpMessage.DHCPRELEASE: handleRELEASE(message); break;
			default: throw new IllegalArgumentException("This DHCP message type is not supported.");
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
			response.setYiaddr(ciAddressSelected);
			response.setSiaddr(InetAddress.getLocalHost());
			response.setGiaddr(message.getGiaddr());
			response.setChaddr(message.getChaddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DhcpOptionCollection options = new DhcpOptionCollection();
		/* Si un lease time est demandé 
		if() { 
			
		} else {
			sinon attribuer celui par défaut */
			options.addOption(new IPLeaseTimeOption());
		//}
		
		try {
			options.addOption(new ServerIdentifierOption());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		MessageTypeOption typeOption = new MessageTypeOption();
		typeOption.setType(DhcpMessage.DHCPOFFER);
		options.addOption(typeOption);
		
		//TODO
		response.setSname("");
		response.setFile("");
		
		response.setOptions(options);
		
		if(sendResponse(response))
			return true;
		
		return false;
	}

	private boolean handleREQUEST(DhcpMessage message) {
		//TODO non terminé
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype( (short) 1 );
		response.setLength( (short) 6 );
		response.setHops( (short) 0 );
		
		response.setXid(message.getXid());
		response.setSecs(0);
		response.setFlags(message.getFlags());
		
		try {
			response.setCiaddr(message.getCiaddr());
			response.setYiaddr(ciAddressSelected);
			response.setSiaddr(InetAddress.getLocalHost());
			response.setGiaddr(message.getGiaddr());
			response.setChaddr(message.getChaddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		DhcpOptionCollection options = new DhcpOptionCollection();
		
		//TODO: à récupérer depuis le REQUEST
		options.addOption(new IPLeaseTimeOption());
		
		
		MessageTypeOption typeOption = new MessageTypeOption();
		typeOption.setType(DhcpMessage.DHCPPACK);
		options.addOption(typeOption);
		
		try {
			options.addOption(new ServerIdentifierOption());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//TODO
		response.setSname("");
		response.setFile("");
		
		response.setOptions(options);
		
		if(sendResponse(response))
			return true;
				
		return false;
	}
	
	private boolean handleRELEASE(DhcpMessage message) {
		//TODO non terminé
		
		if(message.getCiaddr().getAddress()[0] != 0) {
			ServerCore.releaseIPAddress(message.getCiaddr());
			return true;
		}
		return false;
	}
	
	private boolean sendResponse(DhcpMessage message) {
		
		try {
			
			DatagramSocket ds = new DatagramSocket();
			ds.setBroadcast(true);
			
			DatagramPacket response;
			if(message.getCiaddr().getAddress()[0] != 0 )
				response = new DatagramPacket(message.getDhcpMessageBytes()
						,message.getDhcpMessageBytes().length
						,InetAddress.getByAddress(message.getChaddr())
						,68);		
			else { 
				response = new DatagramPacket(message.getDhcpMessageBytes()
						,message.getDhcpMessageBytes().length
						,InetAddress.getByAddress(new byte[] {1,1,1,1})
						,68);
				}
			
			ds.send(response);
			ds.close();
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidDhcpMessageException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
}
