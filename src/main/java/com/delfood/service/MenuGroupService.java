package com.delfood.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delfood.dto.MenuGroupDTO;
import com.delfood.mapper.MenuGroupMapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MenuGroupService {
  
  @Autowired
  MenuGroupMapper menuGroupMapper;
  

  public void addMenuGroup(MenuGroupDTO menuGroupInfo) {
    int result = menuGroupMapper.insertMenuGroup(menuGroupInfo);
    if(result != 1) {
      log.error("insert MenuGroup ERROR! {}", menuGroupInfo);
      throw new RuntimeException("insert MenuGroup error!");
    }
  }
  
  /**
   * 메뉴 그룹 이름 중복 검사.
   * 
   * @param name 이름
   * @return
   */
  public boolean nameCheck(String name) {
    return menuGroupMapper.nameCheck(name) == 1;
  }
  
  /**
   * 한 매장의 메뉴 그룹 조회.
   * 
   * @author jinyoung
   * @param shopId 매장 아이디
   * @return 
   */
  public List<MenuGroupDTO> getMenuGroups(Long shopId){
    return menuGroupMapper.findByShopid(shopId);
  }
  
  /**
   * 메뉴그룹의 이름, 내용 수정.
   * 
   * @author jinyoung
   * @param name 이름
   * @param content 설명
   * @param id 아이디
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void updateMenuGroupNameAndContent(String name, String content, Long id) {
    int result = menuGroupMapper.updateNameAndContent(name, content, id);
    if(result != 1) {
      log.error("updateNameAndContent ERROR! name : {}, content : {}, id : {}",name,content,id);
      throw new RuntimeException("Error during update menuGroup name and content!");
    }
  }
  
  /**
   * 메뉴 그룹 삭제.
   * 
   * @author jinyoung
   * @param id 아이디
   */
  public void deleteMenuGroup(Long id) {
    int result = menuGroupMapper.deleteMenuGroup(id);
    if(result != 1) {
      log.error("deleteMenuGroup ERROR! id : {}", id);
      throw new RuntimeException("Error during update menuGroup status!");
    }
  }
  
  /**
   * 한 매장의 메뉴 그룹과 각 메뉴그룹의 메뉴들을 조회
   * 
   * @author jinyoung
   * @param shopId 매장 아이디
   * @return 
   */
  public List<MenuGroupDTO> getMenuGroupsIncludedMenus(Long shopId){
    return menuGroupMapper.findByShopid(shopId);
  }
  
}
