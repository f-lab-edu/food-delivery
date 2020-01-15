package com.delfood.dto.rider;

import java.time.LocalDateTime;
import com.delfood.dto.address.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class DeliveryRiderDTO {
  private String id;
  
  private Position position;
  
  private LocalDateTime updatedAt;
  
  public DeliveryRiderDTO() {
    this.updatedAt = LocalDateTime.now();
  }
}
