package com.delfood.mapper;

import org.springframework.stereotype.Repository;
import com.delfood.dto.PaymentDTO;

@Repository
public interface PaymentMapper {
  public Long insertPayment(PaymentDTO paymentInfo);
}
