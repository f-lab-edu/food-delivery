package com.delfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleCouponInfo {
  private Long couponIssueId;
  private Long couponId;
  private String couponName;
  private Long discountType;
  private Long discountValue;
  private Long discountPrice;
}
