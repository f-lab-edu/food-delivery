package com.delfood.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delfood.dto.CouponIssueDTO;
import com.delfood.dto.ItemsBillDTO.CouponInfo;

@Repository
public interface CouponIssueMapper {
  
  /**
   * 해당 쿠폰아이디로 발급된 적이 있는 쿠폰의 수를 조회한다.
   * 
   * @param couponId 쿠폰 아이디
   * @return 발급된 쿠폰의 수
   * 
   * @author jinyoung
   */
  public int countCouponIssue(Long couponId);

  /**
   * 회원 아이디와 쿠폰 아이디를 통해 이미 발급된 쿠폰의 수를 조회한다.
   *
   * @param memberId 회원 아이디
   * @param couponId 쿠폰 아이디
   * @return 발급된 쿠폰의 수
   * 
   * @author jinyoung
   */
  public int countCouponIssueByMemberIdAndCouponId(String memberId, Long couponId);
  
  /**
   * 발급 쿠폰을 추가한다. 
   * @param memberId 회원 아이디 
   * @param couponId 쿠폰 아이디
   * @return
   * 
   * @author jinyoung
   */
  public int insertCouponIssue(String memberId, Long couponId);

  /**
   * 발급 쿠폰의 상태를 USED로 변경한다.
   * @param id 발급 쿠폰 아이디
   * 
   * @author jinyoung
   */
  public int updateCouponIssueStatusToUsed(Long id);

  /**
   * 회원이 가진 쿠폰들을 조회한다.
   * @param memberId 회원 아이디
   * @return
   */
  public List<CouponIssueDTO> findByMemberId(String memberId);

  public CouponInfo findInfoById(long couponIssueId);

}
