package com.delfood.service;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.PaymentDTO;
import com.delfood.error.exception.mockPay.MockPayException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@MemberLoginCheck
public class MockPayService {
  
  /**
   * 결제 서버에 결제를 요청한다.
   * 결제 성공시 상태값이 SUCCESS로 세팅된 복제 인스턴스가 반환된다.
   * 결제 실패시 예외를 발생시킨다.
   * 
   * @author jun
   * @param payment 결제 정보
   */
  public PaymentDTO pay(@NonNull PaymentDTO payment) {
    if (payment.isDoSuccess() == false) {
      throw new MockPayException("결제 진행을 실패하였습니다.");
    }
    return payment.pay();
    
  }
}
