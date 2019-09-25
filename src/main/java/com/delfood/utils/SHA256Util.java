package com.delfood.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.Level;

import lombok.extern.log4j.Log4j2;

// 암호화 방식 - SHA-256
/*
 * StringBuffer와 StringBuilder
 * - 동기화가 필요한 경우(멀티 쓰레드 환경) StringBuffer을 사용
 * - StringBuilder은 동기화를 보장하지 않지만 성능적으로 우수함
 */
@Log4j2
public class SHA256Util {
	public static final String ENCRYPTION_TYPE = "SHA-256";
	public static String encryptSHA256(String str) {
		String SHA = null;
		try {	
			MessageDigest sh = MessageDigest.getInstance(ENCRYPTION_TYPE);
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.ERROR, "SHA256Util Error! Please check SHA256Util");
		}
		return SHA;
	}
}