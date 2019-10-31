package com.delfood.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.delfood.dto.MenuGroupDTO;

@Repository
public interface MenuGroupMapper {

  /**
   * 메뉴 그룹을 생성한다.
   * 
   * @author jinyoung
   * 
   * @param menuGroupInfo 메뉴 그룹 정보
   * @return 
   */
  public int insertMenuGroup(MenuGroupDTO menuGroupInfo);
  
  /**
   * 메뉴 그룹 이름의 중복을 검사한다.
   * 
   * @param name 메뉴 그룹 이름
   * @return
   */
  public int nameCheck(String name);
  
  /**
   * 한 매장의 모든 메뉴그룹을 조회한다.
   * 
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   * @return 
   */
  public List<MenuGroupDTO> findByShopid(Long shopId);
  
  /**
   * 메뉴그룹의 이름과 내용을 수정한다.
   * 
   * @author jinyoung
   * 
   * @param name 이름
   * @param content 설명
   * @param id 아이디
   * @return
   */
  public int updateNameAndContent(String name, String content, Long id);
  
  /**
   * 메뉴 그룹 상태를 'DELETED'로 수정한다.
   * 
   * @author jinyoung
   * 
   * @param id 아이디
   * @return 
   */
  public int deleteMenuGroup(Long id);
  
  /**
   * 한 매장의 모든 메뉴그룹 각각에 메뉴들을 포함하여 조회한다.
   * 
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   * @return
   */
  public List<MenuGroupDTO> findMenuGroupAndMenus(Long shopId);

  /**
   * 한 매장의 전체 메뉴그룹 수를 반환한다.
   * 
   * @author jinyoung
   * 
   * @param shopId 매장 아이디
   * @return
   */
  public int totalCount(Long shopId);

  /**
   * 메뉴 그룹의 우선순위를 수정한다.
   * 
   * @author jinyoung
   * 
   * @param id 메뉴그룹 아이디
   * @param priority 우선순위
   * @return
   */
  public int updateMenuGroupPriority(Long id, int priority);
  
}
