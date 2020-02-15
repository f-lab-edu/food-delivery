package com.delfood.dto.order.item;

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
  
  private List<OrdersItemOptionDTO> ordersItemOptions;
  
  public boolean hasNullDataBeforeInsertCart() {
    return menuId == null
        || count == null
        || count <= 0;
  }
}
