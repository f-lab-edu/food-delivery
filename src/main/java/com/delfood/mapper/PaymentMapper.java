package com.delfood.mapper;

import org.springframework.stereotype.Repository;
import com.delfood.dto.pay.PaymentDTO;

@Repository
public interface PaymentMapper {
  public Long insertPayment(PaymentDTO paymentInfo);
}
