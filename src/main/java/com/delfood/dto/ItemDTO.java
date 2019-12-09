package com.delfood.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"menuInfo", "options", "shopInfo"})
public class ItemDTO {
  private CacheMenuDTO menuInfo; // id, name, price
  private List<CacheOptionDTO> options; // id, name, price
  private long count;
  private long price;
  private CacheShopDTO shopInfo; // id, name
  
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
  
  @Getter
  @EqualsAndHashCode
  public static class CacheMenuDTO {
    private Long id;
    private String name;
    private Long price;
  }
  
  @Getter
  @EqualsAndHashCode
  public static class CacheShopDTO {
    private Long id;
    private String name;
  }
  
  @Getter
  @EqualsAndHashCode
  public static class CacheOptionDTO {
    private Long id;
    private String name;
    private Long price;
  }
}
