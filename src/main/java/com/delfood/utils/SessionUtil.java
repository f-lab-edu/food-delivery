package com.delfood.utils;

import javax.servlet.http.HttpSession;
import lombok.NonNull;

public class SessionUtil {

  private static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";
  private static final String LOGIN_OWNER_ID = "LOGIN_OWNER_ID";
  private static final String LOGIN_RIDER_ID = "LOGIN_RIDER_ID";
  
  // 인스턴스화 방지
  private SessionUtil() {}

  /**
   * 로그인한 고객의 아이디를 세션에서 꺼낸다.
   * @author jun
   * @param session 사용자의 세션
   * @return 로그인한 고객의 id 또는 null
   */
  public static String getLoginMemberId(HttpSession session) {
    return (String) session.getAttribute(LOGIN_MEMBER_ID);
  }
  
  /**
   * 로그인 한 고객의 id를 세션에 저장한다.
   * @author jun
   * @param session 사용자의 session
   * @param id 로그인한 고객의 id
   */
  public static void setLoginMemberId(HttpSession session, String id) {
    session.setAttribute(LOGIN_MEMBER_ID, id);
  }
  

  

  /**
   * 로그인한 사장님 id를 세션에서 꺼낸다.
   * 로그인 하지 않았다면 null이 반환된다
   * @author jun
   * @param session 사용자의 세션
   * @return 로그인한 사장님 id 또는 null
   */
  public static String getLoginOwnerId(HttpSession session) {
    return (String) session.getAttribute(LOGIN_OWNER_ID);
  }
  
  /**
   * 로그인한 사장님의 id를 세션에 저장한다.
   * @author jun
   * @param session 사용자의 세션
   * @param id 로그인한 사장님 id
   */
  public static void setLoginOwnerId(HttpSession session, String id) {
    session.setAttribute(LOGIN_OWNER_ID, id);
  }
  
  
  /**
   * 해당 세션의 정보를 모두 삭제한다.
   * @author jun
   * @param session 사용자의 세션
   */
  public static void clear(HttpSession session) {
    session.invalidate();
  }
  
  /**
   * 고객 로그인 정보를 삭제한다.
   * @author jun
   * @param session 사용자의 세션
   */
  public static void logoutMember(HttpSession session) {
    session.removeAttribute(LOGIN_MEMBER_ID);
  }
  
  /**
   * 사장님 로그인 정보를 삭제한다.
   * @author jun
   * @param session 사용자의 세션
   */
  public static void logoutOwner(HttpSession session) {
    session.removeAttribute(LOGIN_OWNER_ID);
  }

  /**
   * 로그인한 라이더의 id를 세션에 저장한다.
   * @author jun
   * @param session 사용자의 세션
   * @param id 저장할 라이더 아이디
   */
  public static void setLoginRiderId(HttpSession session, @NonNull String id) {
    session.setAttribute(LOGIN_RIDER_ID, id);
  }
  
  public static String getLoginRiderId(HttpSession session) {
    return (String) session.getAttribute(LOGIN_RIDER_ID);
  }
  
  public static void logoutRider(HttpSession session) {
    session.removeAttribute(LOGIN_RIDER_ID);
  }
  
  
  
}
