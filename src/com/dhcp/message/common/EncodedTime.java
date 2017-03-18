package com.dhcp.message.common;

import com.dhcp.util.BufferUtils;

public class EncodedTime implements Encodable {
	
	private static long[] masks = new long[]
			{ 4278190080L, // 1111 1111 0000 0000 0000 0000 0000 0000
				16711680L, // 0000 0000 1111 1111 0000 0000 0000 0000
				   65280L, // 0000 0000 0000 0000 1111 1111 0000 0000
                     255L, // 0000 0000 0000 0000 0000 0000 1111 1111
		    };
	
	private long time = 0;
	private short[] encodedTime = new short[4];
	
	private long days;
	private long hours; 
	private long minutes;
	private long seconds;
	
	public EncodedTime(long days, long hours, long minutes, long seconds){
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		
		encode();
	}
	
	public EncodedTime(long seconds){
		days = seconds / (24L * 3600L);
		seconds -= days * (24L * 3600L);
		hours = seconds / 3600L;
		seconds -= hours * 3600L;
		minutes= seconds / 60L;
		seconds -= minutes* 60L;
		this.seconds = seconds;
		
		encode();
	}
	
	private void encode(){
		time += (days * (24L * 3600L));
		time += (hours * 3600L);
		time += (minutes * 60L);
		time += seconds;
		
		for(int i = 0; i < 4; i++){
			encodedTime[i] = (short)  ((time & masks[i]) >> (3 - i) * 8);
			
		}
	}
	
	@Override
	public byte[] getBytes(){
		return new byte[]{ (byte) encodedTime[0],
						   (byte) encodedTime[1],
						   (byte) encodedTime[2],
						   (byte) encodedTime[3] };
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EncodedTime parseEncodable(byte[] buffer){
		return EncodedTime.parseEncodedTime(buffer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EncodedTime getElement(){
		return this;
	}
	
	@Override
	public int getLength(){
		return 4;
	}
	
	public static EncodedTime parseEncodedTime(byte[] buffer){
		long time = 0;
		time += BufferUtils.byteToShort(buffer[0]) << 24;
		time += BufferUtils.byteToShort(buffer[1]) << 16;
		time += BufferUtils.byteToShort(buffer[2]) << 8;
		time += BufferUtils.byteToShort(buffer[3]);
		
		return new EncodedTime(time);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Days : " + getDays());
		sb.append(" Hours : " + getHours());
		sb.append(" Minutes : " + getMinutes());
		sb.append(" Seconds : " + getSeconds() + "\n");
		return sb.toString();
	}
	
	public long getDays() {
		return days;
	}

	public void setDays(long days) {
		this.days = days;
		encode();
	}
	
	public long getHours() {
		return hours;
	}

	public void setHours(long hours) {
		this.hours = hours;
		encode();
	}

	public long getMinutes() {
		return minutes;
	}

	public void setMinutes(long minutes) {
		this.minutes = minutes;
		encode();
	}

	public long getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
		encode();
	}
	
}	

