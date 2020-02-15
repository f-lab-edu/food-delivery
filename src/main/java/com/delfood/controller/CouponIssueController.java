package com.delfood.controller;

import com.delfood.aop.LoginCheck;
import com.delfood.aop.LoginCheck.UserType;
import com.delfood.dto.order.coupon.CouponIssueDTO;
import com.delfood.aop.MemberLoginCheck;
import com.delfood.service.CouponIssueService;
import com.delfood.utils.SessionUtil;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/couponIssues/")
public class CouponIssueController {
  
  @Autowired
  private CouponIssueService couponIssueService;
  
  /**
   * 회원에게 쿠폰을 발행한다.
   * @param session 현재 사용자 세션
   * @param couponId 쿠폰 아이디
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @LoginCheck(type = UserType.MEMBER)
  public void addCouponIssue(HttpSession session, @RequestBody Long couponId) {
    
    couponIssueService.createCouponIssue(SessionUtil.getLoginMemberId(session), couponId);
  }
  
  /**
   * 회원이 가지고 있는 발행 쿠폰들을 조회한다.
   * @param session 현재 사용자 세션
   * @return
   */
  @GetMapping
  @LoginCheck(type = UserType.MEMBER)
  public List<CouponIssueDTO> getCouponIssues(HttpSession session) {
    return couponIssueService.getCouponIssues(SessionUtil.getLoginMemberId(session));
  }
  
}
