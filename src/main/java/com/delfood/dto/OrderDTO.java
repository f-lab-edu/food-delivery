package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;
import org.codehaus.commons.nullanalysis.Nullable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Alias("order")
@NoArgsConstructor // Mybatis에서 기본 생성자가 없으면 예외처리를 한다
public class OrderDTO {

  public enum OrderStatus {
    BEFORE_PAYMENT, ORDER_REQUEST, ORDER_APPROVAL, IN_DELIVERY, DELIVERY_COMPLETE
  }
  
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
  
  private Long deliveryCost;
  
  private Long shopId;
  
  // 조회할 때만 사용하는 컬럼.
  @Nullable
  private String shopName;
  
  private List<OrderItemDTO> items;
  
  @Nullable
  private SimpleCouponInfo couponInfo;
  
  @Builder
  public OrderDTO(String memberId, String addressCode, String addressDetail, Long shopId,
      long deliveryCost) {
    this.orderStatus = OrderStatus.BEFORE_PAYMENT;
    this.memberId = memberId;
    this.addressCode = addressCode;
    this.addressDetail = addressDetail;
    this.shopId = shopId;
    this.deliveryCost = deliveryCost;
  }
}
