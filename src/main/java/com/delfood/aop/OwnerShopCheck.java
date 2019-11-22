package com.delfood.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <b>매장 id가 첫 번째 파라미터로 와야한다.</b>
 * 접속한 사장님이 해당 매장의 주인인지 확인한다.
 * @author yyy99
 *
 */
@Target(ElementType.METHOD)
public @interface OwnerShopCheck {

}
