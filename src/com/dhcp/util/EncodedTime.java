package com.dhcp.util;

public class EncodedTime {
	
	private static long[] masks = new long[]{ 61440  , // 1111 0000 0000 0000
											  3840   , // 0000 1111 0000 0000
											  240    , // 0000 0000 1111 0000
											  15     , // 0000 0000 0000 1111 
										    };
	
	private long time;
	private short[] encodedTime = new short[4];
	
	private long hours; 
	private long minutes;
	private long seconds;
	
	public EncodedTime(long hours, long minutes, long seconds){
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		
		encode();
	}
	
	public EncodedTime(long seconds){
		this(0, 0, seconds);
	}
	
	private void encode(){
		time += (hours * 3600);
		time += (minutes * 60);
		time += seconds;
		
		for(int i = 0; i < 4; i++){
			encodedTime[i] = (short) ((short) (time & masks[i]) >> (3 - i) * 4);
		}
	}
	
	public byte[] getEncodedTime(){
		return new byte[]{ (byte) encodedTime[0],
						   (byte) encodedTime[1],
						   (byte) encodedTime[2],
						   (byte) encodedTime[3] };
	}
	
	public static EncodedTime parseEncodedTime(byte[] buffer){
		long time = 0;
		time += buffer[0] >> 12;
		time += buffer[1] >> 8;
		time += buffer[2] >> 4;
		time += buffer[3];
		
		return new EncodedTime(time);
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

