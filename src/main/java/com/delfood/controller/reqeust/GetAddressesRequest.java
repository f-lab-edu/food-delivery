package com.delfood.controller.reqeust;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetAddressesRequest {
  private String townName;
  private Integer buildingNumber;
  private Integer buildingSideNumber;
  private String buildingNameForCity;
  private String roadName;
  private String lastSearchBuildingManagementNumber;
}
