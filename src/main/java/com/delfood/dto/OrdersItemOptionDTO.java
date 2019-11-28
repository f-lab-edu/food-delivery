package com.delfood.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersItemOptionDTO {
  private Long id;
  private Long optionId;
  private Long ordersItemId;
}
