package com.delfood.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueMapper {
  
  public int countCouponIssue(Long couponId);
}
