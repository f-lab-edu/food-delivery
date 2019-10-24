package com.delfood.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShopUpdateDTO {
  @NonNull
  private Long id;
  private String tel;
  private String deliveryLocation;
  private String operatingTime;
  private String info;
  private String originInfo;
  private String notice;
  private Long minOrderPrice;
  private ShopDTO.OrderType orderType;
  private ShopDTO.DeliveryType deliveryType;
}
