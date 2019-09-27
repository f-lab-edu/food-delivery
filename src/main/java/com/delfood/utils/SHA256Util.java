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
	 * NoSuchAlgorithmException : 잘못된 알고리즘을 입력하여 키를 생성할 경우 발생할 수 있다. 개발시 키 생성을 정상적으로
	 * 할 수 있다면 발생하지 않는 Exception이므로 Runtime Exception으로 사용한다.
	 * 
	 * CheckedException,  UnCheckedException
	 * CheckedException - 프로그램 실행 흐름상 발생 가능성이 있는 오류를 표현한다.
	 * 					프로그래머가 작성한 코드 보다는 실행 상황에 따라 발생 가능성이 있는 예외일 때 사용한다.
	 * 					컴파일시 에러가 발생한다. 코드 작성시 에러 처리 로직을 작성해야 컴파일이 가능하다.
	 * 					Ex) SQLException, IOException
	 * 
	 * UnCheckedException(RuntimeException) - 코드상으로 오류가 있을 때 발생한다.
	 * 						프로그래머가 작성한 로직상 오류가 있거나 실수가 있을 때 발생한다.
	 * 						컴파일시에는 에러가 발생하지 않지만 실행도중 예외가 발생할 수 있다.
	 * 						Ex) IndexOutOfBoundException, NullPointerException
	 */
	public static String encryptSHA256(String str) {
		String SHA = null;

		MessageDigest sh;
		try {
			sh = MessageDigest.getInstance(ENCRYPTION_TYPE);
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("암호화 에러! SHA256Util 확인 필요 ", e);
		}
		return SHA;
	}
}