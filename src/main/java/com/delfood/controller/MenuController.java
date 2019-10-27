package com.delfood.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.delfood.dto.MenuGroupDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.service.MenuGroupService;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
public class MenuController {
  
  @Autowired
  ShopService shopService;
  
  @Autowired
  MenuGroupService menuGroupService;
  
  /**
   * 메뉴 관리
   * - 매장 목록을 보여준다.
   * 
   * @author jinyoung
   * 
   * @param session
   */
  @GetMapping("/menuMng/shops/{lastId}")
  public ResponseEntity<List<ShopDTO>> menuMngInfo(HttpSession session, @PathVariable long lastId ) {
    
    String loginOwnerId = SessionUtil.getLoginOwnerId(session);
    if(loginOwnerId == null) {
      return new ResponseEntity<List<ShopDTO>>(
          HttpStatus.UNAUTHORIZED);
    }
    
    List<ShopDTO> myShops = shopService.getMyShops(loginOwnerId, lastId);
    return new ResponseEntity<List<ShopDTO>>(myShops, HttpStatus.OK);
  }
  
  
  /**
   * 매장 이름, 주소 및 모든 메뉴 정보를 조회한다.
   * 
   * 메뉴 그룹 > 메뉴 > 상위 옵션 2개
   * 
   * @author jinyoung
   * 
   * @param id
   */
  @GetMapping("/shops/{id}/menus")
  public ResponseEntity<ShopMenuInfoResponse> shopMenuInfo(@PathVariable("id") long shopId) {
    // id를 통해 샵정보를 불러온다.
    ShopDTO shopInfo = shopService.getMyShopInfo(shopId);
    
    // 샵 정보가 없다면 샵정보가 존재하지 않음을 알려주고 404코드를 리턴한다.
    if(shopInfo == null) {
        return new ResponseEntity<MenuController.ShopMenuInfoResponse>(ShopMenuInfoResponse.SHOPINFO_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    
    // id를 통해 메뉴그룹을 불러온다.
    List<MenuGroupDTO> menuGroups = menuGroupService.getMenuGroupsIncludedMenus(shopId);
    
    // response 객체에 조회한 정보들을 저장
    ShopMenuInfoResponse menuMngInfoResponse = ShopMenuInfoResponse.builder()
                                                  .result(ShopMenuInfoResponse.ShopMenuInfoStatus.SUCCESS)
                                                  .shopInfo(shopInfo)
                                                  .menuGroups(menuGroups)
                                                 .build();
    
    return new ResponseEntity<MenuController.ShopMenuInfoResponse>(menuMngInfoResponse, HttpStatus.OK);
  }
  

  /**
   * 메뉴 그룹을 추가한다.
   * 
   * @author jinyoung
   * 
   * @param session 사용자 세션
   * @param menuGroupInfo 가입에 필요한 메뉴그룹 정보
   * @return
   */
  @PostMapping("/shops/{id}/menuGroups")
  public ResponseEntity<addMenuGroupResponse> addMenuGroup(HttpSession session, @RequestBody MenuGroupDTO menuGroupInfo){
    
    if(shopService.checkShopId(SessionUtil.getLoginOwnerId(session), menuGroupInfo.getShopId())) {
      return new ResponseEntity<MenuController.addMenuGroupResponse>(
          addMenuGroupResponse.EMPTY_SHOP_ID, HttpStatus.UNAUTHORIZED);
    }
    
    
    if(menuGroupService.nameCheck(menuGroupInfo.getName())){
      return new ResponseEntity<MenuController.addMenuGroupResponse>(
          addMenuGroupResponse.NAME_DUPLICATED, HttpStatus.CONFLICT);
    }
    
    menuGroupService.addMenuGroup(menuGroupInfo);
    
    return new ResponseEntity<MenuController.addMenuGroupResponse>(
        addMenuGroupResponse.SUCCESS, HttpStatus.OK);
  }
  

  
  
  
  
  // ===================== resopnse 객체 =====================
  
  @Getter
  @Builder
  private static class ShopMenuInfoResponse{
    enum ShopMenuInfoStatus{
       SUCCESS, FAIL, SHOPINFO_NOT_FOUND
    }
    
    @NonNull
    private ShopMenuInfoStatus result;
    
    private ShopDTO shopInfo;
    private List<MenuGroupDTO> menuGroups;

    
    private static final ShopMenuInfoResponse FAIL = ShopMenuInfoResponse.builder().result(ShopMenuInfoStatus.FAIL).build();
    private static final ShopMenuInfoResponse SHOPINFO_NOT_FOUND = ShopMenuInfoResponse.builder().result(ShopMenuInfoStatus.SHOPINFO_NOT_FOUND).build();
  }

  @Getter
  @RequiredArgsConstructor
  private static class addMenuGroupResponse{
    enum addMenuGroupStatus{
      SUCCESS, EMPTY_SHOP_ID, NAME_DUPLICATED
    }
    
    @NonNull
    private addMenuGroupStatus result;
    
    private static final addMenuGroupResponse SUCCESS = new addMenuGroupResponse(addMenuGroupStatus.SUCCESS);
    private static final addMenuGroupResponse NAME_DUPLICATED = new addMenuGroupResponse(addMenuGroupStatus.NAME_DUPLICATED);
    private static final addMenuGroupResponse EMPTY_SHOP_ID = new addMenuGroupResponse(addMenuGroupStatus.EMPTY_SHOP_ID);
  }
}
