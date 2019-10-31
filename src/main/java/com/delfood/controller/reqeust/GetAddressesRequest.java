package com.delfood.controller.reqeust;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAddressesRequest {
  private String cityName;
  private String cityCountryName;
  private String townName;
  private Integer buildingNumber;
  private Integer buildingSideNumber;
  private String buildingNameForCity;
  private String roadName;
  private String lastSearchBuildingManagementNumber;
}
