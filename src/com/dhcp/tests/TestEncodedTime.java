package com.dhcp.tests;

import com.dhcp.message.common.EncodedTime;

public class TestEncodedTime {
	
	public static void affiche(byte[] tab){
		for(int i = 0; i < 4; i++){
			System.out.print(tab[i] + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		
		EncodedTime encodedTime1 = new EncodedTime(0, 2, 50, 30);
		EncodedTime encodedTime2 = new EncodedTime(200000);
		
		System.out.println(encodedTime1);
		System.out.println(encodedTime2);
		
		byte[] result1 = encodedTime1.getBytes();
		byte[] result2 = encodedTime2.getBytes();
		affiche(result1);
		affiche(result2);
		
		EncodedTime resultTime1 = EncodedTime.parseEncodedTime(result1);
		EncodedTime resultTime2 = EncodedTime.parseEncodedTime(result2);
		
		System.out.println(resultTime1);
		System.out.println(resultTime2);
		
	}
	
}
