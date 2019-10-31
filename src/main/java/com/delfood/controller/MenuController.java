package com.delfood.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import lombok.Setter;

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
   * 
   */
  @GetMapping("/shops/{shopId}/menuGroups")
  public ResponseEntity<List<ShopDTO>> menuMngInfo(HttpSession session,
      @PathVariable("shopId") long lastId) {
    
    String loginOwnerId = SessionUtil.getLoginOwnerId(session);
    if (loginOwnerId == null) {
      return new ResponseEntity<List<ShopDTO>>(
          HttpStatus.UNAUTHORIZED);
    }
    
    List<ShopDTO> myShops = shopService.getMyShops(loginOwnerId, shopId);
    return new ResponseEntity<List<ShopDTO>>(myShops, HttpStatus.OK);
  }
  
  
  /**
   * 매장 이름, 주소 및 모든 메뉴 정보를 조회한다.
   * 메뉴 그룹 > 메뉴 > 상위 옵션 2개
   * 
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   */
  @GetMapping("/shops/{id}/menus")
  public ResponseEntity<ShopMenuInfoResponse> shopMenuInfo(@PathVariable("id") long shopId) {

    ShopDTO shopInfo = shopService.getMyShopInfo(shopId);
    
    if (shopInfo == null) {
      return new ResponseEntity<MenuController.ShopMenuInfoResponse>(HttpStatus.NOT_FOUND); 
    }
    
    List<MenuGroupDTO> menuGroups = menuGroupService.getMenuGroupsIncludedMenus(shopId);
    
    ShopMenuInfoResponse menuMngInfoResponse = ShopMenuInfoResponse.builder()
                                                  .shopInfo(shopInfo)
                                                  .menuGroups(menuGroups)
                                                 .build();
    
    return new ResponseEntity<MenuController.ShopMenuInfoResponse>(menuMngInfoResponse,
        HttpStatus.OK);
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
  @PostMapping("/shops/{shopId}/menuGroups")
  public ResponseEntity<AddMenuGroupResponse> addMenuGroup(HttpSession session,
      @RequestBody MenuGroupDTO menuGroupInfo, @PathVariable long shopId) {
    
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if (shopService.checkShopId(ownerId, shopId)) {
      return new ResponseEntity<MenuController.AddMenuGroupResponse>(
          AddMenuGroupResponse.EMPTY_SHOP_ID, HttpStatus.UNAUTHORIZED);
    }
    
    
    if (menuGroupService.nameCheck(menuGroupInfo.getName())) {
      return new ResponseEntity<MenuController.AddMenuGroupResponse>(
          AddMenuGroupResponse.NAME_DUPLICATED, HttpStatus.CONFLICT);
    }
    
    menuGroupInfo.setShopId(shopId);
    menuGroupService.addMenuGroup(menuGroupInfo);
    
    return new ResponseEntity<MenuController.AddMenuGroupResponse>(
        AddMenuGroupResponse.SUCCESS, HttpStatus.OK);
  }
  
  /**
   * 메뉴그룹 이름 및 내용 수정.
   * 
   * @author jinyoung
   * 
   * @param request 아이디, 이름, 내용을 담은 요청객체
   * @return
   */
  @PatchMapping("/shops/{id}/menuGroups")
  public ResponseEntity<UpdateMenuGroupResponse> updateMenuGroup(
      @RequestBody UpdateMenuGroupRequest request) {
    
    if (request.getId() == null) {
      return new ResponseEntity<MenuController.UpdateMenuGroupResponse>(
          UpdateMenuGroupResponse.EMPTY_ID, HttpStatus.BAD_REQUEST);
    }
    
    if (request.getName() == null) {
      return new ResponseEntity<MenuController.UpdateMenuGroupResponse>(
          UpdateMenuGroupResponse.EMPTY_NAME, HttpStatus.BAD_REQUEST);
    }
    
    Long id = request.getId();
    String name = request.getName();
    String content = request.getContent() == null ? "" : request.getContent();
    
    menuGroupService.updateMenuGroupNameAndContent(name, content, id);
    return new ResponseEntity<MenuController.UpdateMenuGroupResponse>(
        UpdateMenuGroupResponse.SUCCESS, HttpStatus.OK);
  }
  
  /**
   * 메뉴 그룹 삭제.
   * 실제 데이터를 삭제하진 않고 Status를 "DELETE"로 변경
   * 
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   * @param menuGroupId 매뉴 그룹 아이디
   * @return
   */
  @DeleteMapping("/shops/{shopId}/menuGroups/{menuGroupId}")
  public HttpStatus deleteMenuGroup(
      HttpSession session, @PathVariable("shopId") Long shopId,
      @PathVariable("menuGroupId") Long menuGroupId) {

    String ownerId = SessionUtil.getLoginOwnerId(session);

    if (shopId == null 
        || menuGroupId == null) {
      return HttpStatus.BAD_REQUEST;
    }
    
    if (!shopService.checkShopId(ownerId, shopId)) {
      return HttpStatus.UNAUTHORIZED;
    }
    
    menuGroupService.deleteMenuGroup(menuGroupId);
    return HttpStatus.OK;
  }
  
  /**
   * 메뉴그룹 순서를 변경한다.
   * 
   * @param idList 정렬된 메뉴그룹 아이디 리스트
   * @return
   */
  @PatchMapping("/shops/{shopId}/menuGroups/priority")
  public HttpStatus updateMenuGroupPriority(
      @PathVariable("shopId") Long shopId, @RequestBody List<Long> idList) {
    
    menuGroupService.updateMenuGroupPriority(shopId, idList);
    
    return HttpStatus.OK;
  }
  
  // ===================== resopnse 객체 =====================
  
  @Getter
  @Builder
  private static class ShopMenuInfoResponse {
    private ShopDTO shopInfo;
    private List<MenuGroupDTO> menuGroups;
  }

  @Getter
  @RequiredArgsConstructor
  private static class AddMenuGroupResponse {
    enum AddMenuGroupStatus {
      SUCCESS, EMPTY_SHOP_ID, NAME_DUPLICATED
    }
    
    @NonNull
    private AddMenuGroupStatus result;
    
    private static final AddMenuGroupResponse SUCCESS 
        = new AddMenuGroupResponse(AddMenuGroupStatus.SUCCESS);
    private static final AddMenuGroupResponse NAME_DUPLICATED 
        = new AddMenuGroupResponse(AddMenuGroupStatus.NAME_DUPLICATED);
    private static final AddMenuGroupResponse EMPTY_SHOP_ID 
        = new AddMenuGroupResponse(AddMenuGroupStatus.EMPTY_SHOP_ID);
  }
  
  @Getter
  @RequiredArgsConstructor
  private static class UpdateMenuGroupResponse {
    enum UpdateMenuGroupStatus {
      SUCCESS, EMPTY_ID, EMPTY_NAME
    }
    
    @NonNull
    private UpdateMenuGroupStatus result;
    
    private static final UpdateMenuGroupResponse SUCCESS 
        = new UpdateMenuGroupResponse(UpdateMenuGroupStatus.SUCCESS);
    private static final UpdateMenuGroupResponse EMPTY_ID 
        = new UpdateMenuGroupResponse(UpdateMenuGroupStatus.EMPTY_ID);
    private static final UpdateMenuGroupResponse EMPTY_NAME 
        = new UpdateMenuGroupResponse(UpdateMenuGroupStatus.EMPTY_NAME);
  }
  
  @Getter
  @RequiredArgsConstructor
  private static class UpdateMenuGroupPriorityResponse {
    enum UpdateMenuGroupPriorityStatus {
      SUCCESS, NOT_MATCH_COUNT_OF_MENUGROUP
    }
    
    @NonNull
    private UpdateMenuGroupPriorityStatus result;
    
    private static final UpdateMenuGroupPriorityResponse SUCCESS
        = new UpdateMenuGroupPriorityResponse(UpdateMenuGroupPriorityStatus.SUCCESS);
    private static final UpdateMenuGroupPriorityResponse NOT_MATCH_COUNT_OF_MENUGROUP
        = new UpdateMenuGroupPriorityResponse(
            UpdateMenuGroupPriorityStatus.NOT_MATCH_COUNT_OF_MENUGROUP);
  }
  
  // ===================== request 객체 =====================
  
  @Getter
  private static class UpdateMenuGroupRequest {
    @NonNull
    private Long id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String content;
  }
  
}
