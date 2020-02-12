package com.delfood.mapper;

import com.delfood.dto.order.OrderDTO;
import com.delfood.dto.order.OrderDTO.OrderStatus;
import com.delfood.dto.order.bill.OrderBillDTO;
import com.delfood.dto.order.bill.ItemsBillDTO.MenuInfo;
import com.delfood.dto.order.item.OrderItemDTO;
import com.delfood.dto.order.item.OrderItemOptionDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;

public interface OrderMapper {
  // 파라미터로 넘어온 인스턴스값을 세팅한다
  Long addOrder(OrderDTO orderInfo);
  
  Long addOrderItem(OrderItemDTO item);
  
  OrderDTO findById(Long id);

  Long addOrderItems(List<OrderItemDTO> items);

  Long addOrderItemOptions(List<OrderItemOptionDTO> options);

  OrderBillDTO findOrderBill(Long orderId);

  long findItemsPrice(List<OrderItemDTO> items);

  List<MenuInfo> findItemsBill(List<OrderItemDTO> items);

  List<OrderDTO> findByMemberId(String memberId, Long lastViewedOrderId);

  boolean isShopItem(List<OrderItemDTO> items, Long shopId);

  void updateStatus(@NonNull Long orderId, OrderStatus status);

  List<OrderBillDTO> findRequestByOwnerId(String shopId);

  String findOwnerIdByOrderId(Long orderId);

  void updateOrderStatusAndExArrivalTime(Long orderId, LocalDateTime exArrivalTime);

  String findMemberIdByOrderId(Long orderId);

  OrderStatus getOrderStatus(Long orderId);

  void updateRider(Long orderId, String riderId);

  void updateStatusAndArrivalTime(@NonNull Long orderId, LocalDateTime completeTime);
}
