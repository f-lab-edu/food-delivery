package com.delfood.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.delfood.aop.OwnerLoginCheck;
import com.delfood.aop.OwnerShopCheck;
import com.delfood.dto.AddressDTO;
import com.delfood.dto.DeliveryLocationDTO;
import com.delfood.dto.OwnerDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopUpdateDTO;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/owners/shops/")
@Log4j2
public class ShopController {
  @Autowired
  private ShopService shopService;

  /**
   * 입점 메서드.
   * 
   * @author jun
   * @param session 로그인한 사장님 정보를 불러오기 위한 세션
   * @param shopInfo 입력받은 매장 정보
   * @return
   */
  @PostMapping
  @OwnerLoginCheck
  public ResponseEntity<AddShopResponse> addShop(HttpSession session,
      @RequestBody ShopDTO shopInfo) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    shopInfo.setOwnerId(ownerId);

    // 입력한 데이터 중 필수 데이터가 null일 경우 400 에러코드를 반환한다.
    if (ShopDTO.hasNullDataBeforeCreate(shopInfo)) {
      return AddShopResponse.NULL_ARGUMENTS_RESPONSE;
    }

    shopService.addShop(shopInfo);


    return AddShopResponse.CREATED_RESPONSE;
  }

  /**
   * 사장님이 가진 매장들을 불러온다.
   * 
   * @param myShopsRequest 페이징 정보
   * @param session 사용자의 세션
   * @return 페이지에 따른 사장님 매장, 총 매장 개수
   */
  @GetMapping
  @OwnerLoginCheck
  public ResponseEntity<MyShopsResponse> myShops(MyShopsRequest myShopsRequest,
      HttpSession session) {
    String id = SessionUtil.getLoginOwnerId(session);

    List<ShopDTO> myShops = shopService.getMyShops(id, myShopsRequest.getLastId());
    long myShopCount = shopService.getMyShopCount(id);
    return new ResponseEntity<MyShopsResponse>(MyShopsResponse.success(myShops, myShopCount),
        HttpStatus.OK);
  }

  /**
   * 매장 정보 업데이트.
   * 
   * @author jun
   * @param updateInfo 변경할 정보를 담은 DTO
   * @param session 사용자의 세션
   * @return
   */
  @PatchMapping("{id}")
  @OwnerShopCheck
  public void updateShop(@PathVariable(required = true) Long id,
      @RequestBody(required = true) final ShopUpdateDTO updateInfo, HttpSession session) {
    final ShopUpdateDTO copyData = ShopUpdateDTO.copyWithId(updateInfo, id);
    shopService.updateShop(copyData);
  }

  /**
   * 매장을 오픈한다.
   * 이미 오픈중인 매장을 오픈하려고 시도할 시 에러를 발생시킨다.
   * 
   * @author jun
   * @param id 오픈할 매장의 id
   * @param session 사용자의 세션
   * @return
   */
  @PatchMapping("open/{id}")
  @OwnerShopCheck
  public ShopDTO openShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {
    shopService.openShop(id);
    return shopService.getShop(id);
  }

  /**
   * 오픈할 수 있는 사장님의 모든 매장을 오픈한다.
   * 본인의 매장중 오픈 가능한 매장을 조회한 후, 순차적으로 매장 오픈을 시도한다.
   * 
   * @author jun
   * @param session 사용자의 세션
   * @return 오픈한 매장의 id, 이름
   */
  @PatchMapping("open/")
  @OwnerLoginCheck
  public List<ShopDTO> openAllShops(HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    List<ShopDTO> openShops = shopService.openAllShops(ownerId);
    return openShops;
  }

  /**
   * 매장을 닫는다.
   * 오픈하지 않은 매장에 마감을 시도하면 에러를 발생시킨다.
   * 
   * @author jun
   * @param id 닫을 매장의 id
   * @param session 사용자의 세션
   * @return
   */
  @PatchMapping("close/{id}")
  @OwnerShopCheck
  public void closeShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {
    shopService.closeShop(id);
  }

  /**
   * 현재 오픈중인 모든 매장을 닫는다.
   * 오픈하지 않은 매장을 조회한 후 순차적으로 마감을 시도한다.
   * 
   * @author jun
   * @param session 접속한 사용자의 세션
   * @return 운영 종료를 진행한 매장의 id, 이름
   */
  @PatchMapping("close/")
  @OwnerLoginCheck
  public List<ShopDTO> closeAllShops(HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    return shopService.closeAllShops(ownerId);
  }

  /**
   * 매장 정보를 조회한다.
   * 
   * @param shopId 조회할 매장의 id
   * @param session 사용자의 세션
   * @return
   */
  @GetMapping("{shopId}")
  @OwnerShopCheck
  public ShopInfoResponse shopInfo(
      @PathVariable(value = "shopId", required = true) Long shopId, HttpSession session) {
    ShopDTO shopInfo = shopService.getShop(shopId);
    List<DeliveryLocationDTO> deliveryLocations = shopService.getDeliveryLocations(shopId);
    return new ShopInfoResponse(shopInfo, deliveryLocations);
  }

  // Response 객체
  @Getter
  private static class AddShopResponse {
    enum Message {
      NULL_ARGUMENTS
    }

    @NonNull
    Message message;

    private static final AddShopResponse NULL_ARGUMENTS =
        new AddShopResponse(Message.NULL_ARGUMENTS);
    
    public static final ResponseEntity<AddShopResponse> CREATED_RESPONSE = 
        new ResponseEntity<>(HttpStatus.CREATED);
    
    public static final ResponseEntity<AddShopResponse> NULL_ARGUMENTS_RESPONSE =
        new ResponseEntity<>(NULL_ARGUMENTS, HttpStatus.BAD_REQUEST);

    public AddShopResponse(Message message) {
      this.message = message;
    }
  }

  @Getter
  @AllArgsConstructor
  private static class MyShopsResponse {

    List<ShopDTO> myShops;

    Long shopCount;

    public static MyShopsResponse success(List<ShopDTO> myShops, Long shopCount) {
      return new MyShopsResponse(myShops, shopCount);
    }
  }



  @Getter
  @AllArgsConstructor
  private static class OpenAllShopsResponse {
    List<ShopDTO> openShops;
  }



  @Getter
  @AllArgsConstructor
  private static class ShopInfoResponse {
    private ShopDTO shopInfo;
    private List<DeliveryLocationDTO> deliveryLocations;
  }

  @Getter
  @AllArgsConstructor
  private static class CloseAllShopsResponse {
    private List<ShopDTO> closeShops;
  }

  // Request 객체
  @Getter
  @Setter
  private static class MyShopsRequest {
    @Nullable
    private Long lastId;
  }



}
