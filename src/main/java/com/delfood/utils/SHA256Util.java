package com.delfood.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 암호화 방식 - SHA-256
public class SHA256Util {
	public static String encryptSHA256(String str) {
		String SHA = null;
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return SHA;
	}
}