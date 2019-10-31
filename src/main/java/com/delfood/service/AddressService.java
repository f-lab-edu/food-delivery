package com.delfood.service;

import com.delfood.controller.reqeust.GetAddressesRequest;
import com.delfood.dto.AddressDTO;
import com.delfood.mapper.AddressMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
  @Autowired
  AddressMapper addressMapper;


  public List<AddressDTO> getTownInfoByShopId(Long shopId) {
    return addressMapper.findByShopId(shopId);
  }


  public List<AddressDTO> getAddressByZipAddress(GetAddressesRequest searchInfo) {
    return addressMapper.findByZipAddress(searchInfo);
  }
}
