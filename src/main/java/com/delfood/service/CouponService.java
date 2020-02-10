package com.delfood.service;

import com.delfood.dto.order.coupon.CouponDTO;
import com.delfood.dto.order.coupon.CouponDTO.DiscountType;
import com.delfood.error.exception.coupon.IssuedCouponExistException;
import com.delfood.mapper.CouponMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class CouponService {

  @Autowired
  private CouponMapper couponMapper;
  
  @Autowired
  private CouponIssueService couponIssueService;
  
  /**
   * 쿠폰 추가.
   * @param couponInfo
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addCoupon(CouponDTO couponInfo) {
    
    verifyDiscountData(couponInfo.getDiscountType(), couponInfo.getDiscountValue());
    
    Long insertResult = couponMapper.insertCoupon(couponInfo);

    if (insertResult != 1) {
      log.error("coupon Insert Error! {}", couponInfo.toString());
      throw new RuntimeException("coupon Insert Error");
    }
    
    if (couponInfo.getEndAt().isBefore(couponInfo.getCreatedAt())) {
      log.error("coupon expiration date is ealire than creation date! "
          + "EndAt : {}, startAt : {}",couponInfo.getEndAt(), couponInfo.getCreatedAt());
      throw new IllegalStateException("coupon expiration date is ealire than creation date!");
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
  
  /**
   * 쿠폰 이름과 만료일 수정.
   * 
   * @param id 쿠폰 아이디
   * @param name 이름
   * @param endAt 만료일
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void updateCouponNameAndEndAt(Long id, String name, LocalDateTime endAt) {
    if (couponIssueService.isIssued(id)) {
      log.error("Issued Coupon already exists");
      throw new IssuedCouponExistException("Issued Coupon already exists");
    }
    
    int result = couponMapper.updateCouponNameAndEndAt(id, name, endAt);
    if (result != 1) {
      log.error("coupon update error! id : {}, name : {}, EndAt : {} ", id, name, endAt);
      throw new RuntimeException("coupon update error!");
    }
  }
  
  /**
   * 쿠폰삭제.
   * @param id 쿠폰 아이디
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void deleteCoupon(Long id) {
    if (couponIssueService.isIssued(id)) {
      log.error("Issued Coupon already exists");
      throw new IssuedCouponExistException("Issued Coupon already exists");
    }
    
    int result = couponMapper.deleteCoupon(id);
    if (result != 1) {
      log.error("coupon delete error! id : {}",id);
      throw new RuntimeException("coupon delete error!");
    }
  }
  
  /**
   * 사용 가능한 쿠폰을 조회한다. (만료일이 현재시간 이후의 쿠폰만 조회)
   * @return 쿠폰 리스트
   */
  public List<CouponDTO> getAvaliableCoupons() {
    return couponMapper.findByEndAtGreaterThanNow();
  }
  
}
