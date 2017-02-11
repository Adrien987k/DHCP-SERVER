package com.dhcp.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.dhcp.util.BufferUtils;

public class DhcpMessage {
	
	public static final int DHCP_MESSAGE_MAX_SIZE = 1024;
	public static final int DHCP_MESSAGE_MIN_SIZE = 244;
	public static InetAddress ZERO_IP_ADDRESS = null;
	
	public static final int DHCPDISCOVER = 0;
	public static final int DHCPOFFER = 1;
	public static final int DHCPREQUEST = 2;
	public static final int DHCPPACK = 3;
	public static final int DHCPNAK = 4;
	public static final int DHCPDECLINE = 5;
	public static final int DHCPRELEASE = 6;
	public static final int DHCPINFORM = 7;
	
	private int type;
	private int length = 0;
	
	private short op = 0;
	private short htype = 0;
	private short hlen= 0;
	private short hops = 0;
	
	private long xid = 0;
	private int secs = 0;
	private int flags = 0;
	
	private InetAddress ciaddr;
	private InetAddress yiaddr;
	private InetAddress siaddr;
	private InetAddress giaddr;
	private byte[] chaddr;
	
	private String sname;
	private String file;
	
	private final static byte[] magicCookie = { (byte) 99, (byte) 130, (byte) 83, (byte) 99, }; 
	private DhcpOptionCollection options = null;
	
	public DhcpMessage(){
		try {
			ZERO_IP_ADDRESS = InetAddress.getByName("0.0.0.0");
			ciaddr = InetAddress.getByName("0.0.0.0");
			yiaddr = InetAddress.getByName("0.0.0.0");
			siaddr = InetAddress.getByName("0.0.0.0");
			giaddr = InetAddress.getByName("0.0.0.0");
		} catch (UnknownHostException e) {
			//TODO LOGGER
			e.printStackTrace();
		}
	}
	
	public byte[] getDhcpMessageBytes(){
		//if(length < DhcpMessage.DHCP_MESSAGE_MIN_SIZE) DhcpMessage.invalidDhcpMessage("length error");
		//if(length > DhcpMessage.DHCP_MESSAGE_MAX_SIZE) DhcpMessage.invalidDhcpMessage("length error");
		
		ByteBuffer buffer = ByteBuffer.allocate(length);
		
		buffer.put((byte) op);
		buffer.put((byte) htype);
		buffer.put((byte) hlen);
		buffer.put((byte) hops);
		buffer.putInt((int) xid);
		buffer.putShort((short) secs);
		buffer.putShort((short) flags);
		
		buffer.put(ciaddr != null ? ciaddr.getAddress() : ZERO_IP_ADDRESS.getAddress());
		buffer.put(yiaddr != null ? yiaddr.getAddress() : ZERO_IP_ADDRESS.getAddress());
		buffer.put(siaddr != null ? siaddr.getAddress() : ZERO_IP_ADDRESS.getAddress());
		buffer.put(giaddr != null ? giaddr.getAddress() : ZERO_IP_ADDRESS.getAddress());
		buffer.put(chaddr, 0, 16);
		
		buffer.put(getSname().getBytes());
		buffer.put(getFile().getBytes());
		
		buffer.put(magicCookie, 0, 4);
		
		try {
			if(options != null) buffer.put(options.getOptionsBytes());
		} catch (InvalidDhcpMessageException e) {
			//TODO do something
		}
		
		//buffer.flip();
		
		return buffer.array();
	}
	
	public static void invalidDhcpMessage(String message) throws InvalidDhcpMessageException {
		//TODO LOGGER error
		System.out.println("ERROR : " + message);
		throw new InvalidDhcpMessageException(message);
	}
	
	private static boolean validateMagicCookie(byte[] test){
		return    (test[0] == (byte) 99)
			   && (BufferUtils.byteToShort(test[1]) == (short) 130)
			   && (test[2] == (byte) 83)
			   && (test[3] == (byte) 99);
	}
	
	public static DhcpMessage parseDhcpPacket(byte[] byteTab) {
		ByteBuffer buffer = null; 
		DhcpMessage dhcpMessage = new DhcpMessage();
		try{
			if(byteTab == null) invalidDhcpMessage("empty dhcp message received");
			
			//Condition désactiver juste pour le test sans option
			//if(byteTab.length < DHCP_MESSAGE_MIN_SIZE) invalidDhcpMessage("incomplete dhcp message received");
			
			buffer = ByteBuffer.wrap(byteTab);
			
			short op = BufferUtils.byteToShort(buffer.get());
			if(op == 2) invalidDhcpMessage("BOOTREPLY received");
			if(op != 1 && op != 2) invalidDhcpMessage("dhcp message received with invalid op field");
			dhcpMessage.setOp(op);
			
			dhcpMessage.setHtype(BufferUtils.byteToShort(buffer.get()));
			dhcpMessage.setHlen(BufferUtils.byteToShort(buffer.get()));
			dhcpMessage.setHops(BufferUtils.byteToShort(buffer.get()));
			dhcpMessage.setXid(BufferUtils.intToLong(buffer.getInt()));
			dhcpMessage.setSecs(BufferUtils.shortToInt(buffer.getShort()));
			dhcpMessage.setFlags(BufferUtils.shortToInt(buffer.getShort()));
			
			byte[] buf = new byte[4];
			try {
				buffer.get(buf);
				dhcpMessage.setCiaddr(InetAddress.getByAddress(buf));
				buffer.get(buf);
				dhcpMessage.setYiaddr(InetAddress.getByAddress(buf));
				buffer.get(buf);
				dhcpMessage.setSiaddr(InetAddress.getByAddress(buf));
				buffer.get(buf);
				dhcpMessage.setGiaddr(InetAddress.getByAddress(buf));
			} catch (UnknownHostException e) {
				invalidDhcpMessage("dhcp message with incorrect adresses received");
			}
			buf = new byte[16];
			buffer.get(buf);
			dhcpMessage.setChaddr(buf);
			buf = new byte[64];
			buffer.get(buf);
			dhcpMessage.setSname(new String(buf));
			buf = new byte[128];
			buffer.get(buf);
			dhcpMessage.setFile(new String(buf));
			buf = new byte[4];
			buffer.get(buf);
			if(!validateMagicCookie(buf)) invalidDhcpMessage("dhcp message with invalid magic cookie received");
			
			if(buffer.hasRemaining())
				dhcpMessage.setOptions(DhcpOptionCollection.parseDhcpOptions(buffer));
			else invalidDhcpMessage("dhcp message received with no option");
			
			dhcpMessage.length = DHCP_MESSAGE_MIN_SIZE + dhcpMessage.options.totalLength();
			
		} catch(InvalidDhcpMessageException e){
			//return null; 
		}
		
		return dhcpMessage;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("DHCP MESSAGE\n");
		sb.append("TYPE : " + type +  "\n");
		sb.append("OP : " + op + " HTYPE : " + htype + " HLEN : " + hlen + " HOPS : " + hops + "\n");
		sb.append("XID : " + xid + " SECS : " + secs + " FLAGS : " + flags +  "\n");
		sb.append("CIADDR : " + ciaddr + "\n");
		sb.append("YIADDR : " + yiaddr + "\n");
		sb.append("SIADDR : " + siaddr + "\n");
		sb.append("GIADDR : " + giaddr + "\n");
		sb.append("CHADDR : ");
		for(byte b : chaddr) sb.append(b + " ");
		sb.append("\nSNAME : " + sname + "\n");
		sb.append("FILE : " + file + "\n");
		sb.append("MAGIC COOKIE : ");
		for(byte b : magicCookie) sb.append(b + " ");
		sb.append("\n");
		sb.append(options);
		
		return sb.toString();
	}
	
	/*GETTERS AND SETTERS*/

	public int getType() {
		return type;
	}
	
	public int getLength(){
		return length;
	}
	
	public void setLength(int length){
		this.length = length;
	}

	public short getOp() {
		return op;
	}

	public void setOp(short op) {
		this.op = op;
	}

	public short getHtype() {
		return htype;
	}

	public void setHtype(short htype) {
		this.htype = htype;
	}

	public short getHlen() {
		return hlen;
	}

	public void setHlen(short hlen) {
		this.hlen = hlen;
	}

	public short getHops() {
		return hops;
	}

	public void setHops(short hops) {
		this.hops = hops;
	}

	public long getXid() {
		return xid;
	}

	public void setXid(long xid) {
		this.xid = xid;
	}

	public int getSecs() {
		return secs;
	}

	public void setSecs(int secs) {
		this.secs = secs;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public InetAddress getCiaddr() {
		return ciaddr;
	}

	public void setCiaddr(InetAddress ciaddr) {
		this.ciaddr = ciaddr;
	}

	public InetAddress getYiaddr() {
		return yiaddr;
	}

	public void setYiaddr(InetAddress yiaddr) {
		this.yiaddr = yiaddr;
	}

	public InetAddress getSiaddr() {
		return siaddr;
	}

	public void setSiaddr(InetAddress siaddr) {
		this.siaddr = siaddr;
	}

	public InetAddress getGiaddr() {
		return giaddr;
	}

	public void setGiaddr(InetAddress giaddr) {
		this.giaddr = giaddr;
	}

	public byte[] getChaddr() {
		return chaddr;
	}

	public void setChaddr(byte[] chaddr) {
		chaddr = Arrays.copyOf(chaddr, 16);
		this.chaddr = chaddr;
	}

	public String getSname() {
		sname = BufferUtils.resize(sname, 64);
		return sname;
	}

	public void setSname(String sname) {
		sname = BufferUtils.resize(sname, 64);
		this.sname = sname;
	}

	public String getFile() {
		file = BufferUtils.resize(file, 128);
		return file;
	}

	public void setFile(String file) {
		file = BufferUtils.resize(file, 128);
		this.file = file;
	}
	
	public void setOptions(DhcpOptionCollection options){
		this.options = options;
	}
	
	/*END OF GETTERS AND SETTERS*/
	
	
}
