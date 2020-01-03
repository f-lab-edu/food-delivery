package com.delfood.controller;

import com.delfood.aop.OwnerLoginCheck;
import com.delfood.aop.OwnerShopCheck;
import com.delfood.dao.RecentShopViewDao;
import com.delfood.dto.MenuGroupDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.service.MenuGroupService;
import com.delfood.service.RecentShopViewService;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;

import java.util.List;
import javax.servlet.http.HttpSession;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MenuGroupController {
  @Autowired
  ShopService shopService;
  
  @Autowired
  MenuGroupService menuGroupService;

  @Autowired
  RecentShopViewService recentShopViewService;

  /**
   * 메뉴 관리
   * - 매장 목록을 보여준다.
   * 
   * @author jinyoung
   * 
   * @param session
   * 
   */
  @GetMapping("/shops/{shopId}/manage/menus")
  @OwnerLoginCheck
  public ResponseEntity<List<ShopDTO>> shops(HttpSession session,
      @PathVariable long shopId) {
    
    String loginOwnerId = SessionUtil.getLoginOwnerId(session);
    
    List<ShopDTO> myShops = shopService.getMyShops(loginOwnerId, shopId);
    return new ResponseEntity<List<ShopDTO>>(myShops, HttpStatus.OK);
  }
  
  /**
   * 매장 이름, 주소 및 모든 메뉴 정보를 조회한다.
   *
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   */
  @GetMapping("/shops/{shopId}/menuGroups/all")
  public ResponseEntity<ShopMenuInfoResponse> shopMenuInfo(@PathVariable("shopId") long shopId,
                                                           HttpSession session) {

    ShopDTO shopInfo = shopService.getMyShopInfo(shopId);

    String memberId = SessionUtil.getLoginMemberId(session);
    if (memberId != null) {
      recentShopViewService.add(memberId, shopId);
    }

    if (shopInfo == null) {
      return new ResponseEntity<MenuGroupController.ShopMenuInfoResponse>(HttpStatus.NOT_FOUND); 
    }
    
    List<MenuGroupDTO> menuGroups = menuGroupService.getMenuGroupsIncludedMenus(shopId);
    
    ShopMenuInfoResponse menuMngInfoResponse = ShopMenuInfoResponse.builder()
                                                  .shopInfo(shopInfo)
                                                  .menuGroups(menuGroups)
                                                 .build();
    
    return new ResponseEntity<MenuGroupController.ShopMenuInfoResponse>(menuMngInfoResponse,
        HttpStatus.OK);
  }
  
  /**
   * 메뉴 그룹을 추가한다.
   * 
   * @author jinyoung
   * 
   * @param menuGroupInfo 가입에 필요한 메뉴그룹 정보
   * @return
   */
  @PostMapping("/shops/{shopId}/menuGroups")
  @OwnerShopCheck
  public HttpStatus addMenuGroup(@RequestBody MenuGroupDTO menuGroupInfo,
      @PathVariable long shopId) {
    
    menuGroupService.addMenuGroup(menuGroupInfo);
    
    return HttpStatus.CREATED;
  }
  
  /**
   * 메뉴 그룹 조회.
   * 
   * @param shopId 매장 아이디
   * @return
   */
  @GetMapping("/shops/{shopId}/menuGroups")
  @OwnerShopCheck
  public ResponseEntity<List<MenuGroupDTO>> menuGroups(@PathVariable("shopId") long shopId) {
    
    List<MenuGroupDTO> menuGroups = menuGroupService.getMenuGroups(shopId);
    
    if (menuGroups == null) {
      return new ResponseEntity<List<MenuGroupDTO>>(HttpStatus.NOT_FOUND);
    }
    
    return new ResponseEntity<List<MenuGroupDTO>>(menuGroups, HttpStatus.OK);
  }
  
  /**
   * 메뉴그룹 이름 및 내용 수정.
   * 
   * @author jinyoung
   * 
   * @param request 아이디, 이름, 내용을 담은 요청객체
   * @return
   */
  @PatchMapping("/shops/{shopId}/menuGroups")
  @OwnerShopCheck
  public ResponseEntity<String> updateMenuGroup(
      @RequestBody UpdateMenuGroupRequest request) {
    
    if (request.getId() == null) {
      return EMPTY_ID;
    }
    
    if (request.getName() == null) {
      return EMPTY_NAME;
    }
    
    Long id = request.getId();
    String name = request.getName();
    String content = request.getContent();
    
    menuGroupService.updateMenuGroupNameAndContent(name, content, id);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
  
  /**
   * 메뉴그룹 순서 변경.
   * 
   * @param shopId 매장 아이디
   * @param idList 순서가 있는 메뉴그룹 아이디 아이디 리스트
   * @return
   */
  @PutMapping("/shops/{shopId}/menuGroups/priority")
  @OwnerShopCheck
  public void updateMenuGroupPriority(
      @PathVariable Long shopId, @RequestBody List<Long> idList) {
    
    menuGroupService.updateMenuGroupPriority(shopId, idList);
  }

  /**
   * 메뉴 그룹 삭제.
   * 실제 데이터를 삭제하진 않고 Status를 "DELETE"로 변경
   * 
   * @author jinyoung
   * 
   * @param menuGroupId 매뉴 그룹 아이디
   * @return
   */
  @DeleteMapping("/shops/{shopId}/menuGroups/{menuGroupId}")
  @OwnerShopCheck
  public HttpStatus deleteMenuGroup(@PathVariable("menuGroupId") Long menuGroupId) {
    menuGroupService.deleteMenuGroup(menuGroupId);
    return HttpStatus.OK;
  }
  
  // ==================== static =====================
  
  private static final ResponseEntity<String> EMPTY_ID = new ResponseEntity<>("id is empty.", HttpStatus.BAD_REQUEST);
  private static final ResponseEntity<String> EMPTY_NAME = new ResponseEntity<>("name is empty.", HttpStatus.BAD_REQUEST);
  
  // ===================== resopnse 객체 =====================
  
  @Getter
  @Builder
  private static class ShopMenuInfoResponse {
    private ShopDTO shopInfo;
    private List<MenuGroupDTO> menuGroups;
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
