package com.delfood.dto.order.item;

import java.time.LocalDateTime;
import java.util.List;
import com.delfood.dto.order.OrderDTO.OrderStatus;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
  private String id;
  private Long menuId;
  private String menuName;
  private Long menuPrice;
  private Long orderId;
  private Long count;
  private List<OrderItemOptionDTO> options;
}
