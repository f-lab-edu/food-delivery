package com.delfood.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>매장 id를 파라미터로 주어야 한다.</b>
 * 접속한 사장님이 해당 매장의 주인인지 확인한다.
 * @author yyy99
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwnerShopCheck {
  /**
   * 해당 변수의 이름.
   * @return
   */
  String value();
}
