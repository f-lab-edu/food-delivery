package com.delfood.utils;

public class RedisKeyFactory {
  private static String KEY_CART = "cart";
  
  
  // 인스턴스화 방지
  private RedisKeyFactory() {}
  
  private static String generateKey(String id, String key) {
    return id + ":" + key;
  }
  
  public static String generateCartKey(String memberId) {
    return generateKey(memberId, "cart");
  }
}
