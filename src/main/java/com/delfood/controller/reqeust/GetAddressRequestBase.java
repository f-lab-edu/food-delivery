package com.delfood.controller.reqeust;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GetAddressRequestBase {
  protected Integer buildingNumber;
  protected Integer buildingSideNumber;
  protected String buildingNameForCity;
  protected String lastSearchBuildingManagementNumber;
  
  protected abstract String generateKey();
  
  /**
   * 캐싱을 진행할 키를 제작한다.
   * @author jun
   * @return
   */
  public String getKey() {
    return "buildingNumber:" + this.buildingNumber + "/"
        + "buildingSideNumber:" + this.buildingSideNumber + "/"
        + "buildingNameForCity" + this.buildingNameForCity + "/"
        + "lastSearchBuildingManagementNumber" + this.lastSearchBuildingManagementNumber + "/"
        + generateKey();
  }
}
