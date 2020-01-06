package com.delfood.service;

import static org.mockito.BDDMockito.given;
import com.delfood.error.exception.DuplicateException;
import com.delfood.mapper.CouponIssueMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CouponIssueServiceTest {
  
  @InjectMocks
  private CouponIssueService couponIssueService;
  
  @Mock
  private CouponIssueMapper couponIssueMapper;
  
  @Mock
  private CouponService couponService;

  @Test
  public void createCouponIssue_쿠폰_발급_성공() {
    String memberId = "eric";
    long couponId = 1L;
    
    given(couponIssueMapper.countCouponIssueByMemberIdAndCouponId(memberId, couponId))
      .willReturn(0);
    given(couponIssueMapper.insertCouponIssue(memberId, couponId)).willReturn(1);
    
    couponIssueService.createCouponIssue(memberId, couponId);
  }
  
  @Test(expected = DuplicateException.class)
  public void createCouponIssue_쿠폰_발급_실패_재발급() {
    String memberId = "eric";
    long couponId = 1L;
    
    given(couponIssueMapper.countCouponIssueByMemberIdAndCouponId(memberId, couponId))
      .willReturn(1);
    
    couponIssueService.createCouponIssue(memberId, couponId);
  }
  
  @Test
  public void useCouponIssueTest_쿠폰_사용_성공() {
    long id = 1L;
    given(couponIssueMapper.updateCouponIssueStatusToUsed(id)).willReturn(1);
    couponIssueService.useCouponIssue(id);
  }

}
