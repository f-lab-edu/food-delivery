package com.delfood.error.exception.cart;

public class DuplicateItemException extends RuntimeException{
  public DuplicateItemException(String msg) {
    super(msg);
  }
}
