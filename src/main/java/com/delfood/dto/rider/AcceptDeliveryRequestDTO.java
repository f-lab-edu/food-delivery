package com.delfood.dto.rider;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class AcceptDeliveryRequestDTO {

  @NonNull
  private Long orderId;
  
  @NonNull
  private String riderId;
  
  @NonNull
  private RequestResult result;
  
  private LocalDateTime startedAt;
  
  
  @Builder
  public AcceptDeliveryRequestDTO(Long orderId, String riderId) {
    this.orderId = orderId;
    this.riderId = riderId;
    startedAt = LocalDateTime.now();
  }
  
  public static enum RequestResult { 
    SUCCESS, FAIL
  }
}
