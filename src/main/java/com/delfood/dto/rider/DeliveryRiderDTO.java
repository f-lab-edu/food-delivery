package com.delfood.dto.rider;

import java.time.LocalDateTime;
import com.delfood.dto.address.Position;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class DeliveryRiderDTO {
  @NonNull
  private String riderId;
  
  @NonNull
  private Position position;
  
  @NonNull
  private LocalDateTime updatedAt = LocalDateTime.now();
  
  @Builder
  public DeliveryRiderDTO(String riderId, Position position, LocalDateTime updatedAt) {
    this.riderId = riderId;
    this.position = position;
    this.updatedAt = LocalDateTime.now();
  }
}
