package com.delfood.service;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delfood.dto.CouponDTO;
import com.delfood.dto.CouponIssueDTO;
import com.delfood.dto.ItemsBillDTO.CouponInfo;
import com.delfood.error.exception.DuplicateException;
import com.delfood.mapper.CouponIssueMapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CouponIssueService {
  
  @Autowired
  private CouponIssueMapper couponIssueMapper;
  
  @Autowired
  private CouponService couponService;
  
  /**
   * 쿠폰이 발급된 적이 있는 지 조회한다.
   * @param couponId 쿠폰 아이디
   * @return 쿠폰의 발급여부 ( true : 발급된 적 있음 , false : 발급된 적 없음
   * 
   * @author jinyoung
   */
  public boolean isIssued(Long couponId) {
    return couponIssueMapper.countCouponIssue(couponId) > 0;
  }
  
  /**
   * 회원이 이미 해당 쿠폰을 발급받은 적이 있는지 체크한다.
   * @param memberId 회원 아이디
   * @param couponId 쿠폰 아이디
   * @return
   */
  public boolean checkDuplicateIssue(String memberId, Long couponId) {
    return couponIssueMapper.countCouponIssueByMemberIdAndCouponId(memberId, couponId) > 0;
  }
  
  /**
   * 회원에게 쿠폰을 발급한다.
   * @param memberId 회원 아이디
   * @param couponId 쿠폰 아이디
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void createCouponIssue(String memberId, Long couponId) {
  
    if (checkDuplicateIssue(memberId, couponId)) {
      log.error("coupon has already been issued! memberid :{}, couponId :{}", memberId, couponId);
      throw new DuplicateException("coupon has already been issued!");
    }
    
    int result = couponIssueMapper.insertCouponIssue(memberId, couponId);
    if (result != 1) {
      log.error("insert couponIssue Error! memberId : {}, couponId : {}", memberId, couponId);
      throw new RuntimeException("insert couponIssue Error! ");
    }
  }
  
  /**
   * 발급 쿠폰 사용.
   * 발급 쿠폰의 상태를 사용됨으로 변경한다.
   * @param id 발급 쿠폰 아이디
   * 
   * @author jinyoung
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void useCouponIssue(Long id, Long paymentId) {
    int result = couponIssueMapper.updateCouponIssueStatusToUsed(id, paymentId);
    if (result != 1) {
      log.error("update coupon status error! id : {}", id);
      throw new RuntimeException("update coupon status error!");
    }
  }
  
  /**
   * 회원이 가진 발행 쿠폰들을 조회한다.
   * @param memberId 회원 아이디
   * @return
   */
  public List<CouponIssueDTO> getCouponIssues(String memberId) {
    return couponIssueMapper.findByMemberId(memberId);
  }

  /**
   * 발행 쿠폰 아이디를 기준으로 쿠폰 전반 정보를 조회한다.
   * @author jun
   * @param couponIssueId 발행 쿠폰 아이디
   * @return
   */
  public CouponInfo getCouponInfoByIssueId(long couponIssueId) {
    return couponIssueMapper.findInfoById(couponIssueId);
  }
  
  
  /**
   * 쿠폰으로 인한 할인 가격을 계산한다.
   * 쿠폰이 퍼센트 쿠폰일 시 입력된 가격을 기준으로 퍼센트 할인 가격을 리턴한다.
   * 쿠폰이 정액 할인 쿠폰일 시 쿠폰의 할인값을 리턴한다.
   * 쿠폰의 할인 값이 아이템 가격보다 클 시 아이템의 가격을 할인값으로 리턴한다.
   * 
   * @author jun
   * @param couponIssueId
   * @param price
   * @return
   */
  public long discountPrice(long couponIssueId, long price) {
    CouponInfo couponInfo = getCouponInfoByIssueId(couponIssueId);
    long discountPrice = 0;
    
    if (couponInfo.getDiscountType() == CouponDTO.DiscountType.PERCENT) {
      discountPrice = price * couponInfo.getDiscountValue() / 100L;
    } else {
      discountPrice = couponInfo.getDiscountValue();
    }
    
    return discountPrice > price ? price : discountPrice;
  }
  
  /**
   * 해당 발행 쿠폰이 사용상태인지 확인한다. 사용한 쿠폰이라면 true를 반환한다.
   * @author jun
   * @param couponIssueId 발행 쿠폰 아이디
   * @return
   */
  public boolean isUsed(long couponIssueId) {
    CouponIssueDTO couponIssueInfo = couponIssueMapper.findById(couponIssueId);
    
    if (Objects.isNull(couponIssueInfo)) {
      log.error("발행쿠폰 사용여부 체크 오류! 조회한 발행 쿠폰 정보가 없습니다. 발행 쿠폰 아이디 : {}", couponIssueId);
      throw new IllegalArgumentException("잘못된 쿠폰 발행 번호입니다.");
    }
    
    return couponIssueInfo.getStatus().equals(CouponIssueDTO.Status.USED);
  }
  
  
  
}
