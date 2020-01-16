package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class ShopDTO {

  // 배달 타입. 자체배달, 라이더 매칭 배달
  public enum DeliveryType {
    SELF_DELIVERY, COMPANY_DELIVERY
  }


  public enum Status {
    DEFAULT, DELETED
  }

  // 즉시 결제, 만나서 결제
  public enum OrderType {
    THIS_PAYMENT, MEET_PAYMENT
  }

  public enum WorkCondition {
    OPEN, CLOSE
  }


  // 아아디
  @NonNull
  private Long id;

  // 가게 이름
  @NonNull
  private String name;

  // 배달형태
  private DeliveryType deliveryType;

  // 주력메뉴 치킨, 피자, 분식 등
  private Long signatureMenuId;

  // 가게 전화번호
  private String tel;

  // 주소 코드
  private String addressCode;
  
  // 상세 주소
  private String addressDetail;

  // 사업자번호
  private String bizNumber;

  // 가게 소개
  private String info;

  // 최소 주문금액
  private Long minOrderPrice;

  // 안내 및 혜택
  private String notice;

  // 운영 시간
  private String operatingTime;

  // 사장 아이디
  private String ownerId;

  // 가게 등록일
  private LocalDateTime createdAt;

  // 최종 수정일
  private LocalDateTime updatedAt;

  // 상태 삭제되었을시 DELETE 평소에는 DEFAULT
  private Status status;

  // 주문 타입 바로결제, 전화결제 등 결정
  private OrderType orderType;

  // 원산지 정보 원산지 표기정보를 작성
  private String originInfo;

  // 영업 상태 OPEN, CLOSED 등
  private WorkCondition workCondition;
  
  /**
   * 매장 입점 전 필수 입력 데이터가 누락된 것이 없는지 확인.
   * 
   * @author jun
   * @param shopInfo 매장 데이터
   * @return 누락된 데이터가 있다면 true
   */
  public static boolean hasNullDataBeforeCreate(ShopDTO shopInfo) {
    if (shopInfo.getName() == null || shopInfo.getDeliveryType() == null
        || shopInfo.getSignatureMenuId() == null || shopInfo.getBizNumber() == null
        || shopInfo.getInfo() == null || shopInfo.getMinOrderPrice() == null
        || shopInfo.getOrderType() == null || shopInfo.getTel() == null
        || shopInfo.getAddressCode() == null) {
      return true;
    }
    return false;
  }
}
