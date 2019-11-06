package com.delfood.controller;

import com.delfood.dto.MenuDTO;
import com.delfood.dto.OptionDTO;
import com.delfood.service.MenuService;
import com.delfood.service.OptionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 메뉴와 그 하위개념인 옵션 관련 기능을 수행하는 컨트롤러
@RestController
public class MenuController {

  @Autowired
  MenuService menuService;
  
  @Autowired
  OptionService optionService;
  
  /**
   * 메뉴 조회.
   * 
   * @author jinyoung
   * 
   * @param menuId 메뉴 아이디
   * @return
   */
  @GetMapping("/menuGroups/{menuGroupId}/menus/{menuId}")
  public MenuDTO menu(@RequestBody Long menuId) {
    return menuService.getMenuInfo(menuId);
  }
  
  
  /**
   * 메뉴를 등록한다.
   * 
   * @param menuInfo 메뉴 추가에 필요한 정보 
   * @return
   */
  @PostMapping("/menuGroups/{menuGroupId}/menus")
  public HttpStatus addMenu(@RequestBody MenuDTO menuInfo,
      @PathVariable Long menuGroupId) {
    
    if (MenuDTO.hasNullDataBeforeAdd(menuInfo)) {
      throw new NullPointerException(menuInfo.toString());
    }
    
    Long menuId = menuService.addMenu(menuInfo);
    
    optionService.addOptionList(menuInfo.getOptionList(), menuId);
    
    return HttpStatus.CREATED;
  }
  
  /**
   * 메뉴 삭제.
   * 
   * @param menuGroupId 메뉴그룹 아이디
   * @param menuId 메뉴 아이디
   * @return
   */
  @PatchMapping("/menuGroups/{menuGroupId}/menus/{menuId}")
  public HttpStatus deleteMenu(@PathVariable Long menuGroupId,
      @PathVariable Long menuId) {
    
    if (menuGroupId == null || menuId == null) {
      return HttpStatus.BAD_REQUEST;
    }
    
    menuService.deleteMenu(menuId);
    return HttpStatus.OK;
    
  }

  
  /**
   * 메뉴 순서 변경.
   * 
   * @param menuGroupId 매장 아이디
   * @param idList 순서가 있는 메뉴그룹 아이디 아이디 리스트
   * @return
   */
  @PutMapping("/menuGroups/{menuGroupId}/menus/priority")
  public HttpStatus updateMenuPriority(
      @PathVariable Long menuGroupId, @RequestBody List<Long> idList) {
    menuService.updateMenuPriority(menuGroupId, idList);
    return HttpStatus.OK;
  }
  
  /**
   * 메뉴 수정.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 수정할 메뉴 정보
   * @return
   */
  @PatchMapping("/menuGroups/{menuGroupId}/menus/{menuId}")
  public HttpStatus updateMenu(@RequestBody MenuDTO menuInfo) {
    menuService.updateMenu(menuInfo);
    
    return HttpStatus.OK;
  }
  
  /**
   * 옵션 추가.
   */
  @PostMapping("/menus/{menuId}/options")
  public HttpStatus addOption(@RequestBody OptionDTO optionInfo) {
    optionService.addOption(optionInfo);
    return HttpStatus.OK;
  }
  
  /**
   * 옵션 삭제.
   * 
   * @author jinyoung
   * 
   * @param optionId 옵션 아이디
   * @return
   */
  @DeleteMapping("/menus/{menuId}/options/{optionId}")
  public HttpStatus deleteOption(@PathVariable Long optionId) {
    
    optionService.deleteOption(optionId);
    
    return HttpStatus.OK;
  }
  
  
}
