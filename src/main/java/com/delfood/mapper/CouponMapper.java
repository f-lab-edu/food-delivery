package com.delfood.mapper;

import com.delfood.dto.CouponDTO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponMapper {
    
  /**
   * 쿠폰 추가.
   * @param couponInfo 쿠폰 정보
   * @return
   */
  public Long insertCoupon(CouponDTO couponInfo);

  /**
   * 쿠폰 수정.
   * @param id 쿠폰 아이디
   * @param name 이름
   * @param endAt 만료일
   * @return
   */
  public int updateCouponNameAndEndAt(Long id, String name, LocalDateTime endAt);

  /**
   * 쿠폰삭제.
   * @param id 쿠폰 아이디
   * @return
   */
  public int deleteCoupon(Long id);

}
