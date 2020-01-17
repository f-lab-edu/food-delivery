package com.delfood.dto.rider;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
public class AcceptDeliveryRequestDTO {

  @NonNull
  private Long orderId;
  
  @NonNull
  private String riderId;
  
  @NonNull
  private RequestResult result;
  
  private LocalDateTime startedAt;
  
  
  @Builder
  public AcceptDeliveryRequestDTO(Long orderId, String riderId, RequestResult result) {
    this.orderId = orderId;
    this.riderId = riderId;
    this.result = result;
    startedAt = LocalDateTime.now();
  }
  
  public static enum RequestResult { 
    SUCCESS, FAIL
  }
}
