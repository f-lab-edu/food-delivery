package com.delfood.service;

import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.address.AddressDTO;
import com.delfood.dto.address.Position;
import com.delfood.mapper.AddressMapper;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AddressService {
  public static final long DELIVERY_METER_DEFAULT = 1500L;
  public static final long DELIVERY_COST_DEFAULT = 2000L;
  public static final long DELIVERY_COST_PER_100M = 200L;
  
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
  
  /**
   * 주소코드를 이용하여 거리를 계산한다.
   * @param startAddressCode 시작 주소코드
   * @param endAddressCode 도착 주소코드
   * @return
   */
  public double getDistanceMeter(String startAddressCode, String endAddressCode) {
    Position startPosition = addressMapper.findPositionByAddressCode(startAddressCode);
    Position endPosition = addressMapper.findPositionByAddressCode(endAddressCode);
    
    return startPosition.distanceMeter(endPosition);
  }
  
  /**
   * 거리를 기반 배달료를 계산한다.
   * 
   * @author jun
   * @param distanceMeter 거리(미터 단위)
   * @return
   */
  public long deliveryPrice(double distanceMeter) {
    // 1.5KM까지는 기본료 2000원
    // 이후 100m마다 200원이 추가된다.
    // ex) 1500m = 2000원, 1600m = 2200원, 1650m = 2200원
    long extraCharge = distanceMeter <= 1500 ? 0 :
        (long) (distanceMeter - DELIVERY_METER_DEFAULT) / 100 * DELIVERY_COST_PER_100M;

    long deliveryCost = DELIVERY_COST_DEFAULT;
    
    return deliveryCost + extraCharge;
  }
  
  /**
   * 회원과 매장의 아이디를 기준으로 배달료를 계산한다.
   * @param memberId 회원 id
   * @param shopId 매장 id
   * @return
   */
  public long deliveryPrice(String memberId, Long shopId) {
    Position memberPosition = addressMapper.findPositionByMemberId(memberId);
    Position shopPosition = addressMapper.findPositionByShopId(shopId);    
    double distanceMeter = memberPosition.distanceMeter(shopPosition);
    return deliveryPrice(distanceMeter);
  }
}
