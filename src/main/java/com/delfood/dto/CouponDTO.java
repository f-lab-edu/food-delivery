package com.delfood.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;

@ToString
@Getter
@Setter
@Alias("coupon")
public class CouponDTO {
  
  @JsonFormat(shape = Shape.OBJECT)
  public enum DiscountType {
    WON, PERCENT
  }
  
  public enum Status {
    DEFAULT, DELETED
  }

  private Long id;
  
  private String name;
  
  private DiscountType discountType;
  
  private Long discountValue;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endAt;
  
  private Status status;
  
  /**
   * 쿠폰 등록에 필요한 내용이 null인지 확인.
   * @param couponInfo 쿠폰정보
   * @return
   * 
   * @author jinyoung
   */
  public static boolean hasNullDataBeforeAdd(CouponDTO couponInfo) {
    if (couponInfo.getName() == null || couponInfo.getDiscountType() == null
        || couponInfo.getDiscountValue() == null || couponInfo.getEndAt() == null) {
      return true;
    }
    return false;
  }
}
