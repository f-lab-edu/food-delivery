package com.delfood.controller;

import com.delfood.dto.CouponDTO;
import com.delfood.service.CouponService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons/")
public class CouponController {
  
  @Autowired
  CouponService couponService;
  
  /**
   * 쿠폰을 추가한다.
   * @param couponInfo 쿠폰 정보
   * @return
   * 
   * @author jinyoung
   */
  @PostMapping
  public HttpStatus addCoupon(@RequestBody CouponDTO couponInfo) {
    
    if (CouponDTO.hasNullDataBeforeAdd(couponInfo)) {
      throw new NullPointerException(couponInfo.toString());
    }
    
    couponService.addCoupon(couponInfo);
    
    return HttpStatus.OK;
  }
}
