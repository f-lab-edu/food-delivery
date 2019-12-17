package com.delfood.utils;

public class OrderUtil {
  
  private OrderUtil() {}

  public static String generateOrderItemKey(String memberId, long idx) {
    return memberId + ":" + idx + ":" + System.currentTimeMillis();
  }
  
  public static String generateOrderItemOptionKey(String memberId, long itemIdx, long optionIdx) {
    return memberId + ":" + itemIdx + ":" + optionIdx + ":" + System.currentTimeMillis();
  }
}
