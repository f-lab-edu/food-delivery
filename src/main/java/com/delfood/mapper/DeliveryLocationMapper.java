package com.delfood.mapper;

import org.springframework.stereotype.Repository;
import com.delfood.dto.DeliveryLocationDTO;

@Repository
public interface DeliveryLocationMapper {
  DeliveryLocationDTO findById(Long id);
}
