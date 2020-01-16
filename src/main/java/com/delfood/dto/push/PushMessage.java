package com.delfood.dto.push;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.joda.time.LocalDateTime;

@Getter
public class PushMessage {
  @NonNull
  private String title;
  @NonNull
  private String message;
  
  public static final PushMessage ADD_ORDER_REQUEST = new PushMessage("DelFood 주문", "새로운 주문이 들어왔습니다");
  public static final PushMessage ACCEPT_ORDER_REQUEST = new PushMessage("DelFood 접수", "주문이 접수되었습니다");
  public static final PushMessage REQUIRED_ORDER_REQUEST = new PushMessage("DelFood 주문취소", "매장에서 주문을 취소하였습니다");
  public static final PushMessage DELIVERY_MATCH = new PushMessage("DelFood 배달원 매칭", "배달원이 매칭되었습니다");
  public static final PushMessage DELIVERY_START = new PushMessage("DelFood 배달 시작", "음식 배달이 시작되었습니다");
  public static final PushMessage DELIVERY_SUCCESS = new PushMessage("DelFood 배달 완료", "배달이 완료되었습니다");
  
  
  private LocalDateTime generatedTime;
  
  public PushMessage(String title, String message) {
    this.title = title;
    this.message = message;
    this.generatedTime = LocalDateTime.now();
  }
  
  
  
}
