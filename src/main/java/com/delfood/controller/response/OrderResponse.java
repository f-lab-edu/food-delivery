package com.delfood.controller.response;

import com.delfood.dto.ItemsBillDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
  ItemsBillDTO bill;
  Long orderId;
}
