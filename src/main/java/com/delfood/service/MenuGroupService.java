package com.delfood.service;

import lombok.extern.log4j.Log4j2;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delfood.error.exception.DuplicateException;
import com.delfood.error.exception.TargetNotFoundException;
import com.delfood.error.exception.TooManyModifiedException;
import com.delfood.error.exception.menuGroup.InvalidMenuGroupCountException;
import com.delfood.error.exception.menuGroup.InvalidMenuGroupIdException;
import com.delfood.mapper.MenuGroupMapper;
import com.delfood.dto.MenuGroupDTO;

@Service
@Log4j2
public class MenuGroupService {
  
  @Autowired
  MenuGroupMapper menuGroupMapper;
  

  /**
   * 메뉴그룹 추가.
   * 
   * @author jinyoung
   * 
   * @param menuGroupInfo 메뉴그룹 정보
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addMenuGroup(MenuGroupDTO menuGroupInfo) {
    if (this.nameCheck(menuGroupInfo.getName())) {
      log.error("MenuGroup name is duplicated name : {}", menuGroupInfo.getName());
      throw new DuplicateException("MenuGroup name is duplicated! name : " 
            + menuGroupInfo.getName());
    }
    
    int result = menuGroupMapper.insertMenuGroup(menuGroupInfo);
    if (result != 1) {
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
  public List<MenuGroupDTO> getMenuGroups(Long shopId) {
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
    String contentStr = content == null ? "" : content;
    int result = menuGroupMapper.updateNameAndContent(name, contentStr, id);
    if (result != 1) {
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
  @Transactional(rollbackFor = RuntimeException.class)
  public void deleteMenuGroup(Long id) {
    int result = menuGroupMapper.deleteMenuGroup(id);
    
    if (result == 0) {
      log.error("Not found menugroup to delete! id : {}", id);
      throw new TargetNotFoundException("Not found menugroup to delete!");
    }
    
    if (result != 1) {
      log.error("menugroup modified too many times! id : {}", id);
      throw new TooManyModifiedException("menugroup modified too many times!");
    }
  }
  
  /**
   * 한 매장의 메뉴 그룹과 각 메뉴그룹의 메뉴들을 조회.
   * 
   * @author jinyoung
   * @param shopId 매장 아이디
   * @return 
   */
  public List<MenuGroupDTO> getMenuGroupsIncludedMenus(Long shopId) {
    return menuGroupMapper.findByShopid(shopId);
  }

  /**
   * 매장의 메뉴그룹 순서를 수정한다.
   * 
   * @param shopId 매장 아이디
   * @param idList 메뉴그룹 아이디 리스트
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void updateMenuGroupPriority(Long shopId, List<Long> idList) {
    int total = menuGroupMapper.totalCount(shopId);
    if (idList.size() != total) {
      log.error("The menugroup of targets is not correct. {}", idList);
      throw new InvalidMenuGroupCountException("The menugroup of targets is not correct.");
    }
    
    menuGroupMapper.updateMenuGroupPriority(shopId, idList);
    
  }
  
}
