package com.delfood.dao.deliveery;

import com.delfood.dto.address.Position;
import com.delfood.dto.rider.DeliveryRiderDTO;
import java.util.List;

public interface DeliveryDao {
  
  void updateRiderInfo(DeliveryRiderDTO riderInfo);

  long deleteRiderInfo(String riderId);

  boolean hasRiderInfo(String riderId);

  void deleteNonUpdatedRiders();

  DeliveryRiderDTO getRiderInfo(String riderId);

  List<DeliveryRiderDTO> toList();

  long deleteAll(List<String> idList);
  
}
