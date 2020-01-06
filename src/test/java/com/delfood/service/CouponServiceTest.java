package com.delfood.service;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.delfood.dto.CouponDTO;
import com.delfood.dto.CouponDTO.DiscountType;
import com.delfood.dto.CouponDTO.Status;
import com.delfood.error.exception.coupon.IssuedCouponExistException;
import com.delfood.mapper.CouponMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CouponServiceTest {

  @InjectMocks
  private CouponService couponService;
  
  @Mock
  private CouponMapper couponMapper;
  
  @Mock
  CouponIssueService couponIssueService;
  
  /**
   * 정상적으로 작동할 수 있는 쿠폰 DTO를 새로 생성하여 반환한다.
   * @author jun
   * @return
   */
  public static CouponDTO generateCoupon() {
    CouponDTO couponInfo = new CouponDTO();
    
    couponInfo.setId(111L);
    couponInfo.setName("Test Coupon");
    couponInfo.setDiscountType(DiscountType.PERCENT);
    couponInfo.setDiscountValue(10L);
    couponInfo.setCreatedAt(LocalDateTime.now().minusDays(1)); // 항상 오늘보다 하루 전 만들어진 쿠폰으로 설정한다.
    couponInfo.setUpdatedAt(LocalDateTime.now().minusDays(1));
    couponInfo.setEndAt(LocalDateTime.now().plusDays(1)); // 항상 오늘보다 하루 뒤 종료하도록 설정한다.
    couponInfo.setStatus(Status.DEFAULT);
    
    return couponInfo;
  }
  
  @Test
  public void addCouponTest_쿠폰_추가_성공() {
    CouponDTO couponInfo = generateCoupon();
    given(couponMapper.insertCoupon(couponInfo)).willReturn(1L);
    
    couponService.addCoupon(couponInfo);
  }
  
  @Test(expected = IllegalStateException.class)
  public void addCouponTest_쿠폰_추가_실패_종료일_설정_오류() {
    CouponDTO couponInfo = generateCoupon();
    couponInfo.setEndAt(couponInfo.getCreatedAt().minusDays(1));
    given(couponMapper.insertCoupon(couponInfo)).willReturn(1L);
    
    couponService.addCoupon(couponInfo);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addCouponTest_쿠폰_추가_실패_할인율_101퍼센트() {
    CouponDTO discountValueErrorCouponInfo = generateCoupon();
    discountValueErrorCouponInfo.setDiscountValue(101L);
    
    couponService.addCoupon(discountValueErrorCouponInfo);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addCouponTest_쿠폰_추가_실패_할인율_0미만퍼센트() {
    CouponDTO discountValueErrorCouponInfo = generateCoupon();
    discountValueErrorCouponInfo.setDiscountValue(-1L);
    
    couponService.addCoupon(discountValueErrorCouponInfo);
  }
  
  @Test
  public void updateCouponNameAndEndAtTest_쿠폰_업데이트_성공() {
    String updateName = "new Test Coupon Name";
    LocalDateTime updateEndAt = LocalDateTime.now().plusDays(1L);
    given(couponIssueService.isIssued(1L)).willReturn(false);
    given(couponMapper.updateCouponNameAndEndAt(1L, updateName,
        updateEndAt)).willReturn(1);
    
    couponService.updateCouponNameAndEndAt(1L, updateName, updateEndAt);
  }
  
  @Test(expected = IssuedCouponExistException.class)
  public void updateCouponNameAndEndAtTest_쿠폰_업데이트_실패_이미발행() {
    String updateName = "new Test Coupon Name";
    given(couponIssueService.isIssued(1L)).willReturn(true);
    
    couponService.updateCouponNameAndEndAt(1L, updateName, LocalDateTime.now().plusDays(1L));
  }
  
  @Test
  public void deleteCouponTest_쿠폰_삭제_성공() {
    given(couponIssueService.isIssued(1L)).willReturn(false);
    given(couponMapper.deleteCoupon(1L)).willReturn(1);
    
    couponService.deleteCoupon(1L);
  }
  
  @Test(expected = IssuedCouponExistException.class)
  public void deleteCouponTest_쿠폰_삭제_실패_이미발행() {
    given(couponIssueService.isIssued(1L)).willReturn(true);
    
    couponService.deleteCoupon(1L);
  }
  
  @Test
  public void getAvaliableCouponsTest_사용가능_쿠폰_조회_성공() {
    given(couponMapper.findByEndAtGreaterThanNow())
        .willReturn(Arrays.asList(new CouponDTO[] {generateCoupon(), generateCoupon()}));
    
    assertThat(couponService.getAvaliableCoupons())
      .isNotEmpty()
      .hasSize(2);
  }

  
}
