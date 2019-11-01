package com.delfood.controller;

import com.delfood.dto.MenuDTO;
import com.delfood.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 메뉴와 그 하위개념인 옵션 관련 기능을 수행하는 컨트롤러
@RestController
public class MenuController {

  @Autowired
  MenuService menuService;
  
  /**
   * 메뉴를 등록한다.
   * 
   * @param menuInfo 메뉴가입에 필요한 정보 ( 이름, 가격, 메뉴사진, 메뉴설명 )
   * @return
   */
  @PostMapping("/menuGroups/{menuGroupId}/menus")
  public HttpStatus addMenu(@RequestBody MenuDTO menuInfo,
      @PathVariable("menuGroupId") Long menuGroupId) {
    
    menuInfo.setMenuGroupId(menuGroupId);
    
    // 필수 조건 : 이름, 가격, 메뉴사진, 메뉴설명, 메뉴그룹 아이디
    if (MenuDTO.hasNullDataBeforeAdd(menuInfo)) {
      throw new NullPointerException(menuInfo.toString());
    }
    
    // 메뉴설명이 null 일 경우 "" 빈 문자열로 대체함.
    if (menuInfo.getContent() == null) {
      menuInfo.setContent("");
    }
    
    menuService.addMenu(menuInfo);
    
    return HttpStatus.CREATED;
  }
  
  
}
