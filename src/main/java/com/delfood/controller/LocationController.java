package com.delfood.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.delfood.aop.OwnerShopCheck;
import com.delfood.controller.reqeust.GetAddressesRequest;
import com.delfood.dto.AddressDTO;
import com.delfood.service.AddressService;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/locations/")
public class LocationController {
  @Autowired
  private ShopService shopService;
  
  @Autowired
  private AddressService addressService;
  
  
  
  /**
   * 매장의 배달 가능 지역을 추가한다. 배달 가능 지역에 포함되어 있는 사용자에게 검색이 된다.
   * 
   * @author jun
   * @param deliveryLocationInfo 새로운 배달지역 정보
   * @param session 사용자의 세션
   * @return
   */
  @PostMapping("deliveries/possibles")
  public ResponseEntity<AddDeliveryLocationResponse> addDeliveryLocation(
      @RequestBody(required = true) AddDeliveryLocationRequest deliveryLocationInfo,
      HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    Long shopId = deliveryLocationInfo.getShopId();
    String townCode = deliveryLocationInfo.getTownCode();

    // 로그인 하지 않았을시
    if (ownerId == null) {
      return new ResponseEntity<AddDeliveryLocationResponse>(
          AddDeliveryLocationResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    }
    // 로그인한 사장님이 해당 가게의 사장님이 아닐 시
    if (shopService.isShopOwner(shopId, ownerId) == false) {
      return new ResponseEntity<AddDeliveryLocationResponse>(
          AddDeliveryLocationResponse.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    // 핵심 로직
    shopService.addDeliveryLocation(shopId, townCode);


    return new ResponseEntity<AddDeliveryLocationResponse>(
        AddDeliveryLocationResponse.SUCCESS, HttpStatus.CREATED);
  }
  
  
  /**
   * 배달 지역 삭제.
   * 
   * @author jun
   * @param id 삭제할 배달 지역 id
   * @param session 접속한 사용자의 세션
   * @return
   */
  @DeleteMapping("deliveries/{id}")
  @OwnerShopCheck
  public ResponseEntity<DeleteDeliveryLocationResponse> deleteDeliveryLocation(
      @PathVariable(required = true, value = "id") Long id, HttpSession session) {
    shopService.deleteDeliveryLocation(id);


    return new ResponseEntity<DeleteDeliveryLocationResponse>(
        DeleteDeliveryLocationResponse.SUCCESS, HttpStatus.OK);
  }
  
  /**
   * 주소를 검색한다.
   * @author jun
   * @param requestInfo
   * @return
   */
  @GetMapping("address")
  public ResponseEntity<GetAddressesByZipInfo> getAddressByZipInfo(GetAddressesRequest requestInfo) {
    List<AddressDTO> addresses = addressService.getAddressByZipAddress(requestInfo);
    return new ResponseEntity<LocationController.GetAddressesByZipInfo>(new GetAddressesByZipInfo(addresses), HttpStatus.OK);
  }
  
  
  
  
  
  
  // ---------------------- Request 객체 ----------------------
  @Getter
  @Setter
  private static class AddDeliveryLocationRequest {
    @NonNull
    private Long shopId;
    @NonNull
    private String townCode;
  }
  
  
  
  
  
  // ---------------------- Response 객체 ----------------------
  @Getter
  @RequiredArgsConstructor
  private static class AddDeliveryLocationResponse {
    enum Result {
      SUCCESS, NO_LOGIN, UNAUTHORIZED
    }

    @NonNull
    private Result result;
    private static final AddDeliveryLocationResponse SUCCESS =
        new AddDeliveryLocationResponse(Result.SUCCESS);
    private static final AddDeliveryLocationResponse NO_LOGIN =
        new AddDeliveryLocationResponse(Result.NO_LOGIN);
    private static final AddDeliveryLocationResponse UNAUTHORIZED =
        new AddDeliveryLocationResponse(Result.UNAUTHORIZED);
  }
  
  
  @Getter
  @RequiredArgsConstructor
  private static class DeleteDeliveryLocationResponse {
    enum Result {
      SUCCESS, NO_LOGIN, UNAUTHORIZED
    }

    @NonNull
    private Result result;
    private static final DeleteDeliveryLocationResponse SUCCESS =
        new DeleteDeliveryLocationResponse(Result.SUCCESS);
    private static final DeleteDeliveryLocationResponse NO_LOGIN =
        new DeleteDeliveryLocationResponse(Result.NO_LOGIN);
    private static final DeleteDeliveryLocationResponse UNAUTHORIZED =
        new DeleteDeliveryLocationResponse(Result.UNAUTHORIZED);
  }
  
  @Getter
  @AllArgsConstructor
  private static class GetAddressesByZipInfo{
    private List<AddressDTO> addresses;
  }
}
