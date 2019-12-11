package com.delfood.mapper;

import com.delfood.dto.MenuDTO;

import org.springframework.stereotype.Repository;

@Repository
public interface MenuMapper {

  /**
   * 메뉴 생성.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 메뉴 생성에 필요한 정보 ( 이름, 가격, 사진, 설명, 메뉴그룹 아이디)
   * @return
   */
  public Long insertMenu(MenuDTO menuInfo);
  
  /**
   * 메뉴 삭제.
   * 
   * @author jinyoung
   * 
   * @param id 메뉴 아이디
   * @return
   */
  public int deleteMenu(Long id);
  
  /**
   * 메뉴 존재 여부 체크.
   * 
   * @param menuGroupId 메뉴그룹 아이디
   * @param menuId 메뉴 아이디
   * @return
   */
  public int checkMenu(Long menuGroupId, Long menuId);

  /**
   * 메뉴그룹에 속한 메뉴의 수 조회.
   * @param menuGroupId 메뉴 그룹 아이디
   * @return
   */
  public int totalCount(Long menuGroupId);
  
  /**
   * 메뉴 순서 변경.
   * 
   * @author jinyoung
   * @param id 메뉴 아이디
   * @param priority 우선순위
   * @return
   */
  public int updateMenuPriority(Long id, int priority);

  /**
   * 메뉴 정보 변경.
   * 
   * @author jinyoung
   * @param menuInfo 수정할 메뉴 정보
   * @return
   */
  public int updateMenu(MenuDTO menuInfo);

  /**
   * 메뉴 조회.
   * 
   * @author jinyoung
   * @param id 메뉴 아이디
   * @return
   */
  public MenuDTO findById(Long id);
  
  /**
   * 메뉴를 옵션과 함께 조회한다.
   * @author jun
   * @param id 메뉴 아이디
   * @return
   */
  public MenuDTO findMenuWithOptionsById(Long id);

}
