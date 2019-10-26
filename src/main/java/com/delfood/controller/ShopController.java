package com.delfood.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
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
import com.delfood.dto.OwnerDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopUpdateDTO;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
  public ResponseEntity<AddShopResponse> addShop(HttpSession session,
      @RequestBody ShopDTO shopInfo) {
    String ownerId = SessionUtil.getLoginOwnerId(session);

    // 로그인 하지 않았을 시 401코드를 반환한다.
    if (ownerId == null) {
      return new ResponseEntity<ShopController.AddShopResponse>(AddShopResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }

    shopInfo.setOwnerId(ownerId);

    // 입력한 데이터 중 필수 데이터가 null일 경우 400 에러코드를 반환한다.
    if (ShopDTO.hasNullDataBeforeCreate(shopInfo)) {
      return new ResponseEntity<ShopController.AddShopResponse>(AddShopResponse.NULL_ARGUMENTS,
          HttpStatus.BAD_REQUEST);
    }

    shopService.addShop(shopInfo);


    return new ResponseEntity<ShopController.AddShopResponse>(AddShopResponse.SUCCESS,
        HttpStatus.CREATED);
  }

  /**
   * 사장님이 가진 매장들을 불러온다.
   * 
   * @param myShopsRequest 페이징 정보
   * @param session 사용자의 세션
   * @return 페이지에 따른 사장님 매장, 총 매장 개수
   */
  @GetMapping
  public ResponseEntity<MyShopsResponse> myShops(MyShopsRequest myShopsRequest,
      HttpSession session) {
    String id = SessionUtil.getLoginOwnerId(session);
    if (id == null) {
      return new ResponseEntity<MyShopsResponse>(MyShopsResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    }

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
  public ResponseEntity<UpdateShopResponse> updateShop(
      @RequestBody(required = true) ShopUpdateDTO updateInfo, HttpSession session,
      @PathVariable(required = true) Long id) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    if (ownerId == null) {
      return new ResponseEntity<ShopController.UpdateShopResponse>(UpdateShopResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }

    ShopUpdateDTO copyData = ShopUpdateDTO.copyWithId(updateInfo, id);


    if (shopService.isShopOwner(copyData.getId(), ownerId) == false) {
      return new ResponseEntity<ShopController.UpdateShopResponse>(UpdateShopResponse.UNAUTHORIZED,
          HttpStatus.UNAUTHORIZED);
    }

    shopService.updateShop(copyData);


    return new ResponseEntity<ShopController.UpdateShopResponse>(UpdateShopResponse.SUCCESS,
        HttpStatus.OK);
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
  public ResponseEntity<OpenShopResponse> openShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {

    String ownerId = SessionUtil.getLoginOwnerId(session);

    // 로그인 하지 않았을시
    if (ownerId == null) {
      return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }

    // 로그인한 사장님이 해당 가게의 사장님이 아닐 시
    if (shopService.isShopOwner(id, ownerId) == false) {
      return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.UNAUTHORIZED,
          HttpStatus.UNAUTHORIZED);
    }

    // 매장이 오픈중일 때
    if (shopService.notOpenCheck(id) == false) {
      return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.IS_OPEN,
          HttpStatus.BAD_REQUEST);
    }


    shopService.openShop(id);

    return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.SUCCESS,
        HttpStatus.OK);
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
  public ResponseEntity<CloseShopResponse> closeShop(
      @PathVariable(value = "id", required = true) Long id, HttpSession session) {

    String ownerId = SessionUtil.getLoginOwnerId(session);

    // 로그인 하지 않았을시
    if (ownerId == null) {
      return new ResponseEntity<ShopController.CloseShopResponse>(CloseShopResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }

    // 로그인한 사장님이 해당 가게의 사장님이 아닐 시
    if (shopService.isShopOwner(id, ownerId) == false) {
      return new ResponseEntity<ShopController.CloseShopResponse>(CloseShopResponse.UNAUTHORIZED,
          HttpStatus.UNAUTHORIZED);
    }

    // 해당 매장이 영업중이 아닐시
    if (shopService.notOpenCheck(id) == true) {
      return new ResponseEntity<ShopController.CloseShopResponse>(CloseShopResponse.NOT_OPEN,
          HttpStatus.BAD_REQUEST);
    }

    shopService.closeShop(id);

    return new ResponseEntity<ShopController.CloseShopResponse>(CloseShopResponse.SUCCESS,
        HttpStatus.OK);
  }



  // Response 객체
  @Getter
  @RequiredArgsConstructor
  private static class AddShopResponse {
    enum Result {
      SUCCESS, NO_LOGIN, NULL_ARGUMENTS
    }

    @NonNull
    Result result;

    private static final AddShopResponse SUCCESS = new AddShopResponse(Result.SUCCESS);
    private static final AddShopResponse NO_LOGIN = new AddShopResponse(Result.NO_LOGIN);
    private static final AddShopResponse NULL_ARGUMENTS =
        new AddShopResponse(Result.NULL_ARGUMENTS);
  }

  @Getter
  @RequiredArgsConstructor
  @AllArgsConstructor
  private static class MyShopsResponse {
    enum Result {
      SUCCESS, NO_LOGIN
    }

    @NonNull
    Result result;

    List<ShopDTO> myShops;

    Long shopCount;

    private static final MyShopsResponse NO_LOGIN = new MyShopsResponse(Result.NO_LOGIN);

    public static MyShopsResponse success(List<ShopDTO> myShops, Long shopCount) {
      return new MyShopsResponse(Result.SUCCESS, myShops, shopCount);
    }
  }

  @Getter
  @RequiredArgsConstructor
  private static class UpdateShopResponse {
    enum Result {
      SUCCESS, NO_LOGIN, UNAUTHORIZED
    }

    @NonNull
    private Result result;
    private static final UpdateShopResponse SUCCESS = new UpdateShopResponse(Result.SUCCESS);
    private static final UpdateShopResponse NO_LOGIN = new UpdateShopResponse(Result.NO_LOGIN);
    private static final UpdateShopResponse UNAUTHORIZED =
        new UpdateShopResponse(Result.UNAUTHORIZED);
  }

  @Getter
  @RequiredArgsConstructor
  private static class OpenShopResponse {
    enum Result {
      SUCCESS, NO_LOGIN, UNAUTHORIZED, IS_OPEN
    }

    @NonNull
    private Result result;
    private static final OpenShopResponse SUCCESS = new OpenShopResponse(Result.SUCCESS);
    private static final OpenShopResponse NO_LOGIN = new OpenShopResponse(Result.NO_LOGIN);
    private static final OpenShopResponse UNAUTHORIZED = new OpenShopResponse(Result.UNAUTHORIZED);
    private static final OpenShopResponse IS_OPEN = new OpenShopResponse(Result.IS_OPEN);
  }

  @Getter
  @RequiredArgsConstructor
  private static class CloseShopResponse {
    enum Result {
      SUCCESS, NO_LOGIN, UNAUTHORIZED, NOT_OPEN
    }

    @NonNull
    private Result result;
    private static final CloseShopResponse SUCCESS = new CloseShopResponse(Result.SUCCESS);
    private static final CloseShopResponse NO_LOGIN = new CloseShopResponse(Result.NO_LOGIN);
    private static final CloseShopResponse UNAUTHORIZED =
        new CloseShopResponse(Result.UNAUTHORIZED);
    private static final CloseShopResponse NOT_OPEN = new CloseShopResponse(Result.NOT_OPEN);


  }

  // Request 객체
  @Getter
  @Setter
  private static class MyShopsRequest {
    @Nullable
    private Long lastId;
  }


}
