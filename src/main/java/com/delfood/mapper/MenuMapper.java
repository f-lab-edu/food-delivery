package com.delfood.mapper;

import org.springframework.stereotype.Repository;
import com.delfood.dto.MenuDTO;

@Repository
public interface MenuMapper {

  /**
   * 메뉴 생성 메서드.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 메뉴 생성에 필요한 정보 ( 이름, 가격, 사진, 설명, 메뉴그룹 아이디)
   * @return
   */
  public int insertMenu(MenuDTO menuInfo);
  
  
}
