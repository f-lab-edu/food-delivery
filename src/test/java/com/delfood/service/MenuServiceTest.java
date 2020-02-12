package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import com.delfood.dto.menu.MenuDTO;
import com.delfood.dto.menu.OptionDTO;
import com.delfood.dto.menu.MenuDTO.Status;
import com.delfood.mapper.MenuMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MenuServiceTest {
  
  @InjectMocks
  MenuService service;
  
  @Mock
  MenuMapper mapper;
  
  public MenuDTO generateMenu() {
    MenuDTO menu = new MenuDTO();
    menu.setId(1L);
    menu.setMenuGroupId(1L);
    menu.setContent("Test Menu Content");
    menu.setOptionList(new ArrayList<OptionDTO>());
    menu.setPhoto("Test Photo URL");
    menu.setPrice(12000L);
    menu.setPriority(1L);
    menu.setStatus(Status.DEFAULT);
    menu.setCreatedAt(LocalDateTime.now());
    menu.setUpdatedAt(LocalDateTime.now());
    return menu;
  }
  
  @Test
  public void getMenuInfoTest_메뉴_조회_성공() {
    MenuDTO menu = generateMenu();
    given(mapper.findById(1L)).willReturn(menu);
    given(mapper.findById(999L)).willReturn(null);
    assertThat(service.getMenuInfo(1L)).isEqualTo(menu);
    assertThat(service.getMenuInfo(999L)).isNull();
  }
  
  @Test
  public void addMenuTest_메뉴_추가_성공() {
    MenuDTO menu = generateMenu();
    given(mapper.insertMenu(menu)).willReturn(menu.getId());
    service.addMenu(menu);
  }
  
  @Test
  public void deleteMenu_메뉴_삭제_성공() {
    given(mapper.deleteMenu(1L)).willReturn(1);
    service.deleteMenu(1L);
  }
  
  @Test(expected = RuntimeException.class)
  public void deleteMenu_메뉴_삭제_실패() {
    given(mapper.deleteMenu(1L)).willReturn(0);
    service.deleteMenu(1L);
  }
  
  @Test
  public void checkMenuTest_메뉴존재_체크_성공() {
    given(mapper.checkMenu(1L, 1L)).willReturn(1);
    service.checkMenu(1L, 1L);
  }
  
  @Test(expected = RuntimeException.class)
  public void checkMenuTest_메뉴존재_체크_없음() {
    given(mapper.checkMenu(1L, 1L)).willReturn(0);
    service.checkMenu(1L, 1L);
  }
  
  @Test(expected = RuntimeException.class)
  public void checkMenuTest_메뉴존재_체크_실패() {
    given(mapper.checkMenu(1L, 1L)).willReturn(999);
    service.checkMenu(1L, 1L);
  }
  
  @Test
  public void updateMenuPriorityTest_메뉴_순서_변경_성공() {
    given(mapper.totalCount(1L)).willReturn(3);
    given(mapper.updateMenuPriority(anyLong(), anyInt())).willReturn(1);
    List<Long> idList = LongStream.of(1,2,3).boxed().collect(Collectors.toList());
    
    service.updateMenuPriority(1L, idList);
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMenuPriorityTest_메뉴_순서_변경_실패() {
    given(mapper.totalCount(1L)).willReturn(0);
    List<Long> idList = LongStream.of(1,2,3).boxed().collect(Collectors.toList());
    
    service.updateMenuPriority(1L, idList);
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMenuPriorityTest_메뉴_순서_변경_실패2() {
    given(mapper.totalCount(1L)).willReturn(3);
    given(mapper.updateMenuPriority(anyLong(), anyInt())).willReturn(0);
    List<Long> idList = LongStream.of(1,2,3).boxed().collect(Collectors.toList());
    
    service.updateMenuPriority(1L, idList);
  }
  
  @Test
  public void updateMenuTest_메뉴_정보_수정_성공() {
    MenuDTO menu = generateMenu();
    given(mapper.updateMenu(menu)).willReturn(1);
    service.updateMenu(menu);
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMenuTest_메뉴_정보_수정_실패() {
    MenuDTO menu = generateMenu();
    given(mapper.updateMenu(menu)).willReturn(0);
    service.updateMenu(menu);
  }
  
  
  
}
