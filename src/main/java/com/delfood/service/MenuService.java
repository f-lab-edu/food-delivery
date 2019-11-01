package com.delfood.service;

import com.delfood.dto.MenuDTO;
import com.delfood.mapper.MenuMapper;
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
   * 메뉴 추가.
   * 
   * @param menuInfo 메뉴 추가에 필요한 정보
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addMenu(MenuDTO menuInfo) {
    int result = menuMapper.insertMenu(menuInfo);
    if (result == 1) {
      log.error("insert Menu error!" + menuInfo);
      throw new RuntimeException("insert Menu error!");
    }
  }
  
  
}
