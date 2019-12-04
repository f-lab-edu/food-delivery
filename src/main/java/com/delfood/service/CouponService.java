package com.delfood.service;

import com.delfood.dto.CouponDTO;
import com.delfood.dto.CouponDTO.DiscountType;
import com.delfood.mapper.CouponMapper;

import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class CouponService {

  @Autowired
  private CouponMapper couponMapper;
  
  /**
   * 쿠폰 추가.
   * @param couponInfo
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addCoupon(CouponDTO couponInfo) {
    
    verifyDiscountData(couponInfo.getDiscountType(), couponInfo.getDiscountValue());
    
    LocalDateTime createdAt = couponMapper.insertCoupon(couponInfo);
    
    if (couponInfo.getEndAt().isBefore(createdAt)) {
      log.error("coupon expiration date is ealire than creation date! "
          + "EndAt : {}, startAt : {}",couponInfo.getEndAt(), createdAt);
      throw new RuntimeException("coupon expiration date is ealire than creation date!");
    }
  }
  
  /**
   * 쿠폰 할인 값 검증.
   * @param discountType 할인 타입
   * @param discountValue 할인 값
   * @throws IllegalArgumentException
   * 
   * @author jinyoung
   */
  public static void verifyDiscountData(DiscountType discountType, Long discountValue) {
    if (DiscountType.PERCENT == discountType 
        && ((discountValue < 0 || discountValue > 100))) {
      log.error("coupon discount setting error! couponType : {} , discountValue : {}",
          discountType, discountValue);
      throw new IllegalArgumentException("coupon discount setting error!");
    }
  }
}
