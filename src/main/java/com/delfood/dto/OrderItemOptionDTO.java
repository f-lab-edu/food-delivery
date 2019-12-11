package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.delfood.dto.OrderDTO.OrderStatus;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter 
@Setter
public class OrderItemOptionDTO {
  private Long id;
  private Long optionId;
  private Long orderItemId;
}
