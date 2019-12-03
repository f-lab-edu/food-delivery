package com.delfood.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class OrderFormDTO {
  
  @NonNull
  private AddressDTO basicAddress;
  
  private String addressDetail;
  
  private Long totalPrice;
}
