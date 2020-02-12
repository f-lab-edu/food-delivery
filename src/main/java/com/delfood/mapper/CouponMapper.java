package com.delfood.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.delfood.dto.order.coupon.CouponDTO;

@Repository
public interface CouponMapper {
    
  /**
   * 쿠폰 추가.
   * @param couponInfo 쿠폰 정보
   * @return
   */
  public Long insertCoupon(CouponDTO couponInfo);

  /**
   * 쿠폰 이름과 만료일 수정.
   * @param id 쿠폰 아이디
   * @param name 이름
   * @param endAt 만료일
   * @return
   */
  public int updateCouponNameAndEndAt(Long id, String name, LocalDateTime endAt);

  /**
   * 쿠폰 삭제.
   * @param id 쿠폰 아이디
   * @return
   */
  public int deleteCoupon(Long id);
  
  /**
   * 만료일이 지나지 않은 쿠폰 조회.
   * @return
   */
  public List<CouponDTO> findByEndAtGreaterThanNow();
}
