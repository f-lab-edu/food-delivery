package com.delfood.mapper;

import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
import com.delfood.dto.ItemsBillDTO.MenuInfo;
import com.delfood.dto.OrderBillDTO;
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

  List<OrderDTO> findByMemberId(String memberId);

  boolean isShopItem(List<OrderItemDTO> items, Long shopId);
}
