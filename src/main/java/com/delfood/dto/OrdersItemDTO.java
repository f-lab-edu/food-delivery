package com.delfood.dto;

import java.util.List;
import org.codehaus.commons.nullanalysis.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersItemDTO {
  private Long id;
  @NotNull
  private Long menuId;
  private Long orderId;
  @NotNull
  private Long count;
  
  // 매장 아이디를 캐싱하기 위한 컬럼
  private Long shopId;
  
  private List<OrdersItemOptionDTO> options;
  
  public boolean hasNullDataBeforeInsertCart() {
    return menuId == null
        || count == null
        || count <= 0
        || shopId == null;
  }
}
