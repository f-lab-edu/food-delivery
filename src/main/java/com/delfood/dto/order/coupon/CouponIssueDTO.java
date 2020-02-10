package com.delfood.dto.order.coupon;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;

@Setter
@Getter
@ToString
@Alias("couponIssue")
public class CouponIssueDTO {
  
  public enum Status {
    DEFAULT, USED
  }
  
  public enum DiscountType {
    WON, PERCENT
  }
  
  private Long id;
  
  private String memberId;
  
  private Long couponId;
  
  private Status status;
  
  private Long paymentId;
  
  private DiscountType discountType;
  
  private String name;
  
  private Long discountValue;
  
  @JsonFormat(pattern = "yy-MM-dd hh:mm:ss")
  private LocalDateTime createdAt;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endAt;
  
}
