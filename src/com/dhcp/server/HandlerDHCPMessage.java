package com.dhcp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.Option;
import com.dhcp.message.options.DNSOption;
import com.dhcp.message.options.IPLeaseTimeOption;
import com.dhcp.message.options.IpRequestedOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.message.options.RouterOption;
import com.dhcp.message.options.ServerIdentifierOption;
import com.dhcp.message.options.SubnetMaskOption;
import com.dhcp.util.ServerLogger;
import com.dhcp.util.TransactionID;

public class HandlerDHCPMessage extends Thread {

	private ServerLogger logger;
	private ServerCore server;
	
	public static final HashMap<TransactionID,InetAddress> addressPreSelected = new HashMap<>();
	
	public HandlerDHCPMessage(DatagramPacket packet, ServerLogger logger,ServerCore server) {
		this.server = server;
		this.logger = logger;
		DhcpMessage message = DhcpMessage.parseDhcpPacket(packet.getData());
		handle(message);
	}
	
	private void handle(DhcpMessage message) {
		
		logger.messageReceived(message.toString());
		
		/* TODO A la fin du projet (pour ignorer les messages non valides) 
		   
		   
		 */
		
		switch(message.getType()) {
			case DhcpMessage.DHCPDISCOVER: handleDISCOVER(message); break;
			case DhcpMessage.DHCPREQUEST: handleREQUEST(message); break;
			case DhcpMessage.DHCPRELEASE: handleRELEASE(message); break;
			default: logger.systemMessage("Message " + message + " ignored");
		}
	}
	
	private boolean handleDISCOVER(DhcpMessage message) {
		
		DhcpMessage response = new DhcpMessage();
		
		response.setType(DhcpMessage.DHCPOFFER);
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype((short) 1);
		response.setHlen((short) 6);
		response.setHops((short) 0);
		
		response.setXid(message.getXid());
		response.setSecs((short) 0);
		response.setFlags(message.getFlags());
		
		InetAddress addressPreSelected;
		TransactionID xid = new TransactionID(message.getXid());
		if( (addressPreSelected = getServer().getLeaseManager().findAvailableIpAddress(xid)) != null )
			response.setYiaddr(addressPreSelected);
		else
			logger.systemMessage("error");


		response.setSiaddr(getServer().getConfig().getServerAddress());
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		
		if(response.getOptions().getByCode(Option.IP_LEASE_TIME) != null) { 
			response.addOption(response.getOptions().getByCode(Option.IP_LEASE_TIME));
		} else {
			response.addOption(new IPLeaseTimeOption(getServer().getConfig().getLeaseDuration()));
		}
		
		try {
			response.addOption(new ServerIdentifierOption(getServer().getConfig().getServerAddress()));
			response.addOption(new DNSOption(getServer().getConfig().getServerAddress()));
			response.addOption(new SubnetMaskOption(getServer().getConfig().getNetMask()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		response.addOption(new MessageTypeOption(DhcpMessage.DHCPOFFER));
		
		response.setSname("");
		response.setFile("");
		
		logger.messageSent(response.toString());
		
		if(!response.validateBeforeSending()){
			//TODO HANDLE Error
		}
		
		if(sendResponse(response)) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				HandlerDHCPMessage.addressPreSelected.remove(xid);
			}
			return true;
		}
			
		
		return false;
	}
	
	
	//TODO d'autres options pour le renouvellement doivent, peut être, être ajoutées
	//l'ack d'un client ayant déjà une adresse allouée avant mais s'il renvoie un Discover ça marche
	private boolean handleREQUEST(DhcpMessage message) {
		
		InetAddress ipAddressAllocated = null;
		boolean leaseAttributed = false;

		if(message.getCiaddr().getAddress()[0] == 0) {
			InetAddress ipAddressRequested = ( (IpRequestedOption) message.getOptions().getByCode(Option.IP_REQUESTED)).getElements().get(0) ;
			if( ( ipAddressAllocated = getServer().getLeaseManager().tryAddressElseRand(ipAddressRequested
																						, message.getChaddr())) != null) {
				leaseAttributed = true;
			}
		}
		
		DhcpMessage response = new DhcpMessage();
		
		response.setOp(DhcpMessage.BOOTREPLY);
		response.setHtype( (short) 1 );
		response.setHlen( (short) 6 );
		response.setHops( (short) 0 );
		
		response.setXid(message.getXid());
		response.setSecs(0);
		response.setFlags(message.getFlags());
		
		
		response.setGiaddr(message.getGiaddr());
		response.setChaddr(message.getChaddr());
		
		if(leaseAttributed) {
			response.setCiaddr(message.getCiaddr());
			response.setYiaddr(ipAddressAllocated);
			response.setSiaddr(getServer().getConfig().getServerAddress());
			
			response.addOption(message.getOptions().getByCode(Option.IP_LEASE_TIME));
			response.addOption(new MessageTypeOption(DhcpMessage.DHCPPACK));
			response.setType(DhcpMessage.DHCPPACK);
			try {
				response.addOption(new ServerIdentifierOption(getServer().getConfig().getServerAddress()));
				response.addOption(new DNSOption(getServer().getConfig().getServerAddress()));
				response.addOption(new RouterOption(getServer().getConfig().getRouterAddress()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			response.addOption(new MessageTypeOption(DhcpMessage.DHCPNAK));
			response.setType(DhcpMessage.DHCPNAK);
		}

		
		
		response.setSname("");
		response.setFile("");
		
		if(!response.validateBeforeSending()){
			//TODO HANDLE Error
			if(leaseAttributed)
				logger.systemMessage("INVALID DHCPACK ");
			else
				logger.systemMessage("INVALID DHCPNACK ");
		}
		
		if(sendResponse(response)) {
			logger.messageSent(response.toString());
			return true;
		}
			
				
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
						,getServer().getConfig().getNetMask()
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
