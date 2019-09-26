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
	/*
	 * NoSuchAlgorithmException : 잘못된 알고리즘을 입력하여 키를 생성할 경우 발생할 수 있다.
	 * 		개발시 키 생성을 정상적으로 할 수 있다면 발생하지 않는 Exception이므로 Runtime Exception으로 사용한다. 
	 */
	public static String encryptSHA256(String str) throws NoSuchAlgorithmException {
		String SHA = null;
			
			MessageDigest sh = MessageDigest.getInstance(ENCRYPTION_TYPE);
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		return SHA;
	}
}