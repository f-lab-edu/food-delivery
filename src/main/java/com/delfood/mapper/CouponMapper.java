package com.delfood.mapper;

import com.delfood.dto.CouponDTO;

import java.time.LocalDateTime;

public interface CouponMapper {
  
  public LocalDateTime insertCoupon(CouponDTO couponInfo);

}
