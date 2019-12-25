package com.delfood.utils;

public class RedisKeyFactory {
  public enum Key {
    CART, FCM_MEMBER, FCM_OWNER 
  }
  
  // 인스턴스화 방지
  private RedisKeyFactory() {}
  
  private static String generateKey(String id, Key key) {
    return id + ":" + key;
  }
  
  public static String generateCartKey(String memberId) {
    return generateKey(memberId, Key.CART);
  }
  
  public static String generateFcmMemberKey(String memberId) {
    return generateKey(memberId, Key.FCM_MEMBER);
  }
  
  public static String generateFcmOwnerKey(String ownerId) {
    return generateKey(ownerId, Key.FCM_OWNER);
  }
  
  /**
   * 생성된 키로부터 아이디를 추출한다.
   * @author jun 
   * @param key redis Key
   * @return
   */
  public static String getIdFromKey(String key) {
    return key.substring(0, key.indexOf(":"));
  }
}
