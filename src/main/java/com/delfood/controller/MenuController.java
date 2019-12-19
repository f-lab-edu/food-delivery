package com.delfood.controller;

import com.delfood.dto.MenuDTO;
import com.delfood.dto.OptionDTO;
import com.delfood.service.MenuService;
import com.delfood.service.OptionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<MenuDTO> menu(@PathVariable Long menuId) {
    return new ResponseEntity<MenuDTO>(menuService.getMenuInfo(menuId), HttpStatus.OK);
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
    if (menuInfo.getOptionList() != null) {
      optionService.addOptionList(menuInfo.getOptionList(), menuId);
    }
    
    return HttpStatus.CREATED;
  }
  
  /**
   * 메뉴 삭제.
   * 
   * @param menuGroupId 메뉴그룹 아이디
   * @param menuId 메뉴 아이디
   */
  @DeleteMapping("/menuGroups/{menuGroupId}/menus/{menuId}")
  public void deleteMenu(@PathVariable Long menuGroupId,
      @PathVariable Long menuId) {
    
    menuService.deleteMenu(menuId);
  }

  
  /**
   * 메뉴 순서 변경.
   * 
   * @param menuGroupId 매장 아이디
   * @param idList 순서가 있는 메뉴그룹 아이디 아이디 리스트
   */
  @PutMapping("/menuGroups/{menuGroupId}/menus/priority")
  public void updateMenuPriority(
      @PathVariable Long menuGroupId, @RequestBody List<Long> idList) {
    menuService.updateMenuPriority(menuGroupId, idList);
  }
  
  /**
   * 메뉴 수정.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 수정할 메뉴 정보
   */
  @PatchMapping("/menuGroups/{menuGroupId}/menus/{menuId}")
  public void updateMenu(@RequestBody MenuDTO menuInfo) {
    
    menuService.updateMenu(menuInfo);
  }
  
  /**
   * 옵션 추가.
   */
  @PostMapping("/menus/{menuId}/options")
  public void addOption(@RequestBody OptionDTO optionInfo) {
    
    optionService.addOption(optionInfo);
  }
  
  /**
   * 옵션 삭제.
   * 
   * @author jinyoung
   * 
   * @param optionId 옵션 아이디
   */
  @DeleteMapping("/menus/{menuId}/options/{optionId}")
  public void deleteOption(@PathVariable Long optionId) {
    
    optionService.deleteOption(optionId);
  }
  
  
}
