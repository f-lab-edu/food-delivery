package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.service.OrderService;
import com.delfood.utils.SessionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/")
@Log4j2
public class OrderController {

  @Autowired
  OrderService orderService;
  
  
  @PostMapping("price")
  @MemberLoginCheck
  public TotalPriceResponse totalPrice(HttpSession session, @RequestBody List<OrderItemDTO> items) {
    return new TotalPriceResponse(orderService.totalPrice(items));
  }
  
  @Getter
  @AllArgsConstructor
  private static class TotalPriceResponse {
    long totalPrice;
  }
}
