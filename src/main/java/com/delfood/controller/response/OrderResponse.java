package com.delfood.controller.response;

import com.delfood.dto.order.bill.ItemsBillDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
  ItemsBillDTO bill;
  Long orderId;
}
