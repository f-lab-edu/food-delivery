package com.delfood.controller.reqeust;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAddressByZipRequest extends GetAddressRequestBase {
  @NotNull
  private String townName;
  
  
  protected String generateKey() {
    return "townName:" + this.townName;
  }
}
