package com.delfood.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delfood.dto.CouponIssueDTO;
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
  public void useCouponIssue(Long id) {
    int result = couponIssueMapper.updateCouponIssueStatusToUsed(id);
    if (result != 1) {
      log.error("update coupon status error! id : {}", id);
      throw new RuntimeException("update coupon status error!");
    }
  }
  
}
