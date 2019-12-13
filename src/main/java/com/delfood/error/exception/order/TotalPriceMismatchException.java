package com.delfood.error.exception.order;

public class TotalPriceMismatchException extends RuntimeException{
  public TotalPriceMismatchException(String msg) {
    super(msg);
  }
}
