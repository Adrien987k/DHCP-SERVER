package com.dhcp.tests;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dhcp.message.DhcpMessage;
import com.dhcp.message.DhcpOptionFactory;
import com.dhcp.message.InvalidDhcpMessageException;
import com.dhcp.message.Option;
import com.dhcp.message.common.EncodedAddress;
import com.dhcp.message.options.IpRequestedOption;
import com.dhcp.message.options.MessageTypeOption;
import com.dhcp.util.HardwareAddress;

public class TestDhcpMessage {
		
	public static void main(String[] args) {
		
		DhcpMessage dhcpMsg = new DhcpMessage();
		dhcpMsg.setOp((short) 1); 
		dhcpMsg.setHtype((short) 1); 
		dhcpMsg.setHlen((short) 6); 
		dhcpMsg.setHops((short) 0); 
		
		dhcpMsg.setXid( 4294967295L); //Unsigned int max value
		dhcpMsg.setSecs(65535); //Unsigned short max value
		dhcpMsg.setFlags(12345); 
		
		try {
			dhcpMsg.setCiaddr(InetAddress.getByName("0.0.0.0"));
			dhcpMsg.setYiaddr(InetAddress.getByName("255.255.255.255")); 
			dhcpMsg.setSiaddr(InetAddress.getByName("192.90.0.4"));
			dhcpMsg.setGiaddr(InetAddress.getByName("20.251.4.157"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		byte[] hardwareAddressBytes = new byte[]{ (byte) 0, (byte) 5, (byte) 234, (byte) 4,
									(byte) 255, (byte) 123, (byte) 0, (byte) 0,
									(byte) 0, (byte) 0, (byte) 0, (byte) 0,
									(byte) 0, (byte) 0, (byte) 0, (byte) 0,
				                  };
		HardwareAddress chaddr = new HardwareAddress(hardwareAddressBytes);
		dhcpMsg.setChaddr(chaddr); 
		
		dhcpMsg.setSname("bonjour"); 
		dhcpMsg.setFile("file");
		
		MessageTypeOption option = null;
		
		option = (MessageTypeOption) DhcpOptionFactory.buildDhcpOption(Option.MESSAGE_TYPE);
		
		option.setType(DhcpMessage.DHCPREQUEST);
		
		if(!dhcpMsg.addOption(option)){
			System.out.println("ERROR ADD OPTION 53");
		}
		
		IpRequestedOption ipOption = null;
	
		ipOption = (IpRequestedOption) DhcpOptionFactory.buildDhcpOption(Option.IP_REQUESTED);

		try {
			ipOption.addEncodable(new EncodedAddress(InetAddress.getByName("192.68.102.13")));
		} catch (UnknownHostException e) {
			System.out.println("ERROR IP OPTION");
		}
		
		if(!dhcpMsg.addOption(ipOption)){
			System.out.println("ERROR ADD OPTION 50");
		}
		
		dhcpMsg.setLength(250);
		
		System.out.println(dhcpMsg);
		System.out.println();
		
		byte[] bufferMsg = null;
		try {
			bufferMsg = dhcpMsg.getDhcpMessageBytes();
		} catch (InvalidDhcpMessageException e) {
			System.out.println("CANNOT GET BUFFER MESSAGE BYTES");
		} 
		
		int cpt = 0;
		for(byte b : bufferMsg){
			System.out.print(b + " ");
			cpt++;
			if(cpt == 100){
				System.out.println();
				cpt = 0;
			}
		}
		System.out.println("\n");
		
		DhcpMessage dhcpMsgResult = DhcpMessage.parseDhcpPacket(bufferMsg);
		
		System.out.println(dhcpMsgResult);
	}
	
}
