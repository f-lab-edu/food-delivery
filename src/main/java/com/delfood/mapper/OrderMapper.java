package com.delfood.mapper;

import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderDTO.OrderStatus;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
import com.delfood.dto.ItemsBillDTO.MenuInfo;
import com.delfood.dto.OrderBillDTO;
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
