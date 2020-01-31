package com.delfood.dto.order.item;

import java.time.LocalDateTime;
import java.util.List;
import com.delfood.dto.order.OrderDTO.OrderStatus;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter 
@Setter
public class OrderItemOptionDTO {
  private String id;
  private Long optionId;
  private String optionName;
  private Long optionPrice;
  private String orderItemId;
}
