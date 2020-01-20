package com.delfood.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delfood.dto.rider.DeliveryInfoDTO;

@Repository
public interface DeliveryMapper {

  List<DeliveryInfoDTO> findByRiderId(String riderId, Long lastViewedOrderId);

  DeliveryInfoDTO findCurrentDeliveryByRiderId(String riderId);
}
