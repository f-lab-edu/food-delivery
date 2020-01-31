package com.delfood.dto.address;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeliveryLocationDTO {
  private Long id;
  private Long shopId;
  private String townCode;
  private LocalDateTime createdAt;
  private AddressDTO addressInfo;
}
