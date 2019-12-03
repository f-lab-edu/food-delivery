package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.ItemDTO;
import com.delfood.service.CartService;
import com.delfood.utils.SessionUtil;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartControllelr {
  
  @Autowired
  private CartService cartService;
  
  @PostMapping("/members/cart/menus")
  @MemberLoginCheck
  public void addMenu(@RequestBody ItemDTO item, HttpSession session) {
    cartService.addOrdersItem(item, SessionUtil.getLoginMemberId(session));
  }
  
  @GetMapping("/members/cart/menus")
  @MemberLoginCheck
  public List<ItemDTO> getCart(HttpSession session) {
    return cartService.getItems(SessionUtil.getLoginMemberId(session));
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
  
  @GetMapping("/members/cart/price")
  @MemberLoginCheck
  public CartPriceResponse cartPrice(HttpSession session) {
    return new CartPriceResponse(cartService.allPrice(SessionUtil.getLoginMemberId(session)));
  }
  
  
  
  // Response
  @Getter
  @AllArgsConstructor
  private static class CartPriceResponse {
    private long totalPrice;
  }
  
}
