package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;

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
  
  // 응답 데이터의 형식을 지정해준다.
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startTime;
  
  @NonNull
  private OrderStatus orderStatus;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime expectedArrivalTime;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime arrivalTime;
  
  private String riderId;

  @NonNull
  private String memberId;
  
  @NonNull
  private String addressCode;
  
  private String addressDetail;
  
  @NonNull
  private Long deliveryCost;
  
  List<OrderItemDTO> items;
}
