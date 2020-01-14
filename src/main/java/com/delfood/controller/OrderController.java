package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.aop.OwnerLoginCheck;
import com.delfood.controller.response.OrderResponse;
import com.delfood.dto.ItemsBillDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.error.exception.coupon.IssuedCouponExistException;
import com.delfood.error.exception.order.TotalPriceMismatchException;
import com.delfood.dto.OrderBillDTO;
import com.delfood.service.CouponIssueService;
import com.delfood.service.OrderService;
import com.delfood.service.PushService;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.codehaus.commons.nullanalysis.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
  
  @Autowired
  ShopService shopService;
  
  @Autowired
  PushService pushService;
  
  @Autowired
  CouponIssueService couponIssueService;
  
  /**
   * 아이템들의 가격과 정보를 조회한다.
   * 쿠폰과 배달 가격을 제외한 순수 아이템 가격만 제공한다.
   * @author jun
   * @param items 가격을 계산할 아이템들
   * @return
   */
  @GetMapping("price")
  @MemberLoginCheck
  public ItemsBillResponse getItemsBill(HttpSession session,
      @RequestBody List<OrderItemDTO> items) {
    long itemsPrice = orderService.totalPrice(SessionUtil.getLoginMemberId(session), items);
    return ItemsBillResponse.builder().itemsPrice(itemsPrice).build();
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
    
    // 쿠폰이 유효한지 검증
    if (couponIssueService.isUsed(request.getCouponIssueId())) {
      log.info("이미 사용한 쿠폰 사용 시도. 요청 발행 쿠폰 아이디 : {}", request.getCouponIssueId());
      throw new IssuedCouponExistException("이미 사용한 쿠폰입니다");
    }
    
    // 클라이언트가 계산한 금액과 서버에서 계산한 금액이 같은지 비교
    long totalItemsPriceFromServer = orderService.totalPrice(SessionUtil.getLoginMemberId(session),
        request.getItems());
    long discountPriceFromServer =
        couponIssueService.discountPrice(request.getCouponIssueId(), totalItemsPriceFromServer);
    long totalPrice = totalItemsPriceFromServer - discountPriceFromServer;
    if (totalPrice != request.getTotalPrice()) {
      log.error(
          "Total Price Mismatch! client price : {}, server price : {},"
          + " totalItemsPriceFromServer : {}, discountPriceFromServer : {}",
          request.getTotalPrice(), totalPrice, totalItemsPriceFromServer, discountPriceFromServer);
      throw new TotalPriceMismatchException("Total Price Mismatch!");
    }

    OrderResponse orderResponse = orderService.order(SessionUtil.getLoginMemberId(session),
        request.getItems(), request.getShopId(), request.getCouponIssueId());
    
    return orderResponse;
  }
  
  /**
   * 아이템 리스트들을 상세하게 계산서로 발행한다.
   * @param session 사용자의 세션
   * @param billRequest 주문할 아이템들, 쿠폰정보. 쿠폰정보는 Null 가능
   * @return
   */
  @GetMapping("bill")
  @MemberLoginCheck
  public ItemsBillDTO getBill(HttpSession session, @RequestBody BillRequest billRequest) {
    if (couponIssueService.isUsed(billRequest.getCouponIssueId())) {
      log.info("이미 사용한 쿠폰 사용 시도. 요청 발행 쿠폰 아이디 : {}", billRequest.getCouponIssueId());
      throw new IssuedCouponExistException("이미 사용한 쿠폰입니다");
    }
    return orderService.getBill(SessionUtil.getLoginMemberId(session), billRequest.getItems(),
        billRequest.getCouponIssueId());
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
  
  
  
  // 여기서 부터는 사장님 관련 컨트롤러입니다.
  
  /**
   * 사장님이 소유한 가게에 요청된 주문들을 조회한다.
   * 유효한 주문만 조회된다.
   * @author jun
   * @return
   */
  @GetMapping("owner")
  @OwnerLoginCheck
  public List<OrderBillDTO> getRequestedOrders(HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    List<OrderBillDTO> shopOrders = orderService.getOwnerOrderRequest(ownerId);
    return shopOrders;
  }
  
  /**
   * 주문을 승인한다.
   * @author jun
   * @param orderId 주문 아이디
   * @param request 주문 승낙시 입력해야하는 정보
   * @param session 사장님 세션
   */
  @PatchMapping("{orderId}/approve")
  @OwnerLoginCheck
  public void orderApprove(@PathVariable(name = "orderId") Long orderId,
      @RequestBody OrderApproveRequest request,
      HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    // 해당 주문에 대한 권한이 있는지 확인한다
    if (orderService.isOwnerOrder(ownerId, orderId) == false) {
      throw new IllegalArgumentException("해당 주문에 대한 권한이 없습니다.");
    }   
    
    // 주문 승인을 진행한다
    orderService.orderApprove(orderId, request.getMinute());
  }
  
  
  // request
  @Getter
  private static class OrderRequest {
    @NonNull
    private Long shopId;
    @NonNull
    private List<OrderItemDTO> items;
    @Nullable
    private Long couponIssueId;
    private long totalPrice;
  }
  
  @Getter
  private static class BillRequest {
    private List<OrderItemDTO> items;
    @Nullable
    private Long couponIssueId;
  }
  
  @Getter
  private static class OrderApproveRequest {
    @NonNull
    private Long minute; // 몇분이나 걸릴지 입력한 값
    
  }
  
  // response
  
  @Builder
  @Getter
  private static class ItemsBillResponse {
    private long itemsPrice;
  }
}
