package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.controller.response.OrderResponse;
import com.delfood.dto.ItemsBillDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.error.exception.order.TotalPriceMismatchException;
import com.delfood.dto.OrderBillDTO;
import com.delfood.service.OrderService;
import com.delfood.utils.SessionUtil;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.codehaus.commons.nullanalysis.Nullable;
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
  @GetMapping("{orderId}/bill")
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
    if (request.getItems().isEmpty()) {
      // items가 null일때도 NullpointerException이 발생한다
      throw new NullPointerException("아이템이 없습니다.");
    }
    
    // 해당 아이템들이 해당 매장의 것인지 검증
    if (orderService.isShopItems(request.getItems(), request.getShopId()) == false) {
      log.error("주문하신 매장의 메뉴 또는 옵션이 아닙니다.");
      throw new IllegalArgumentException("주문하신 매장의 메뉴 또는 옵션이 아닙니다.");
    }    
    // 클라이언트가 계산한 금액과 서버에서 계산한 금액이 같은지 비교
    long totalPriceFromServer =
        orderService.totalPrice(SessionUtil.getLoginMemberId(session), request.getItems());
    if (totalPriceFromServer != request.getTotalPrice()) {
      log.error("Total Price Mismatch! client price : {}, server price : {}",
          request.getTotalPrice(),
          totalPriceFromServer);
      throw new TotalPriceMismatchException("Total Price Mismatch!");
    }

    return orderService.order(SessionUtil.getLoginMemberId(session), request.getItems(),
        request.getShopId());
  }
  
  /**
   * 아이템 리스트들을 상세하게 계산서로 발행한다.
   * @param session 사용자의 세션
   * @param items 주문하기 전 아이템들
   * @return
   */
  @GetMapping("bill")
  @MemberLoginCheck
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
  public List<OrderDTO> myOrders(HttpSession session, @Nullable Long lastViewedOrderId) {
    return orderService.getMemberOrder(SessionUtil.getLoginMemberId(session), lastViewedOrderId);
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
    OrderDTO orderInfo = orderService.getOrder(orderId);
    if (orderInfo == null) {
      log.info("존재하지 않는 주문번호 조회. 주문 번호 : {}", orderId);
      throw new IllegalArgumentException("존재하지 않는 주문 정보입니다.");
    }
    
    String memberId = SessionUtil.getLoginMemberId(session);
    if (memberId.equals(orderInfo.getMemberId()) == false) {
      throw new IllegalArgumentException("해당 회원의 주문이 아닙니다!");
    }
    return orderInfo;
  }
  
  // request
  @Getter
  private static class OrderRequest {
    private Long shopId;
    private List<OrderItemDTO> items;
    private long totalPrice;
  }
}
