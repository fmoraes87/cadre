package com.cadre.server.core.util;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtils {

	public static String getHash(String password) {
		//Change line below
		return DigestUtils.md5Hex(password);
		
		//String hash = SCryptUtil.scrypt(password, 32768, 8, 1);
		//return hash;
		/*<dependency>
			<groupId>com.lambdaworks</groupId>
			<artifactId>scrypt</artifactId>
			<version>1.4.0</version>
		</dependency>*/
	}
	
	public static String getRandomPass(){  
		char[] chart ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};  
		  
		char[] senha= new char[8];  
		  
		int chartLenght = chart.length;  
		Random rdm = new Random();  
		  
		for (int x=0; x<8; x++)  
		senha[x] = chart[rdm.nextInt(chartLenght)];  
		  
		return new String(senha);  
	}  
}
