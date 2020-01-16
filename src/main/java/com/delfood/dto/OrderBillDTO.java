package com.delfood.dto;

import com.delfood.dto.ItemsBillDTO.MenuInfo;
import com.delfood.dto.OrderDTO.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderBillDTO {
  private Long orderId;
  private String memberId;
  private OrderStatus orderStatus;
  private LocalDateTime startTime;
  private SimpleCouponInfo couponInfo;
  private Long deliveryCost;
  private SimpleAddressInfo addressInfo;
  private List<MenuInfo> menus;
  
  @Getter
  public static class SimpleAddressInfo {
    private String buildingManagementNumber;
    private String cityName;
    private String cityCountryName;
    private String townName;
    private String roadName;
    private Integer buildingNumber;
    private Integer buildingSideNumber;
    private String addressDetail;
  }
  
}
