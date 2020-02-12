package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockitoSession;
import com.delfood.dto.menu.MenuGroupDTO;
import com.delfood.dto.menu.MenuGroupDTO.Status;
import com.delfood.error.exception.TargetNotFoundException;
import com.delfood.error.exception.TooManyModifiedException;
import com.delfood.error.exception.menuGroup.InvalidMenuGroupCountException;
import com.delfood.error.exception.menuGroup.InvalidMenuGroupIdException;
import com.delfood.mapper.MenuGroupMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MenuGroupServiceTest {
  
  @InjectMocks
  MenuGroupService service;
  
  @Mock
  MenuGroupMapper mapper;
  
  /**
   * MenuGroupDTO를 새로 생성하여 반환한다.
   * @return
   */
  public MenuGroupDTO generateMenuGroupDTO() {
    MenuGroupDTO dto = new MenuGroupDTO();
    dto.setId(1L);
    dto.setContent("Test Menu Group");
    dto.setName("Test Menu Name");
    dto.setPriority(0);
    dto.setShopId(1L);
    dto.setStatus(Status.DEFAULT);
    dto.setCreatedAt(LocalDateTime.now());
    dto.setUpdatedAt(LocalDateTime.now());
    return dto;
  }
  
  /**
   * MenuGroupDTO 리스트를 새로 만들어 반환한다.
   * @return
   */
  public List<MenuGroupDTO> generateMenuGroups() {
    List<MenuGroupDTO> menuGroups = new ArrayList<MenuGroupDTO>();
    for (int i = 0; i < 5; i++) {
      MenuGroupDTO menuGroup = generateMenuGroupDTO();
      menuGroup.setId((long)i);
      menuGroups.add(menuGroup);
    }
    return menuGroups;
  }
  
  public List<Long> generateIdList() {
    List<Long> idList = new ArrayList<Long>();
    for (long i = 0; i < 5; i++) {
      idList.add(i);
    }
    return idList;
  }
  
  @Test
  public void addMenuGroupTest_메뉴그룹_생성_성공() {
    MenuGroupDTO menuGroup = generateMenuGroupDTO();
    given(mapper.insertMenuGroup(menuGroup))
      .willReturn(1);
    service.addMenuGroup(menuGroup);
  }
  
  @Test(expected = RuntimeException.class)
  public void addMenuGroupTest_메뉴그룹_생성_실패() {
    MenuGroupDTO menuGroup = generateMenuGroupDTO();
    given(mapper.insertMenuGroup(menuGroup))
      .willReturn(0);
    service.addMenuGroup(menuGroup);
  }
  
  @Test
  public void nameCheckTest_메뉴그룹_이름_중복검사() {
    given(mapper.nameCheck("test menu"))
      .willReturn(1);
    given(mapper.nameCheck("test menu2"))
      .willReturn(0);
    
    assertThat(service.nameCheck("test menu")).isEqualTo(true);
    assertThat(service.nameCheck("test menu2")).isEqualTo(false);
  }
  
  @Test
  public void getMenuGroupsTest_매장_메뉴그룹_조회() {
    List<MenuGroupDTO> menuGroups = generateMenuGroups();
    given(mapper.findByShopid(1L))
      .willReturn(menuGroups);
    given(mapper.findByShopid(2L))
      .willReturn(new ArrayList<MenuGroupDTO>() {});
    
    assertThat(service.getMenuGroups(1L)).isEqualTo(menuGroups);
    assertThat(service.getMenuGroups(2L)).hasSize(0);
  }
  
  @Test
  public void updateMenuGroupNameAndContentTest_메뉴그룹_이름_내용_수정_성공() {
    given(mapper.updateNameAndContent("test name", "test content", 1L))
      .willReturn(1);
    service.updateMenuGroupNameAndContent("test name", "test content", 1L);
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMenuGroupNameAndContentTest_메뉴그룹_이름_내용_수정_실패() {
    given(mapper.updateNameAndContent("test name", "test content", 2L))
      .willReturn(0);
    service.updateMenuGroupNameAndContent("test name", "test content", 2L);
  }
  
  @Test
  public void deleteMenuGroupTest_메뉴그룹_삭제_성공() {
    given(mapper.deleteMenuGroup(1L))
      .willReturn(1);
    service.deleteMenuGroup(1L);
  }
  
  @Test(expected = TargetNotFoundException.class)
  public void deleteMenuGroupTest_메뉴그룹_삭제_실패() {
    given(mapper.deleteMenuGroup(2L))
      .willReturn(0);
    service.deleteMenuGroup(2L);
  }
  
  @Test(expected = TooManyModifiedException.class)
  public void deleteMenuGroupTest_메뉴그룹_삭제_실패2() {
    given(mapper.deleteMenuGroup(2L))
      .willReturn(100);
    service.deleteMenuGroup(2L);
  }
  
  @Test
  public void getMenuGroupsInkudeMenusTest_매장_메뉴그룹과_메뉴들_조회() {
    List<MenuGroupDTO> menuGroups = generateMenuGroups();
    given(mapper.findByShopid(1L))
      .willReturn(menuGroups);
    
    assertThat(service.getMenuGroupsIncludedMenus(1L))
      .isEqualTo(menuGroups)
      .hasSize(5);
  }
  
  @Test
  public void updateMenuGroupPriorityTest_메뉴그룹_순서_수정_성공() {
    List<MenuGroupDTO> menuGroups = generateMenuGroups();
    given(mapper.totalCount(1L))
      .willReturn(menuGroups.size());
    given(mapper.updateMenuGroupPriority(Mockito.anyLong(), Mockito.anyList()))
      .willReturn(1);
    List<Long> idList = generateIdList();
    
    service.updateMenuGroupPriority(1L, idList);
  }

  
  @Test(expected = InvalidMenuGroupCountException.class)
  public void updateMenuGroupPriorityTest_메뉴그룹_순서_수정_유효성검사_실패() {
    given(mapper.totalCount(1L))
      .willReturn(23232);
    
    List<Long> idList = generateIdList();
    
    service.updateMenuGroupPriority(1L, idList);
  }
}
