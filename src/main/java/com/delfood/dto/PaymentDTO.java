package com.delfood.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

@Getter
public class PaymentDTO {
  private Long id;
  @NotNull
  private Type type;
  @NotNull
  private Long amountPayment;
  private LocalDateTime payTime;
  @NotNull
  private Long orderId;
  @NotNull
  private Status status;
  @NotNull
  private Long amountDiscount;
  
  // 성공시킬지 실패시킬지 결정하는 필드
  private boolean doSuccess;
  
  /**
   * payTime, status는 자동으로 입력된다.
   * @param type 결제 타입
   * @param amountPayment 실제 결제 가격
   * @param orderId 주문 아이디
   * @param amountDiscount 할인된 가격
   */
  @Builder
  public PaymentDTO(@NonNull Type type, 
      @NonNull Long amountPayment, 
      @NonNull Long orderId,
      @NonNull Long amountDiscount,
      @Nullable Status status) {
    this.type = type;
    this.amountPayment = amountPayment;
    this.orderId = orderId;
    this.amountDiscount = amountDiscount;
    
    this.doSuccess = true;
    this.payTime = LocalDateTime.now();
    this.status = status == null ?  Status.READY : status;
  }
  
  private void success(Status status) {
    this.status = status;
  }
  
  public void doFail() {
    doSuccess = false;
  }
  
  public void doSuccess() {
    doSuccess = true;
  }

  enum Type {
    CARD, CASH
  }
  
  enum Status {
    READY, SUCCESS, FAIL
  }
  
  /**
   * 성공적으로 결제가 이루어 질 때 이 메소드를 호출한 후 리턴값을 사용한다.
   * 해당 메소드가 반환하는 값은 인스턴스의 복제 인스턴스이다.
   * 복제 인스턴스의 'status' 컬럼만 SUCCESS로 변한 채로 반환된다.
   * @author jun
   * @return
   */
  public PaymentDTO pay() {
    PaymentDTO pay = PaymentDTO.builder()
        .type(getType())
        .amountPayment(getAmountPayment())
        .orderId(getOrderId())
        .amountDiscount(getAmountDiscount())
        .status(Status.SUCCESS)
        .build();
    return pay;
  }
}
