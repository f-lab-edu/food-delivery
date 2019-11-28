package com.delfood.controller;

import java.util.List;
import java.util.function.Predicate;
import javax.servlet.http.HttpSession;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.OrdersItemDTO;
import com.delfood.service.CartService;
import com.delfood.utils.SessionUtil;

@RestController
public class CartControllelr {
  
  @Autowired
  private CartService cartService;
  
  @PostMapping("/members/cart/menus")
  @MemberLoginCheck
  public void addMenu(@RequestBody OrdersItemDTO ordersItem, HttpSession session) {
    cartService.addOrdersItem(ordersItem, SessionUtil.getLoginMemberId(session));
  }
  
  @GetMapping("/members/cart/menus")
  @MemberLoginCheck
  public List<OrdersItemDTO> getCart(HttpSession session) {
    return cartService.getOrdersItems(SessionUtil.getLoginMemberId(session));
  }
  
  @DeleteMapping("/members/cart/menus")
  @MemberLoginCheck
  public void clearCart(HttpSession session) {
    cartService.claer(SessionUtil.getLoginMemberId(session));
  }
  
  @DeleteMapping("/members/cart/menus/{index}")
  @MemberLoginCheck
  public void deleteCartMenu(HttpSession session, @PathVariable long index) {
    cartService.deleteCartMenu(SessionUtil.getLoginMemberId(session), index);
  }
}
