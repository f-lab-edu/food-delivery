package com.delfood.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.commons.nullanalysis.NotNull;

@Getter
@Setter
@EqualsAndHashCode(of = {"menuId", "count", "ordersItemOptions"})
public class OrdersItemDTO {
  private Long id;
  @NotNull
  private Long menuId;
  private Long orderId;
  @NotNull
  private Long count;
  
  // 캐싱용
  private Long shopId;
  
  // 캐싱용
  private Long price;
  
  // 캐싱용
  private String shopName;
  
  private List<OrdersItemOptionDTO> ordersItemOptions;
  
  public boolean hasNullDataBeforeInsertCart() {
    return menuId == null
        || count == null
        || count <= 0
        || shopId == null;
  }
}
