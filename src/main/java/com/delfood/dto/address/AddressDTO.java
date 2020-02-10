package com.delfood.dto.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddressDTO {
  // 주소관할읍면동코드
  private String townCode;

  // 시도명
  private String cityName;

  // 시군구명
  private String cityCountryName;

  // 읍면동명
  private String townName;

  // 도로명코드
  private String roadNameCode;

  // 도로명
  private String roadName;

  // 지하여부
  private String undergroundStatus;

  // 건물본번
  private Integer buildingNumber;

  // 건물부번
  private Integer buildingSideNumber;

  // 우편번호
  private String zipCode;

  // 건물관리번호
  private String buildingManagementNumber;

  // 시군구용건물명
  private String buildingNameForCity;

  // 건축물용도분류
  private String buildingUseClassification;

  // 행정동코드
  private String administrativeTownCode;

  // 행정동명
  private String administrativeTownName;

  // 지상층수
  private Integer groundFloorNumber;

  // 지하층수
  private Integer undergroundFloorNumber;

  // 공동주택구분
  private String classificationApartmentBuildings;

  // 건물수
  private Integer buildingCount;

  // 상세건물명
  private String detailBuildingName;

  // 건물명변경이력
  private String buildingNameChangeHistory;

  // 상세건물명변경이력
  private String detailBuildingNameChangeHistory;

  // 거주여부
  private String livingStatus;

  // 건물중심점_x좌표
  private Double buildingCenterPointXCoordinate;

  // 건물중심점_y좌표
  private Double buildingCenterPointYCoordinate;

  // 출입구_x좌표
  private Double exitXCoordinate;

  // 출입구_y좌표
  private Double exitYCoordinate;

  // 시도명_영문
  private String cityNameEng;

  // 시군구명_영문
  private String cityCountryNameEng;

  // 읍면동명_영문
  private String townNameEng;

  // 도로명_영문
  private String roadNameEng;

  // 읍면동구분
  private String townMobileClassification;

  // 이동사유코드
  private String mobileReasonCode;
}
