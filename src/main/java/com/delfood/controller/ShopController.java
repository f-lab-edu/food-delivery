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
import com.delfood.controller.response.CommonResponse;
import com.delfood.dto.AddressDTO;
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
  public ResponseEntity<CommonResponse> addShop(HttpSession session,
      @RequestBody ShopDTO shopInfo) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    shopInfo.setOwnerId(ownerId);

    // 입력한 데이터 중 필수 데이터가 null일 경우 400 에러코드를 반환한다.
    if (ShopDTO.hasNullDataBeforeCreate(shopInfo)) {
      return new ResponseEntity<CommonResponse>(AddShopResponse.NULL_ARGUMENTS,
          HttpStatus.BAD_REQUEST);
    }

    shopService.addShop(shopInfo);


    return new ResponseEntity<CommonResponse>(AddShopResponse.SUCCESS, HttpStatus.CREATED);
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
  public ResponseEntity<CommonResponse> myShops(MyShopsRequest myShopsRequest,
      HttpSession session) {
    String id = SessionUtil.getLoginOwnerId(session);

    List<ShopDTO> myShops = shopService.getMyShops(id, myShopsRequest.getLastId());
    long myShopCount = shopService.getMyShopCount(id);
    return new ResponseEntity<CommonResponse>(MyShopsResponse.success(myShops, myShopCount),
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
  public ResponseEntity<CommonResponse> updateShop(@PathVariable(required = true) Long id,
      @RequestBody(required = true) final ShopUpdateDTO updateInfo, HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    final ShopUpdateDTO copyData = ShopUpdateDTO.copyWithId(updateInfo, id);

    if (shopService.isShopOwner(copyData.getId(), ownerId) == false) {
      return new ResponseEntity<CommonResponse>(UpdateShopResponse.UNAUTHORIZED,
          HttpStatus.UNAUTHORIZED);
    }

    shopService.updateShop(copyData);


    return new ResponseEntity<CommonResponse>(UpdateShopResponse.SUCCESS, HttpStatus.OK);
  }

  /**
   * 매장을 오픈한다.
   * 
   * @author jun
   * @param id 오픈할 매장의 id
   * @param session 사용자의 세션
   * @return
   */
  @PatchMapping("open/{id}")
  @OwnerShopCheck
  public ResponseEntity<CommonResponse> openShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {

    // 매장이 오픈중일 때
    if (shopService.notOpenCheck(id) == false) {
      return new ResponseEntity<CommonResponse>(OpenShopResponse.IS_OPEN, HttpStatus.BAD_REQUEST);
    }


    shopService.openShop(id);

    return new ResponseEntity<CommonResponse>(OpenShopResponse.SUCCESS, HttpStatus.OK);
  }
  
  /**
   * 오픈할 수 있는 사장님의 모든 매장을 오픈한다.
   * @author jun
   * @param session
   * @return 오픈한 매장의 id, 이름
   */
  @PatchMapping("open/")
  @OwnerLoginCheck
  public ResponseEntity<CommonResponse> openAllShops(HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    List<ShopDTO> openShops =  shopService.openAllShops(ownerId);
    return new ResponseEntity<CommonResponse>(new OpenAllShopsResponse(openShops), HttpStatus.OK);
  }

  /**
   * 매장을 닫는다.
   * 
   * @author jun
   * @param id 닫을 매장의 id
   * @param session 사용자의 세션
   * @return
   */
  @PatchMapping("close/{id}")
  @OwnerShopCheck
  public ResponseEntity<CommonResponse> closeShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {

    // 해당 매장이 영업중이 아닐시
    if (shopService.notOpenCheck(id) == true) {
      return new ResponseEntity<CommonResponse>(CloseShopResponse.NOT_OPEN, HttpStatus.BAD_REQUEST);
    }

    shopService.closeShop(id);

    return new ResponseEntity<CommonResponse>(CloseShopResponse.SUCCESS, HttpStatus.OK);
  }

  /**
   * 현재 오픈중인 모든 매장을 닫는다.
   * @author jun
   * @param session 접속한 사용자의 세션
   * @return 운영 종료를 진행한 매장의 id, 이름
   */
  @PatchMapping("close/")
  @OwnerLoginCheck
  public ResponseEntity<CommonResponse> closeAllShops(HttpSession session) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    List<ShopDTO> closeShops = shopService.closeAllShops(ownerId);
    return new ResponseEntity<CommonResponse>(new closeAllShopsResponse(closeShops),
        HttpStatus.OK);
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
  public ResponseEntity<ShopInfoResponse> shopInfo(
      @PathVariable(value = "shopId", required = true) Long shopId, HttpSession session) {
    ShopDTO shopInfo = shopService.getShop(shopId);
    List<AddressDTO> deliveryLocations = shopService.getDeliveryLocations(shopId);

    return new ResponseEntity<ShopController.ShopInfoResponse>(
        new ShopInfoResponse(shopInfo, deliveryLocations), HttpStatus.OK);
  }



  // Response 객체
  @Getter
  private static class AddShopResponse extends CommonResponse {
    enum Message {
      NULL_ARGUMENTS
    }

    @NonNull
    Message message;

    private static final AddShopResponse NULL_ARGUMENTS =
        new AddShopResponse(Message.NULL_ARGUMENTS);

    public AddShopResponse(Message message) {
      super(Result.FAIL);
      this.message = message;
    }
  }

  @Getter
  @AllArgsConstructor
  private static class MyShopsResponse extends CommonResponse {

    List<ShopDTO> myShops;

    Long shopCount;

    public static MyShopsResponse success(List<ShopDTO> myShops, Long shopCount) {
      return new MyShopsResponse(myShops, shopCount);
    }
  }

  @Getter
  private static class UpdateShopResponse extends CommonResponse {
    // 해당 Response는 AOP로 통합되었습니다.
    // 추후 확장성을 고려하여 남겨놓습니다. - jun
  }

  @Getter
  private static class OpenShopResponse extends CommonResponse {
    enum Message {
      AREADY_OPEN
    }

    @NonNull
    private Message message;
    private static final OpenShopResponse IS_OPEN = new OpenShopResponse(Message.AREADY_OPEN);

    public OpenShopResponse(Message message) {
      super(Result.FAIL);
      this.message = message;
    }
  }
  
  
  @Getter
  @AllArgsConstructor
  private static class OpenAllShopsResponse extends CommonResponse {
    List<ShopDTO> openShops;
  }

  @Getter
  private static class CloseShopResponse extends CommonResponse {
    enum Message {
      NOT_OPEN
    }

    @NonNull
    private Message message;
    private static final CloseShopResponse NOT_OPEN = new CloseShopResponse(Message.NOT_OPEN);

    public CloseShopResponse(Message message) {
      super(Result.FAIL);
      this.message = message;
    }
  }



  @Getter
  @AllArgsConstructor
  private static class ShopInfoResponse extends CommonResponse {
    private ShopDTO shopInfo;
    private List<AddressDTO> deliveryLocations;
  }

  @Getter
  @AllArgsConstructor
  private static class closeAllShopsResponse extends CommonResponse {
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
