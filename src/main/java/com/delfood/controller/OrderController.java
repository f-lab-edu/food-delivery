package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.controller.response.OrderResponse;
import com.delfood.dto.ItemsBillDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderBillDTO;
import com.delfood.service.OrderService;
import com.delfood.utils.SessionUtil;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  
  /**
   * 아이템들의 가격과 정보를 조회한다.
   * @author jun
   * @param items 가격을 계산할 아이템들
   * @return
   */
  @GetMapping("price")
  @MemberLoginCheck
  public long getItemsBill(HttpSession session, @RequestBody List<OrderItemDTO> items) {
    return orderService.totalPrice(SessionUtil.getLoginMemberId(session), items);
  }
  
  /**
   * 주문 정보를 조회한다. 주문한 메뉴, 옵션, 가격 등의 정보를 조회할 수 있다.
   * @author jun
   * @param orderId 주문번호
   * @return
   */
  @GetMapping("orderBill/{orderId}")
  @MemberLoginCheck
  public OrderBillDTO orderInfo(@PathVariable("orderId") Long orderId) {
    return orderService.getPreOrderBill(orderId);
  }
  
  /**
   * 주문을 진행한다.
   * 클라이언트에서 계산한 총 가격과 서버에서 계산한 총 가격이 다를 시 에러를 발생시킨다.
   * @param session 사용자의 세션
   * @param request 주문 정보
   * @return
   */
  @PostMapping
  @MemberLoginCheck
  public OrderResponse order(HttpSession session, @RequestBody OrderRequest request) {
    return orderService.order(SessionUtil.getLoginMemberId(session), request.getItems(),
        request.getTotalPrice(), request.getShopId());
  }
  
  /**
   * 아이템 리스트들을 상세하게 계산서로 발행한다.
   * @param session 사용자의 세션
   * @param items 주문하기 전 아이템들
   * @return
   */
  @GetMapping("bill")
  public ItemsBillDTO getBill(HttpSession session, @RequestBody List<OrderItemDTO> items) {
    return orderService.getBill(SessionUtil.getLoginMemberId(session), items);
  }
  
  /**
   * 회원 주문내역을 모두 조회한다.
   * 추후 페이징 추가 해야한다.
   * @author jun
   * @param session 사용자의 세션
   * @return
   */
  @GetMapping
  @MemberLoginCheck
  public List<OrderDTO> myOrders(HttpSession session) {
    return orderService.getMemberOrder(SessionUtil.getLoginMemberId(session));
  }
  
  /**
   * 주문 번호를 기반으로 주문 상세내역을 조회한다.
   * 추후 해당 회원의 주문인지 확인하는 로직을 작성해야한다.
   * @param session 사용자의 세션
   * @param orderId 주문 아이디
   * @return
   */
  @GetMapping("{orderId}")
  @MemberLoginCheck
  public OrderDTO getOrder(HttpSession session, @PathVariable Long orderId) {
    return orderService.getOrder(orderId);
  }
  
  // request
  @Getter
  private static class OrderRequest {
    private Long shopId;
    private List<OrderItemDTO> items;
    private long totalPrice;
  }
}
