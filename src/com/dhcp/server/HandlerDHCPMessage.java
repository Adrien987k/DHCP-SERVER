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

	private ServerCore server;
	
	public static final HashMap<TransactionID,InetAddress> addressPreSelected = new HashMap<>();
	
	public HandlerDHCPMessage(DatagramPacket packet, ServerCore server) {
		this.server = server;
		DhcpMessage message = DhcpMessage.parseDhcpPacket(packet.getData());
		if(message.isValid()){ 
			handle(message);
		}
	}
	
	private void handle(DhcpMessage message) {
		
		ServerLogger.messageReceived(message.toString());
		
		switch(message.getType()) {
			case DhcpMessage.DHCPDISCOVER: handleDISCOVER(message); break;
			case DhcpMessage.DHCPREQUEST: handleREQUEST(message); break;
			case DhcpMessage.DHCPRELEASE: handleRELEASE(message); break;
			default: ServerLogger.systemMessage("Message " + message + " ignored");
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
		else{			
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Cannot handle DHCPDISCOVER message, ignored");
			return false;
		}


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
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Try to put invalid ip address in a dhcp message, cancel operation");
			return false;
		}
		
		response.addOption(new MessageTypeOption(DhcpMessage.DHCPOFFER));
		
		response.setSname("");
		response.setFile("");
		
		ServerLogger.messageSent(response.toString());
		
		if(!response.validateBeforeSending()){
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "try to send invalid dhcp message");
			return false;
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
	
	
	//TODO d'autres options pour le renouvellement doivent, peut �tre, �tre ajout�es
	//l'ack d'un client ayant d�j� une adresse allou�e avant mais s'il renvoie un Discover �a marche
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
				ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Try to put invalid ip address in a dhcp message, cancel operation");
				return false;
			}
		} else {
			response.addOption(new MessageTypeOption(DhcpMessage.DHCPNAK));
			response.setType(DhcpMessage.DHCPNAK);
		}

		response.setSname("");
		response.setFile("");
		
		if(!response.validateBeforeSending()){
			if(leaseAttributed)
				ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Try to send invalid DHCPACK message, cancel operation");
			else
				ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Try to send invalid DHCPNAK message, cancel operation");
			
			return false;
		}
		
		if(sendResponse(response)) {
			ServerLogger.messageSent(response.toString());
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
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Socket Exception");
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Sending dhcp message failed, cancel operation");
			return false;
		} catch (UnknownHostException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "Try to use an unknown host");
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Sending dhcp message failed, cancel operation");
			return false;
		} catch (InvalidDhcpMessageException e) {
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Try to send invalid dhcp message, cancel operation");
			return false;
		} catch (IOException e) {
			ServerLogger.error(ServerLogger.SEVERITY_HIGH, "IO Exception, Server might not be connect to the network");
			ServerLogger.error(ServerLogger.SEVERITY_MEDIUM, "Sending dhcp message failed, cancel operation");
			return false;
		}
		
		return true;
	}
	
	public ServerCore getServer() {
		return server;
	}
	
}
