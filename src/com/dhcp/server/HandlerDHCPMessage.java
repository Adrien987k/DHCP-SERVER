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
import com.dhcp.message.common.EncodedAddress;
import com.dhcp.message.common.EncodedTime;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.util.ServerLogger;


//TODO: distribuer le travail pour chaque requête dans une classe à part
//			méthodes un peu longues et a fortio ri la classe
public class HandlerDHCPMessage extends Thread {

	@SuppressWarnings("unused")
	private ServerLogger logger;
	private ServerCore server;
	
	
	//TODO: déterminer cette addresse
	InetAddress ciAddressSelected;
	
	@Deprecated
	public HandlerDHCPMessage(DatagramPacket packet, ServerLogger logger) {
		this.logger = logger;
		DhcpMessage message = DhcpMessage.parseDhcpPacket(packet.getData());
		handle(message);
	}
	
	public HandlerDHCPMessage(DatagramPacket packet, ServerLogger logger,ServerCore server) {
		this.server = server;
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
		//TODO normalement terminé
		
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
			response.setSiaddr(getServer().getConfig().getServerAddress());
			response.setGiaddr(message.getGiaddr());
			response.setChaddr(message.getChaddr());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DhcpOptionCollection options = new DhcpOptionCollection();
		 
		if(response.getOptions().getByCode((short) 51) != null) { 
			options.addOption(response.getOptions().getByCode((short) 51));
		} else {
			IPLeaseTimeOption leaseTime = new IPLeaseTimeOption();
			leaseTime.addEncodable(new EncodedTime(getServer().getConfig().getLeaseDuration()));
			options.addOption(leaseTime);
		}
		
		try {
			options.addOption(new ServerIdentifierOption());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		MessageTypeOption typeOption = new MessageTypeOption();
		typeOption.setType(DhcpMessage.DHCPOFFER);
		options.addOption(typeOption);
		
		response.setSname("No name given");
		response.setFile("No film given");
		
		response.setOptions(options);
		
		if(sendResponse(response))
			return true;
		
		return false;
	}

	private boolean handleREQUEST(DhcpMessage message) {
		//TODO non terminé
		
		boolean leaseAttributed = false;
		
		//quel état ? -> ciaddr
		
		//quel addresse ? -> ciaddr / requested (option)
		
		//quel ack ? -> retour du leaseManager

		if(message.getCiaddr().getAddress()[0] == 0) {
			if(getServer().getLeaseManager().tryOldIPAddressElseRand(message.getOptions().getByCode((short) 50)), message.getChaddr()) != null)
				leaseAttributed = true;
		} else {
			
		}
		
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype( (short) 1 );
		response.setLength( (short) 6 );
		response.setHops( (short) 0 );
		
		response.setXid(message.getXid());
		response.setSecs(0);
		response.setFlags(message.getFlags());
		
		response.setCiaddr(message.getCiaddr());
		response.setYiaddr(ciAddressSelected);
		response.setSiaddr(getServer().getConfig().getServerAddress());
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		
		DhcpOptionCollection options = new DhcpOptionCollection();
		
		options.addOption(message.getOptions().getByCode((short) 51));
		
		
		MessageTypeOption typeOption = new MessageTypeOption();
		typeOption.setType(DhcpMessage.DHCPPACK);
		options.addOption(typeOption);
		
		try {
			ServerIdentifierOption sio = new ServerIdentifierOption();
			sio.addEncodable(new EncodedAddress(getServer().getConfig().getServerAddress()));
			options.addOption(sio);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		response.setSname("No name given");
		response.setFile("No film given");
		
		response.setOptions(options);
		
		if(sendResponse(response))
			return true;
				
		return false;
	}
	
	private boolean handleRELEASE(DhcpMessage message) {
		//TODO normalement terminé
		
		if(message.getCiaddr().getAddress()[0] != 0) {
			server.getLeaseManager().release(message.getCiaddr());
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
						,message.getCiaddr()
						,68);		
			else { 
				response = new DatagramPacket(message.getDhcpMessageBytes()
						,message.getDhcpMessageBytes().length
						,InetAddress.getByName("255.255.255.255")
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
	
	public ServerCore getServer() {
		return server;
	}
	
}
