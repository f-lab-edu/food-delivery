package com.delfood.mapper;

import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.AddressDTO;
import com.delfood.dto.address.Position;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressMapper {
  public List<AddressDTO> findByShopId(Long shopId);

  public List<AddressDTO> findByZipName(GetAddressByZipRequest searchInfo);

  public List<AddressDTO> findByRoadName(GetAddressesByRoadRequest searchInfo);

  public Position findPositionByMemberId(String memberId);

  public Position findPositionByShopId(Long shopId);

  public Position findPositionByAddressCode(String addressCode);
}
