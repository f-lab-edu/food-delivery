package com.delfood.mapper;

import com.delfood.dto.AddressDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressMapper {
  public List<AddressDTO> findByShopId(Long shopId);
}
