package com.delfood.service;

import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.AddressDTO;
import com.delfood.mapper.AddressMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
  @Autowired
  AddressMapper addressMapper;


  public List<AddressDTO> getTownInfoByShopId(Long shopId) {
    return addressMapper.findByShopId(shopId);
  }

  @Cacheable(value = "ADDRESS_SERCH_ZIP", key = "#searchInfo.getKey()")
  public List<AddressDTO> getAddressByZipAddress(GetAddressByZipRequest searchInfo) {
    return addressMapper.findByZipName(searchInfo);
  }

  @Cacheable(value = "ADDRESS_SERCH_ROAD", key = "#searchInfo.getKey()")
  public List<AddressDTO> getAddressByRoadName(GetAddressesByRoadRequest searchInfo) {
    return addressMapper.findByRoadName(searchInfo);
  }
}
