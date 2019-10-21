package com.delfood.controller;

import com.delfood.dto.ShopDTO;
import com.delfood.service.ShopService;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<OpenShopResponse> openShop(HttpSession session,
      @RequestBody ShopDTO shopInfo) {
    String ownerId = (String) session.getAttribute("LOGIN_OWNER_ID");

    // 로그인 하지 않았을 시 401코드를 반환한다.
    if (ownerId == null) {
      return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }

    shopInfo.setOwnerId(ownerId);

    // 입력한 데이터 중 필수 데이터가 null일 경우 400 에러코드를 반환한다.
    if (ShopDTO.hasNullDataBeforeCreate(shopInfo)) {
      return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.NULL_ARGUMENTS,
          HttpStatus.BAD_REQUEST);
    }

    shopService.addShop(shopInfo);


    return new ResponseEntity<ShopController.OpenShopResponse>(OpenShopResponse.SUCCESS,
        HttpStatus.CREATED);
  }



  // Response 객체
  @Getter
  @RequiredArgsConstructor
  private static class OpenShopResponse {
    enum Result {
      SUCCESS, NO_LOGIN, NULL_ARGUMENTS
    }

    @NonNull
    Result result;

    private static final OpenShopResponse SUCCESS = new OpenShopResponse(Result.SUCCESS);
    private static final OpenShopResponse NO_LOGIN = new OpenShopResponse(Result.NO_LOGIN);
    private static final OpenShopResponse NULL_ARGUMENTS =
        new OpenShopResponse(Result.NULL_ARGUMENTS);
  }

}
