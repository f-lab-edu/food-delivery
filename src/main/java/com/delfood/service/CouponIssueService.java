package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.delfood.mapper.CouponIssueMapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CouponIssueService {
  
  @Autowired
  private CouponIssueMapper couponIssueMapper;
  
  public boolean isIssued(Long couponId) {
    return couponIssueMapper.countCouponIssue(couponId) > 0;
  }
  
}
