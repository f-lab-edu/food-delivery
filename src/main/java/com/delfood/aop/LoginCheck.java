package com.delfood.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로그인의 상태를 확인한다. 
 * 회원, 사장님, 라이더의 로그인 상태를 확인하여 로그인 되지 않았다면 예외를 발생시킨다.
 * @author jun
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginCheck {
  
  /**
   * 로그인을 체크하고 싶은 유저의 로그인 타입.
   * 회원(MEMBER), 사장님(OWNER), 라이더(RIDER)중 선택할 수 있다.
   * @return
   */
  UserType type();
  
  public static enum UserType {
    MEMBER, OWNER, RIDER
  }
}
