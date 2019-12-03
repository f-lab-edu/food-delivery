package com.delfood.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;

@Getter
@Setter
@ToString
@Alias("order")
public class OrderDTO {
  
  public enum OrderStatus {
    BEFORE_PAYMENT, ORDER_REQUEST, ORDER_APPROVAL, IN_DELIVERY, DELIVERY_COMPLETE
  }
  
  @NonNull
  private Long id;
  
  private LocalDateTime startTime;
  
  @NonNull
  private OrderStatus orderStatus;
  
  private LocalDateTime exArrivalTime;
  
  private LocalDateTime arrivalTime;
  
  private String riderId;

  @NonNull
  private String memberId;
  
  @NonNull
  private String addressCode;
  
  private String addressDetail;
  
  @NonNull
  private Long deliveryCost;
  
}
