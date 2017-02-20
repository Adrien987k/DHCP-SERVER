package com.dhcp.lease;

import java.net.InetAddress;
import java.util.ArrayList;

public class Lease {
	private InetAddress ipAddress;
	private InetAddress currentHardwareAddress;
	private ArrayList<InetAddress> knownHardwareAddresses = new ArrayList<>();
	
	private long duration;
	private boolean isAvailable = true;
	
	public Lease(InetAddress ipAddress, InetAddress hardwareAddress, int duration) {
		this.ipAddress = ipAddress;
		this.currentHardwareAddress = hardwareAddress;
		this.knownHardwareAddresses.add(hardwareAddress);
		this.duration = duration;
		setUnavailable();
	}
	
	public long getDuration() {
		return duration;
	}
	private void setDuration(long duration) {
		this.duration = duration;
	}
	
	public long getT1() {
		return duration/2;
	}
	public long getT2() {
		return ( (long) ( (float) duration *0.875 ) );
	}
	
	public InetAddress getIPAddress() {
		return ipAddress;
	}
	
	public InetAddress getHardwareAddress() {
		return currentHardwareAddress;
	}
	private void setHardwareAddress(InetAddress hardwareAddress) {
		this.currentHardwareAddress = hardwareAddress;
		if(!hardwareAddressIsKnown(hardwareAddress))
			knownHardwareAddresses.add(hardwareAddress);
	}
	
	public boolean isAvailable() {
		return isAvailable;
	}
	private void setIsAvalaible(boolean bool) {
		this.isAvailable = bool;
	}
	private void setAvailable() {
		setIsAvalaible(true);
	}
	private void setUnavailable() {
		setIsAvalaible(false);
	}
	
	public void release(boolean forget) {
		if(forget)
			knownHardwareAddresses.remove(currentHardwareAddress);
		this.currentHardwareAddress = null;
		setAvailable();
	}
	
	public void bind(InetAddress hardwareAddress) {
		setHardwareAddress(hardwareAddress);
		setUnavailable();
	}
	
	public void bind(InetAddress hardwareAddress, long duration) {
		setHardwareAddress(hardwareAddress);
		setDuration(duration);
		setUnavailable();
	}
	
	public boolean hardwareAddressIsKnown(InetAddress hardwareAddress) {
		return knownHardwareAddresses.contains(hardwareAddress);
	}
}
