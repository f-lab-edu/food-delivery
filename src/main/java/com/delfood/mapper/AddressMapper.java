package com.delfood.mapper;

import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.AddressDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressMapper {
  public List<AddressDTO> findByShopId(Long shopId);

  public List<AddressDTO> findByZipName(GetAddressByZipRequest searchInfo);

  public List<AddressDTO> findByRoadName(GetAddressesByRoadRequest searchInfo);
}
