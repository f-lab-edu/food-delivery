package com.delfood.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"optionId", "ordersItemId"})
public class OrdersItemOptionDTO {
  private Long id;
  private Long optionId;
  private Long ordersItemId;
  
  // 캐싱용 컬럼
  private Long price;
}
