package com.delfood.controller;

import com.delfood.dto.CouponDTO;
import com.delfood.service.CouponService;
import lombok.extern.log4j.Log4j2;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
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
  public void addCoupon(@RequestBody CouponDTO couponInfo) {
    
    if (CouponDTO.hasNullData(couponInfo)) {
      log.error("insufficient coupon information! {}", couponInfo.toString());
      throw new NullPointerException("insufficient coupon information! " + couponInfo.toString());
    }
    
    couponService.addCoupon(couponInfo);
  }

  /**
   * 쿠폰 이름과 만료일을 수정한다.
   * 
   * @param id 쿠폰 아이디
   * @param name 수정할 쿠폰 이름
   * @param endAt 수정할 만료일
   * 
   * @author jinyoung
   */
  @PatchMapping
  public void updateCouponNameAndEndAt(Long id, String name, LocalDateTime endAt) {
    couponService.updateCouponNameAndEndAt(id, name, endAt);
  }
  
  /**
   * 쿠폰을 삭제한다.
   * 
   * @param id 쿠폰 아이디
   */
  public void deleteCoupon(Long id) {
    couponService.deleteCoupon(id);
  }
  
  
  /**
   * 만료일이 지나지 않은 쿠폰들을 조회한다.
   * @return 쿠폰리스트
   * 
   * @author jinyoung
   */
  @GetMapping
  public List<CouponDTO> getAvailableCoupons() {
    return couponService.getAvaliableCoupons();
  }
  
}
