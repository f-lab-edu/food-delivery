package com.delfood.dto.rider;

import java.time.LocalDateTime;
import com.delfood.dto.address.Position;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class DeliveryRiderDTO {
  private String riderId;
  
  private Position position;
  
  private LocalDateTime updatedAt;
  
  @Builder
  public DeliveryRiderDTO(String riderId, Position position, LocalDateTime updatedAt) {
    this.riderId = riderId;
    this.position = position;
    this.updatedAt = LocalDateTime.now();
  }
}
