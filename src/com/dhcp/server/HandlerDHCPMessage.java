package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TreeMap;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.Option;
import com.dhcp.message.common.EncodedAddress;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.IpRequestedOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.util.ServerLogger;
import com.dhcp.util.TransactionID;


//TODO: distribuer le travail pour chaque requête dans une classe à part
//			méthodes un peu longues et a fortio ri la classe l'est aussi
// ou alors tout garder et faire un handler par client (donc qui gère la transaction complète)
// ou alors une liste avec les traitements en cours contenant l'xid et l'addresse
public class HandlerDHCPMessage extends Thread {

	private ServerLogger logger;
	private ServerCore server;
	
	public static final TreeMap<TransactionID,InetAddress> addressPreSelected = new TreeMap<>();
	
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
	
	private synchronized void handle(DhcpMessage message) {
		System.out.println(message);
		
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
	
	private synchronized boolean handleDISCOVER(DhcpMessage message) {
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype((short) 1);
		response.setHlen((short) 6);
		response.setHops((short) 0);
		
		response.setXid(message.getXid());
		response.setSecs((short) 0);
		response.setFlags(message.getFlags());
		
		InetAddress addressPreSelected; 
		if( (addressPreSelected = getServer().getLeaseManager().findAvailableIpAddress(new TransactionID(message.getXid()))) != null )
			response.setYiaddr(addressPreSelected);
		
		response.setSiaddr(getServer().getConfig().getServerAddress());
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		
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
		
		if(sendResponse(response)) {
			
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				HandlerDHCPMessage.addressPreSelected.remove(new TransactionID(message.getXid()));
			}
			return true;
		}
			
		
		return false;
	}

	private synchronized boolean handleREQUEST(DhcpMessage message) {
		
		InetAddress ipAddressAllocated = null;
		boolean leaseAttributed = false;

		if(message.getCiaddr().getAddress()[0] == 0) {
			InetAddress ipAddressRequested = ( (IpRequestedOption) message.getOptions().getByCode(Option.IP_REQUESTED)).getElements().get(0) ;
			if( ( ipAddressRequested = getServer().getLeaseManager().tryAddressElseRand(
																						ipAddressRequested
																						, message.getChaddr())) != null);
				leaseAttributed = true;
		}
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype( (short) 1 );
		response.setLength( (short) 6 );
		response.setHops( (short) 0 );
		
		response.setXid(message.getXid());
		response.setSecs(0);
		response.setFlags(message.getFlags());
		
		
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		if(leaseAttributed) {
			response.setCiaddr(message.getCiaddr());
			response.setYiaddr(ipAddressAllocated);response.setSiaddr(getServer().getConfig().getServerAddress());
			
			response.addOption(message.getOptions().getByCode(Option.IP_LEASE_TIME));
			response.addOption(new MessageTypeOption(DhcpMessage.DHCPPACK));
		} else {
			response.addOption(new MessageTypeOption(DhcpMessage.DHCPNAK));
		}
		
		try {
			ServerIdentifierOption sio = new ServerIdentifierOption();
			sio.addEncodable(new EncodedAddress(getServer().getConfig().getServerAddress()));
			response.addOption(sio);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		response.setSname("No name given");
		response.setFile("No file given");
		
		if(!response.validateBeforeSending()){
			//TODO HANDLE Error
		}
		
		if(sendResponse(response))
			return true;
				
		return false;
	}
	
	private synchronized boolean handleRELEASE(DhcpMessage message) {
		
		if(message.getCiaddr().getAddress()[0] != 0) {
			server.getLeaseManager().release(message.getCiaddr());
			return true;
		}
		return false;
	}
	
	private synchronized boolean sendResponse(DhcpMessage message) {
		
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
