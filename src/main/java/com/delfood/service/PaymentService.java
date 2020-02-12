package com.delfood.service;

import com.delfood.dto.pay.PaymentDTO;
import com.delfood.mapper.PaymentMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class PaymentService {
  @Autowired
  private PaymentMapper paymentMapper;
  
  @Transactional
  public void insertPayment(PaymentDTO paymentInfo) {
    long result = paymentMapper.insertPayment(paymentInfo);
    if (result != 1) {
      log.error("결제 입력 오류. 결제 정보 : {}", paymentInfo);
      throw new RuntimeException("결제 입력 오류!");
    }
  }
}
