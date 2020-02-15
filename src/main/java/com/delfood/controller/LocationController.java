package com.delfood.controller;

import com.delfood.aop.OwnerShopCheck;
import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.address.AddressDTO;
import com.delfood.dto.address.DeliveryLocationDTO;
import com.delfood.service.AddressService;
import com.delfood.service.ShopService;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locations/")
public class LocationController {
  @Autowired
  private ShopService shopService;

  @Autowired
  private AddressService addressService;

  /**
   * 매장의 배달 가능 지역을 추가한다. 배달 가능 지역에 포함되어 있는 사용자에게 검색이 된다. 클라이언트에서는 요청 전 중복된 배달지역이 있는지 체크해야한다. 체크하지 않은
   * 상태로 중복된 추가 요청을 보낼 경우 예외처리를 진행한다.
   * 
   * @param shopId 배달 지역을 추가할 매장의 아이디
   * @param addDeliveryLocationRequest 추가할 지역 리스트
   * @return
   */
  @PostMapping("deliveries/{shopId}/possibles")
  @OwnerShopCheck("shopId")
  @ResponseStatus(HttpStatus.CREATED)
  public void addDeliveryLocation(
      @PathVariable(name = "shopId") Long shopId,
      @RequestBody(required = true) AddDeliveryLocationRequest addDeliveryLocationRequest) {
    Set<String> townCodes = addDeliveryLocationRequest.getTownCodes();
    shopService.addDeliveryLocation(shopId, townCodes);
  }

  /**
   * 매장의 배달가능지역을 조회한다.
   * 
   * @author jun
   * @param shopId 배달가능 지역을 조회할 매장의 id
   * @return
   */
  @GetMapping("deliveries/{shopId}/possibles")
  @OwnerShopCheck("shopId")
  public List<DeliveryLocationDTO> getDeliveryLocations(
      @PathVariable(name = "shopId") Long shopId) {
    return shopService.getDeliveryLocations(shopId);
  }


  /**
   * 배달 지역 삭제.
   * 
   * @author jun
   * @param deliveryLocationId 삭제할 배달 지역 id
   * @param session 접속한 사용자의 세션
   * @return
   */
  @DeleteMapping("deliveries/{shopId}/possibles/{deliveryLocationId}")
  @OwnerShopCheck("shopId")
  public void deleteDeliveryLocation(
      @PathVariable(value = "shopId") Long shopId,
      @PathVariable(value = "deliveryLocationId") Long deliveryLocationId,
      HttpSession session) {
    shopService.deleteDeliveryLocation(deliveryLocationId);
  }

  /**
   * 도로명 주소를 검색한다.
   * 
   * @author jun
   * @param requestInfo 검색할  도로명 주소 정보.
   * @return
   */
  @GetMapping("address/road")
  public List<AddressDTO> getAddressByRoadInfo(
      GetAddressesByRoadRequest requestInfo) {
    List<AddressDTO> addresses = addressService.getAddressByRoadName(requestInfo);
    return addresses;
  }
  
  
  /**
   * 지번 주소를 검색한다.
   * 
   * @author jun
   * @param requestInfo 검색할 지번 주소 정보.
   * @return
   */
  @GetMapping("address/zip")
  public List<AddressDTO> getAddressByZipInfo(
      GetAddressByZipRequest requestInfo) {
    List<AddressDTO> addresses = addressService.getAddressByZipAddress(requestInfo);
    return addresses;
  }



  // ---------------------- Request 객체 ----------------------
  @Getter
  @Setter
  private static class AddDeliveryLocationRequest {
    @NonNull
    private Set<String> townCodes;
  }


}
