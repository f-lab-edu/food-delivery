package com.delfood.controller.reqeust;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class GetAddressesByRoadRequest extends GetAddressRequestBase {
  @NotNull
  private String roadName;
  
  protected String generateKey() {
    return "roadName" + roadName;
  }
}
