package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.Option;
import com.dhcp.message.common.EncodedAddress;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.util.ServerLogger;

public class HandlerDHCPMessage extends Thread {

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
		
		logger.messageReceived(message.toString());
		
		/* TODO A la fin du projet (pour ignorer les messages non valides) 
		   
		   if(!message.isValid()) return;
		 */
		
		switch(message.getType()) {
			case DhcpMessage.DHCPDISCOVER: handleDISCOVER(message); break;
			case DhcpMessage.DHCPREQUEST: handleREQUEST(message); break;
			case DhcpMessage.DHCPRELEASE: handleRELEASE(message); break;
			default: throw new IllegalArgumentException("This DHCP message type is not supported.");
		}
	}
	
	private boolean handleDISCOVER(DhcpMessage message) {
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype((short) 1);
		response.setHlen((short) 6);
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
		
		if(response.getOptions().getByCode(Option.IP_LEASE_TIME) != null) { 
			response.addOption(response.getOptions().getByCode(Option.IP_LEASE_TIME));
		} else {
			response.addOption(new IPLeaseTimeOption(getServer().getConfig().getLeaseDuration()));
		}
		
		try {
			response.addOption(new ServerIdentifierOption());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		response.addOption(new MessageTypeOption(DhcpMessage.DHCPOFFER));
		
		response.setSname("No name given");
		response.setFile("No film given");
		
		if(!response.validateBeforeSending()){
			//TODO HANDLE Error
		}
		
		if(sendResponse(response))
			return true;
		
		return false;
	}

	private boolean handleREQUEST(DhcpMessage message) {
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype( (short) 1 );
		response.setHlen((short) 6);
		response.setHops( (short) 0 );
		
		response.setXid(message.getXid());
		response.setSecs(0);
		response.setFlags(message.getFlags());
		
		response.setCiaddr(message.getCiaddr());
		response.setYiaddr(ciAddressSelected);
		response.setSiaddr(getServer().getConfig().getServerAddress());
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		response.addOption(message.getOptions().getByCode(Option.IP_LEASE_TIME));
		
		response.addOption(new MessageTypeOption(DhcpMessage.DHCPPACK));
		
		try {
			ServerIdentifierOption sio = new ServerIdentifierOption();
			sio.addEncodable(new EncodedAddress(getServer().getConfig().getServerAddress()));
			response.addOption(sio);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		response.setSname("No name given");
		response.setFile("No film given");
		
		if(!response.validateBeforeSending()){
			//TODO HANDLE Error
		}
		
		if(sendResponse(response))
			return true;
				
		return false;
	}
	
	private boolean handleRELEASE(DhcpMessage message) {
		
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
