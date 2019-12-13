package com.delfood.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

// 사용자에게 전달하는 최종 주문서 DTO
@Getter
public class ItemsBillDTO {
  @NonNull
  private List<MenuInfo> menus;
  
  private long totalPrice;
  @NonNull
  private String memberId;
  @NonNull
  private AddressDTO addressInfo;
  @NonNull
  private ShopInfo shopInfo;
  
  private DeliveryInfo deliveryInfo;
  
  /**
   * 해당 인자를 세팅하여 새로운 객체를 반환한다.
   * 리스트인 'menus'에는 ArrayList를 할당한다.
   * @param memberId 고객 아이디
   * @param addressInfo 고객 주소정보
   */
  @Builder
  public ItemsBillDTO(@NonNull String memberId,
      @NonNull AddressDTO addressInfo,
      @NonNull ShopInfo shopInfo,
      double distanceMeter,
      long deliveryPrice) {
    this.memberId = memberId;
    this.addressInfo = addressInfo;
    this.shopInfo = shopInfo;
    this.deliveryInfo = DeliveryInfo.builder()
                                    .distanceMeter(distanceMeter)
                                    .deliveryPrice(deliveryPrice)
                                    .build();
    
    menus = new ArrayList<ItemsBillDTO.MenuInfo>();
  }
  
  @Getter
  public static class MenuInfo {
    private long id;
    private String name;
    private long price;
    private List<OptionInfo> options;
    
    /**
     * 메뉴 정보의 간략한 정보를 저장하는 DTO를 생성한다.
     * @param id 메뉴 아이디
     * @param name 메뉴의 이름
     * @param price 메뉴 가격
     */
    @Builder
    public MenuInfo(long id, @NonNull String name, long price) {
      this.id = id;
      this.name = name;
      this.price = price;
      options = new ArrayList<ItemsBillDTO.MenuInfo.OptionInfo>();
    }

    @Getter
    @Builder
    public static class OptionInfo {
      private long id;
      @NonNull
      private String name;
      private long price;
    }
  }
  
  @Getter
  @Builder
  public static class ShopInfo {
    
    private long id;
    
    @NonNull
    private String name;
    
    private String addressCode;
  }
  
  @Builder
  @Getter
  public static class DeliveryInfo {
    private double distanceMeter;
    private long deliveryPrice;
  }

}
