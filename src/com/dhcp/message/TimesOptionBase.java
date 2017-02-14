package com.dhcp.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.dhcp.util.BufferUtils;
import com.dhcp.util.EncodedTime;

public class TimesOptionBase extends DhcpOption {
	
	private List<EncodedTime> times = new ArrayList<>();
	
	public TimesOptionBase(short code){
		super(code);
	}

	@Override
	public byte[] getContent() throws InvalidDhcpMessageException {
		if(!contentIsValid()) 
			DhcpMessage.invalidDhcpMessage("try to send dhcp message with invalid : " + name);
		
		ByteBuffer buffer = ByteBuffer.allocate(2 + (times.size() * 4));
		buffer.put((byte) code);
		buffer.put((byte) length);
		
		for(EncodedTime time : times){
			buffer.put(time.getEncodedTime());
		}
		
		return buffer.array();
	}

	@Override
	public void parseDhcpOption(ByteBuffer buffer) throws InvalidDhcpMessageException {
		length = BufferUtils.byteToShort(buffer.get());
		if(length % 4 != 0)
			DhcpMessage.invalidDhcpMessage("dhcp message received with address option of invalid length");
		
		int numberTimes = length / 4;
		byte[] timesBuffer = new byte[4];
		for(int i = 0; i < numberTimes; i++){
			buffer.get(timesBuffer);
			times.add(EncodedTime.parseEncodedTime(timesBuffer));
		}
	}
	
	public void addEncodedTime(EncodedTime encodedTime){
		times.add(encodedTime);
		length += 4;
	}
	
	@Override
	public boolean contentIsValid(){
		return super.contentIsValid() && (length % 4 == 0);
	}
	
}
