package com.delfood.dto.rider;

import com.delfood.dto.OrderBillDTO.SimpleAddressInfo;
import com.delfood.dto.SimpleShopInfo;
import java.time.LocalDateTime;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class DeliveryInfoDTO {
  @NonNull
  private String riderId;
  
  @NonNull
  private SimpleAddressInfo addressInfo;
  
  @NonNull
  private SimpleShopInfo shopInfo;
  
  @NonNull
  private DeliveryOrderInfo orderInfo;
  
  @Nullable
  private LocalDateTime arrivalTime;
  
  
  @Builder
  public DeliveryInfoDTO(@NonNull String riderId, @NonNull SimpleAddressInfo addressInfo,
      @NonNull SimpleShopInfo shopInfo, LocalDateTime arrivalTime,
      @NonNull DeliveryOrderInfo orderInfo) {
    this.riderId = riderId;
    this.addressInfo = addressInfo;
    this.shopInfo = shopInfo;
    this.arrivalTime = arrivalTime;
    this.orderInfo = orderInfo;
  }
}
