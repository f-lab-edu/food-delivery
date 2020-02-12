package com.delfood.error.exception.order;

public class TotalPriceMismatchException extends IllegalArgumentException {
  public TotalPriceMismatchException(String msg) {
    super(msg);
  }
}
