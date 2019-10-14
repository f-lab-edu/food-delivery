package com.delfood.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.Level;
import lombok.extern.log4j.Log4j2;

// 암호화 방식 - SHA-256
/*
 * StringBuffer와 StringBuilder - 동기화가 필요한 경우(멀티 쓰레드 환경) StringBuffer을 사용 - StringBuilder은 동기화를 보장하지
 * 않지만 성능적으로 우수함
 * 
 * 동기화를 지원하는 StringBuffer의 경우 thread-safe를 보장하기 위해 block, unblock 처리를 진행한다.
 * 그렇기 때문에 동일한 연산에 동기화 처리를 한 연산과 동기화 처리를 하지 않은 연산은 약 9배 이상의 성능 차이가 발생할 수 있다. - 자바 성능튜닝 이야기
 */
@Log4j2
public class SHA256Util {
  public static final String ENCRYPTION_TYPE = "SHA-256";

  /**
   * Checked Exception과 Unchecked Exception
   * Checked Exception은 개발하는 프로그래머가 인지하고 있어야 하는 예외일 때 사용한다. 
   * 개발자가 반드시 이 예외 처리 로직을 작성해야 하며 이러한 예외가 발생할 수 있다는 정보를 알려준다.
   * 
   * Unchecked Exception은 일반적으로 프로그래머의 실수에 의해 발생할 수 있다.
   * 업무의 흐름 보다는 프로그래머가 작성한 로직이 잘못되었 을 때, 기본적인 내용에 문제가 있을 때(0으로 나누는 경우) 발생한다.
   * 이 경우 기본적인 코드가 잘못된 경우가 많기 때문에 예외처리 보다는 이 예외가 아예 발생하지 않도록 처리하는 것이 올바른 처리 방법이다.
   * 
   * NoSuchAlgorithmException : 잘못된 알고리즘을 입력하여 키를 생성할 경우 발생할 수 있다. 개발시 키 생성을 정상적으로 할 수 있다면 발생하지 않는
   * Exception이므로 Runtime Exception으로 사용한다.
   * 
   * CheckedException, UnCheckedException CheckedException - 프로그램 실행 흐름상 발생 가능성이 있는 오류를 표현한다. 프로그래머가
   * 작성한 코드 보다는 실행 상황에 따라 발생 가능성이 있는 예외일 때 사용한다. 컴파일시 에러가 발생한다. 코드 작성시 에러 처리 로직을 작성해야 컴파일이 가능하다. Ex)
   * SQLException, IOException
   * 
   * UnCheckedException(RuntimeException) - 코드상으로 오류가 있을 때 발생한다. 프로그래머가 작성한 로직상 오류가 있거나 실수가 있을 때
   * 발생한다. 컴파일시에는 에러가 발생하지 않지만 실행도중 예외가 발생할 수 있다. Ex) IndexOutOfBoundException, NullPointerException
   */
  public static String encryptSHA256(String str) {
    String SHA = null;

    MessageDigest sh;
    try {
      sh = MessageDigest.getInstance(ENCRYPTION_TYPE);
      sh.update(str.getBytes());
      byte[] byteData = sh.digest();
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
