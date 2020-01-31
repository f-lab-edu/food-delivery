package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import com.delfood.dto.PaymentDTO;
import com.delfood.dto.PaymentDTO.Status;
import com.delfood.dto.PaymentDTO.Type;
import com.delfood.error.exception.mockPay.MockPayException;

public class MockPayServiceTest {
  
  MockPayService service;
  
  @Before
  public void init() {
    service = new MockPayService();
  }
  
  
  
  @Test
  public void mockPayTest_가상결제_성공_테스트() {
    PaymentDTO payInfo = PaymentDTO.builder().amountDiscount(0L).amountPayment(10000L).orderId(1L)
        .type(Type.CARD).build();
    
    assertThat(payInfo.getStatus()).isEqualTo(Status.READY);
    assertThat(service.pay(payInfo).getStatus()).isEqualTo(Status.SUCCESS);
  }
  
  @Test(expected = MockPayException.class)
  public void mockPayFailTest_가상결제_실패_테스트() {
    PaymentDTO payInfo = PaymentDTO.builder().amountDiscount(0L).amountPayment(10000L).orderId(1L)
        .type(Type.CARD).build();
    payInfo.doFail();
    
    assertThat(service.pay(payInfo));
  }
  

}
