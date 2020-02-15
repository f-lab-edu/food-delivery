package com.delfood.dto.shop;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Shop을 Update하기 위한 전용 DTO
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
  
  /**
   * id값을 세팅한 ShopUpdateDTO를 반환한다.
   * 반환되는 객체는 파라미터로 들어온 객체를 복사한 객체이다.
   * @author jun
   * @param updateDTO 복사할 데이터
   * @param id 세팅할 id
   * @return
   */
  public static ShopUpdateDTO copyWithId(ShopUpdateDTO updateDTO, Long id) {
    ShopUpdateDTO result = new ShopUpdateDTO();
    result.setId(id);
    result.setTel(updateDTO.getTel());
    result.setDeliveryLocation(updateDTO.getDeliveryLocation());
    result.setOperatingTime(updateDTO.getOperatingTime());
    result.setInfo(updateDTO.getInfo());
    result.setOriginInfo(updateDTO.getOriginInfo());
    result.setNotice(updateDTO.getNotice());
    result.setMinOrderPrice(updateDTO.getMinOrderPrice());
    result.setOrderType(updateDTO.getOrderType());
    result.setDeliveryType(updateDTO.getDeliveryType());

    return result;
  }
}
