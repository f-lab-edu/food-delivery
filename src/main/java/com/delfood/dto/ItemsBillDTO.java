package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

// 사용자에게 전달하는 최종 주문서 DTO
@Getter
@NoArgsConstructor
public class ItemsBillDTO {
  @NonNull
  private List<MenuInfo> menus;
  
  private long itemsPrice;
  
  private long discountPrice;
  
  private long totalPrice;
  @NonNull
  private String memberId;
  @NonNull
  private AddressDTO addressInfo;
  @NonNull
  private ShopInfo shopInfo;
  
  private DeliveryInfo deliveryInfo;
  
  private CouponInfo couponInfo;
  
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
      long deliveryPrice,
      long itemsPrice,
      List<MenuInfo> menus,
      CouponInfo couponInfo) {
    this.memberId = memberId;
    this.addressInfo = addressInfo;
    this.shopInfo = shopInfo;
    this.deliveryInfo = DeliveryInfo.builder()
                                    .deliveryPrice(deliveryPrice)
                                    .build();
    this.menus = menus;
    this.couponInfo = couponInfo;
    this.totalPrice = totalPrice();
  }
  
  @Getter
  @NoArgsConstructor
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
    @NoArgsConstructor
    public static class OptionInfo {
      private long id;
      @NonNull
      private String name;
      private long price;
      
      /**
       * 직접 생성할 경우 Builder를 통해서 생성하게 한다.
       * @param id 옵션 아이디
       * @param name 옵션 이름
       * @param price 옵션 가격
       */
      @Builder
      public OptionInfo(long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
      }
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
    private long deliveryPrice;
  }
  
  @Getter
  @NoArgsConstructor
  public static class CouponInfo {
    private long couponIssueId;
    private long couponId;
    private String memberId;
    private String name;
    private CouponDTO.DiscountType discountType;
    private long discountValue;
    private LocalDateTime createdAt;
    private LocalDateTime endAt;
    
    /**
     * 직접 쿠폰 정보를 생설할 경우 사용하는 빌더.
     * @param couponIssueId 발행 쿠폰 아이디
     * @param couponId 쿠폰 아이디
     * @param memberId 회원 아이디
     * @param name 쿠폰 이름
     * @param discountType 할인 타입
     * @param discountValue 할인 가격
     * @param createAt 발행일
     * @param endAt 만료일
     */
    @Builder
    public CouponInfo(long couponIssueId, long couponId, String memberId, String name,
        CouponDTO.DiscountType discountType, long discountValue, LocalDateTime createAt, LocalDateTime endAt) {
      this.couponIssueId = couponIssueId;
      this.couponId = couponId;
      this.memberId = memberId;
      this.name = name;
      this.discountType = discountType;
      this.discountValue = discountValue;
      this.createdAt = createAt;
      this.endAt = endAt;
    }
  }
  
  /**
   * 메뉴 가격, 옵션 가격, 배달 가격, 할인 가격을 합친 총 가격을 계산한다.
   * @author jun
   * @return
   */
  public long totalPrice() {
    long itemsPrice = menus.stream().mapToLong(menu -> menu.getPrice()
        + menu.getOptions().stream().mapToLong(option -> option.getPrice()).sum()).sum()
        + deliveryInfo.getDeliveryPrice();
    long couponDiscountPrice;
    
    if (couponInfo != null) { // 사용하는 쿠폰이 있을 경우
      if (couponInfo.getDiscountType() == CouponDTO.DiscountType.PERCENT) {
        // 이렇게하면 소수점 미만이 버림된다.
        couponDiscountPrice = itemsPrice * couponInfo.getDiscountValue() / 100L; 
      } else {
        couponDiscountPrice = couponInfo.getDiscountValue();
      }
    } else { // 쿠폰을 사용하지 않을 경우
      couponDiscountPrice = 0;
    }
    
    this.itemsPrice = itemsPrice;
    this.discountPrice = couponDiscountPrice;
    this.totalPrice = itemsPrice - couponDiscountPrice;
    
    return totalPrice;
  }

}
