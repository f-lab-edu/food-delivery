package com.delfood.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"menuInfo", "options", "shopInfo"})
public class ItemDTO {
  private MenuDTO menuInfo; // id, name, price
  private List<OptionDTO> options; // id, name, price
  private long count;
  private long price;
  private ShopDTO shopInfo; // id, name
  
  /**
   * 필요한 값을 모두 가지고 있는지 검사한다.
   * @author jun
   * @return
   */
  public boolean hasNullDataBeforeInsertCart() {
    return menuInfo.getId() == null
        || menuInfo.getName() == null
        || menuInfo.getPrice() == null 
        || options.stream().anyMatch(option -> option.getId() == null
            || option.getName() == null || option.getPrice() == null);
  }
}
