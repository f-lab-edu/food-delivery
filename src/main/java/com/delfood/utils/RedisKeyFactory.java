package com.delfood.utils;

public class RedisKeyFactory {
  public enum Key {
    CART
  }
  
  // 인스턴스화 방지
  private RedisKeyFactory() {}
  
  private static String generateKey(String id, Key key) {
    return id + ":" + key;
  }
  
  public static String generateCartKey(String memberId) {
    return generateKey(memberId, Key.CART);
  }
}
