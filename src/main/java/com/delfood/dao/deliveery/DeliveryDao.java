package com.delfood.dao.deliveery;

import com.delfood.dto.OrderDTO.OrderStatus;
import com.delfood.dto.address.Position;
import com.delfood.dto.rider.DeliveryRiderDTO;
import java.util.List;

public interface DeliveryDao {
  
  void updateRiderInfo(DeliveryRiderDTO riderInfo);

  boolean deleteRiderInfo(String riderId);

  boolean hasRiderInfo(String riderId);

  void deleteNonUpdatedRiders();

  DeliveryRiderDTO getRiderInfo(String riderId);

  List<DeliveryRiderDTO> getRiderList();

  void deleteAll(List<String> idList);

  OrderStatus getOrderStatus(Long orderId);
  
  void setOrderStatus(Long orderId, OrderStatus status);
  
  void deleteOrderStatus(Long orderId);
}
