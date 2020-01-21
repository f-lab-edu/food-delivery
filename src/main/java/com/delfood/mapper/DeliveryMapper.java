package com.delfood.mapper;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.delfood.dto.rider.DeliveryInfoDTO;
import com.delfood.dto.rider.DeliveryOrderInfo;

@Repository
public interface DeliveryMapper {

  List<DeliveryInfoDTO> findByRiderId(String riderId, Long lastViewedOrderId);

  DeliveryInfoDTO findCurrentDeliveryByRiderId(String riderId);

  List<DeliveryOrderInfo> findTodayBillsByRiderId(String riderId, LocalDate today);
}
