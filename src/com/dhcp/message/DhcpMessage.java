package com.dhcp.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.dhcp.util.BufferUtils;

public class DhcpMessage {
	
	public static final int DHCP_MESSAGE_SIZE = 1024;
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
	//private Map<Integer, DhcpOption> options = new HashMap<>();
	
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
		ByteBuffer buffer = ByteBuffer.allocate(DHCP_MESSAGE_SIZE);
		
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
		
		buffer.put(sname.getBytes());
		buffer.put(file.getBytes());
		
		//TODO put options in the buffer
		
		buffer.flip();
		
		return buffer.array();
	}
	
	public static void invalidDhcpMessage(String message) throws InvalidDhcpMessageException {
		//TODO LOGGER error
		throw new InvalidDhcpMessageException(message);
	}
	
	public static DhcpMessage parseDhcpPacket(byte[] byteTab, int length) {
		ByteBuffer buffer = null; 
		DhcpMessage dhcpMessage = new DhcpMessage();
		try{
			if(byteTab == null) invalidDhcpMessage("empty dhcp message received");
			if(byteTab.length < DHCP_MESSAGE_MIN_SIZE) invalidDhcpMessage("incomplete dhcp message received");
			buffer = ByteBuffer.allocate(byteTab.length);
			
			short op = buffer.get();
			if(op != 1) invalidDhcpMessage("reply received");
			
			dhcpMessage.setHtype(buffer.get());
			dhcpMessage.setHlen(buffer.get());
			dhcpMessage.setHops(buffer.get());
			dhcpMessage.setXid(buffer.get());
			dhcpMessage.setSecs(buffer.get());
			dhcpMessage.setFlags(buffer.get());
			
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
			if(!Arrays.equals(buf, magicCookie)) invalidDhcpMessage("dhcp message with invalid magic cookie received");
			
			//TODO read options
			
		} catch(InvalidDhcpMessageException e){
			e.printStackTrace();
		}
		
		return dhcpMessage;
	}
	
	/*GETTERS AND SETTERS*/

	public int getType() {
		return type;
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
		return sname;
	}

	public void setSname(String sname) {
		sname = BufferUtils.resize(sname, 64);
		this.sname = sname;
	}

	public String getFile() {
		sname = BufferUtils.resize(sname, 128);
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	/*END OF GETTERS AND SETTERS*/
	
	
}
