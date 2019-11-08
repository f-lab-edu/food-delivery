package com.delfood.service;

import com.delfood.dto.MenuDTO;
import com.delfood.mapper.MenuMapper;
import java.sql.SQLException;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class MenuService {

  @Autowired
  MenuMapper menuMapper;
  
  /**
   * 메뉴 조회.
   * 
   * @author jinyoung
   * @param menuId 메뉴 아이디
   * @return
   */
  public MenuDTO getMenuInfo(Long menuId) {
    return menuMapper.findById(menuId);
  }
  
  /**
   * 메뉴 추가.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 메뉴 추가에 필요한 정보
   * @return 
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public Long addMenu(MenuDTO menuInfo) {
    return menuMapper.insertMenu(menuInfo);
  }
  
  /**
   * 메뉴 삭제.
   * 
   * @author jinyoung
   * 
   * @param id 삭제할 메뉴의 아이디
   */
  public void deleteMenu(Long id) {
    int result = menuMapper.deleteMenu(id);
    if (result != 1) {
      log.error("delete Menu error! Menu id : " + id);
      throw new RuntimeException("delete Menu error!");
    }
  }
  
  /**
   * 메뉴의 존재 여부 체크.
   * 
   * @author jinyoung
   * 
   * @param menuGroupId 메뉴 그룹 아이디
   * @param menuId 메뉴 아이디
   */
  public void checkMenu(Long menuGroupId, Long menuId) {
    int result = menuMapper.checkMenu(menuGroupId, menuId);
    if (result == 0) {
      log.error("menuGroupId(" + menuGroupId + ") is not match menuId(" + menuId + ")");
      throw new RuntimeException("menuGroupId is not match menuId");
    }
    
    if (result != 1) {
      log.error("check Menu error! Menu id {}, MenuGroup id {} ", menuId, menuGroupId);
      throw new RuntimeException("check Menu error!");
    }
  }
  
  /**
   * 메뉴 순서를 변경한다.
   * 
   * @author jinyoung
   * 
   * @param menuGroupId 메뉴 그룹 아이디
   * @param idList 메뉴 아이디 리스트
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void updateMenuPriority(Long menuGroupId, List<Long> idList) {
    int total = menuMapper.totalCount(menuGroupId);
    if (idList.size() != total) {
      log.error("The menu of targets is not correct. {}", idList);
      throw new RuntimeException("The menu of targets is not correct.");
    }
    
    for (int i = 1; i <= idList.size(); i++) {
      if ((menuMapper.updateMenuPriority(idList.get(i - 1), i)) == 0) {
        log.error("Invalid menu id", idList.get(i - 1));
        throw new RuntimeException("Invalid menu id");
      }
    }
  }

  /**
   * 메뉴 수정.
   * 메뉴 정보를 수정한다 
   * MenuDTO에 있는 optionList는 추가할 옵션 정보
   * 
   * @author jinyoung
   * 
   * @param menuInfo 수정할 메뉴 정보
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void updateMenu(MenuDTO menuInfo) {
    int result = menuMapper.updateMenu(menuInfo);
    if (result != 1) {
      log.error("update menu error! {}", menuInfo);
      throw new RuntimeException("update menu error!");
    }
  }

}
