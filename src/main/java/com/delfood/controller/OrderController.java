package com.delfood.controller;

import com.delfood.dto.OrderFormDTO;

import com.delfood.service.OrderService;

import com.delfood.utils.SessionUtil;

import javax.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/")
@Log4j2
public class OrderController {

  @Autowired
  OrderService orderService;
  
  /**
   * 주문 신청을 위한 기본 정보 조회.
   * 
   * @param session 현재 사용자 세션
   * @return
   */
  @RequestMapping("form")
  public ResponseEntity<OrderFormDTO> orderForm(HttpSession session) {
    String memberId = SessionUtil.getLoginMemberId(session);
    
    OrderFormDTO orderFormInfo = orderService.getOrderForm(memberId);
    
    return new ResponseEntity<OrderFormDTO>(orderFormInfo, HttpStatus.OK);
  }

  
}
