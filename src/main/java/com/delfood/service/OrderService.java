package com.delfood.service;

import com.delfood.controller.response.OrderResponse;
import com.delfood.dto.AddressDTO;
import com.delfood.dto.ItemsBillDTO;
import com.delfood.dto.ItemsBillDTO.ShopInfo;
import com.delfood.dto.OrderDTO.OrderStatus;
import com.delfood.dto.MemberDTO;
import com.delfood.dto.OrderBillDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
import com.delfood.dto.PaymentDTO;
import com.delfood.dto.PaymentDTO.Type;
import com.delfood.dto.push.PushMessage;
import com.delfood.mapper.OrderMapper;
import com.delfood.utils.OrderUtil;
import com.google.firebase.database.annotations.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class OrderService {
  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private MemberService memberService;
  
  @Autowired
  private ShopService shopService;
  
  @Autowired
  private AddressService addressService;
  
  @Autowired
  private MockPayService mockPayService;
  
  @Autowired
  private PaymentService paymentService;
  
  @Autowired
  private PushService pushService;
  
  @Autowired
  private CouponIssueService couponIssueService;
  
  /**
   * 주문 요청을 진행한다.
   * 사용자가 주문 요청시 전달받은 가격과, 서버에서 직접 비교한 가격을 비교하여 다르면 예외처리 할 예정.
   * @param memberId 고객 아이디
   * @param items 주문한 아이템들
   * @return
   */
  @Transactional
  public OrderResponse order(String memberId, List<OrderItemDTO> items, long shopId,
      @Nullable Long couponIssueId) {

    // 주문 준비 작업. 결제 전.
    Long orderId = doOrder(memberId, items, shopId);
    
    // 계산서 발행
    ItemsBillDTO bill = getBill(memberId, items, couponIssueId);
    
    
    // 가상 결제 진행
    PaymentDTO paymentInfo = PaymentDTO.builder()
        .type(Type.CARD)
        .amountPayment(bill.getTotalPrice()) // 추후 할인 금액을 빼줘야함
        .orderId(orderId)
        .amountDiscount(0L) // 쿠폰 로직 제작 후 작성 예정
        .build();
    
    PaymentDTO payResult = mockPayService.pay(paymentInfo);
    paymentService.insertPayment(payResult);
    
    // 결제 완료 처리
    updateStatus(orderId, OrderDTO.OrderStatus.ORDER_REQUEST);
    
    
    // 쿠폰 사용처리
    if (bill.getCouponInfo() != null) {
      couponIssueService.useCouponIssue(bill.getCouponInfo().getCouponIssueId(), payResult.getId());
    }
    
    // 사장님에게 알림(푸시)
    PushMessage pushMsg = PushMessage.ADD_ORDER_REQUEST;
    String ownerId = shopService.getShop(shopId).getOwnerId();
    pushService.sendMessageToOwner(pushMsg, ownerId); // Exception이 발생하지 않는다.
    
    return new OrderResponse(bill, orderId);
  }
  
  /**
   * 주문 테이블에 insert를 진행한다.
   * 주문 메뉴, 주문 옵션이 추가된다.
   * 주문도중 에러가 나더라도 주문기록을 남기기 위해 독자적인 트랜잭션을 가진다.
   * 
   * @param memberId 고객 아이디
   * @param items 주문할 아이템들
   * @return
   */
  @Transactional(propagation = Propagation.NESTED)
  public Long doOrder(String memberId, List<OrderItemDTO> items, Long shopId) {
    MemberDTO memberInfo = memberService.getMemberInfo(memberId);
    OrderDTO order = OrderDTO
        .builder()
        .memberId(memberId)
        .addressCode(memberInfo.getAddressCode())
        .addressDetail(memberInfo.getAddressDetail())
        .shopId(shopId)
        .deliveryCost(addressService.deliveryPrice(memberId, shopId))
        .build();
    
    orderMapper.addOrder(order);
    Long orderId = order.getId();
    
    log.debug("addOrder Finished");
    log.debug("order id : {}", orderId);

    List<OrderItemOptionDTO> options = new ArrayList<OrderItemOptionDTO>();
    
    for (int i = 0; i < items.size(); i++) {
      OrderItemDTO item = items.get(i);
      item.setId(OrderUtil.generateOrderItemKey(memberId, i));
      item.setOrderId(orderId);
      for (int j = 0; j < item.getOptions().size(); j++) {
        OrderItemOptionDTO option = item.getOptions().get(j);
        option.setId(OrderUtil.generateOrderItemOptionKey(memberId, i, j));
        option.setOrderItemId(item.getId());
        options.add(option);
      }
    }
    
    orderMapper.addOrderItems(items);
    orderMapper.addOrderItemOptions(options);

    return order.getId();
  }
  
  
  /**
   * 고객이 선정한 메뉴의 총 계산서를 출력한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param items 주문 상품들. 메뉴, 옵션 리스트가 존재한다.
   * @return
   */
  @Transactional(readOnly = true)
  public ItemsBillDTO getBill(String memberId, List<OrderItemDTO> items, Long couponIssueId) {
    // 고객 주소 정보 추출
    AddressDTO addressInfo = memberService.getMemberInfo(memberId).getAddressInfo();
    // 매장 정보 추출
    ShopInfo shopInfo = shopService.getShopByMenuId(items.get(0).getMenuId());
    
    // 쿠폰 정보 추출
    ItemsBillDTO.CouponInfo couponInfo = null;
    if (couponIssueId != null) {
      couponInfo = couponIssueService.getCouponInfoByIssueId(couponIssueId);
    }
    
    // 배달료 계산
    long deliveryPrice = addressService.deliveryPrice(memberId, shopInfo.getId());
    
    // 계산서 생성
    ItemsBillDTO bill = ItemsBillDTO.builder()
                                    .memberId(memberId)
                                    .addressInfo(addressInfo)
                                    .shopInfo(shopInfo)
                                    .deliveryPrice(deliveryPrice)
                                    .menus(orderMapper.findItemsBill(items))
                                    .couponInfo(couponInfo)
                                    .ordersItems(items)
                                    .build();
    return bill;
  }
  
  
  /**
   * 아이템들의 총 가격을 계산한다.
   * @author jun
   * @param items 계산할 아이템들
   * @return 총 가격
   */
  @Transactional(readOnly = true)
  public long totalPrice(String memberId, List<OrderItemDTO> items) {
    long totalPrice = orderMapper.findItemsPrice(items);
    return totalPrice;
  }
  
  
  /**
   * 두 주소 사이 거리(Meter 단위)를 조회한다.
   * @author jun
   * @param startAddressCode 시작 주소
   * @param endAddressCode 도착 주소
   * @return
   */
  public double addressDistance(String startAddressCode, String endAddressCode) {
    return addressService.getDistanceMeter(startAddressCode, endAddressCode);
  }
  
  /**
   * 주문 정보를 조회한다.
   * @param orderId 주문 아이디
   * @return
   */
  public OrderBillDTO getPreOrderBill(Long orderId) {
    return orderMapper.findOrderBill(orderId);
  }
  
  /**
   * 고객의 주문 내역을 확인한다.
   * @author jun
   * @param memberId 고객아이디
   * @return
   */
  public List<OrderDTO> getMemberOrder(String memberId, Long lastViewedOrderId) {
    return orderMapper.findByMemberId(memberId, lastViewedOrderId);
  }
  
  /**
   * 주문 번호를 기반으로 주문 상세를 조회한다.
   * @author jun
   * @param orderId 주문 아이디
   * @return
   */
  public OrderDTO getOrder(Long orderId) {
    OrderDTO orderInfo = orderMapper.findById(orderId);
    return orderInfo;
  }
  
  /**
   * 해당 매장의 아이템인지 확인한다.
   * @author jun
   * @param items 해당 매장의 아이템인지 확인할 아이템들
   * @param shopId 매장 아이디
   * @return
   */
  public boolean isShopItems(List<OrderItemDTO> items, Long shopId) {
    return orderMapper.isShopItem(items, shopId);
  }
  
  /**
   * 주문 상태를 변경시킨다.
   * @author jun
   * @param orderId 주문 아이디
   * @param status 변경시킬 주문 상태
   */
  public void updateStatus(@NonNull Long orderId, OrderDTO.OrderStatus status) {
    orderMapper.updateStatus(orderId, status);
  }

  /**
   * 사장님 아이디를 기반으로 주문 정보를 조회한다.
   * @param ownerId 사장님 아이디
   * @return
   */
  public List<OrderBillDTO> getOwnerOrderRequest(String ownerId) {
    return orderMapper.findRequestByOwnerId(ownerId);
  }

  public boolean isOwnerOrder(String ownerId, Long orderId) {
    String ownerIdByOrderId = orderMapper.findOwnerIdByOrderId(orderId);
    return Objects.equals(ownerId, ownerIdByOrderId);
  }

  /**
   * 해당 주문을 승인하고 도착 예정시간을 설정한다.
   * 승인 완료 후 고객에게 푸시 메세지를 전송한다.
   * @author jun
   * @param orderId 주문 아이디
   * @param minute 배달까지 몇 분 걸릴지 예상시간
   */
  @Transactional
  public void orderApprove(Long orderId, long minute) {
    
    LocalDateTime exArrivalTime = LocalDateTime.now().plusMinutes(minute);
    orderMapper.updateOrderStatusAndExArrivalTime(orderId, exArrivalTime);
    String memberId = orderMapper.findMemberIdByOrderId(orderId);
    
    // 푸시메세지 전송
    PushMessage messageInfo = new PushMessage("DelFood 주문 승인",
        "사장님이 주문을 승인했어요! 도착 예정 시간 " + minute + "분 후");
    pushService.sendMessageToMember(messageInfo, memberId);
  }

  public OrderStatus getOrderStatus(Long orderId) {
    return orderMapper.getOrderStatus(orderId);
  }

  /**
   * 해당 주문에 라이더를 배치한다.
   * @param orderId 주문번호
   * @param riderId 라이더 아이디
   */
  public void setRider(Long orderId, String riderId) {
    log.info("주문번호 '{}'번에 라이더 '{}'가 매칭되었습니다.", orderId, riderId);
    orderMapper.updateRider(orderId, riderId);
  }

  public void completeOrder(@NonNull Long orderId, LocalDateTime completeTime) {
    log.info("주문번호 '{}'번 완료되었습니다.", orderId);
    orderMapper.updateStatusAndArrivalTime(orderId, completeTime);
  }


}
