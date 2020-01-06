package com.delfood.error.exception.coupon;

public class IssuedCouponExistException extends RuntimeException{
  public IssuedCouponExistException(String msg) {
    super(msg);
  }
}
