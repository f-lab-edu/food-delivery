package com.delfood.dto;

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
  
  private Long id;
  
  private String memberId;
  
  private Long couponId;
  
  @JsonFormat(pattern = "yy-MM-dd hh:mm:ss")
  private LocalDateTime createdAt;
  
  private Status status;
  
  private Long paymentId;
  
}
