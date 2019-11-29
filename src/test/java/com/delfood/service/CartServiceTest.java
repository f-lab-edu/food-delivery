package com.delfood.service;

import static org.mockito.BDDMockito.given;

import com.delfood.dao.CartDao;
import com.delfood.dto.OrdersItemDTO;
import com.delfood.dto.OrdersItemOptionDTO;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {
  @InjectMocks
  CartService service;
  
  @Mock
  CartDao dao;
  
  public OrdersItemDTO generateOrdersItemDTO() {
    OrdersItemDTO item = new OrdersItemDTO();
    item.setId(1L);
    item.setMenuId(1L);
    item.setOptions(new ArrayList<OrdersItemOptionDTO>());
    item.setOrderId(null);
    item.setShopId(4L);
    item.setCount(1L);
    return item;
  }
  
  public List<OrdersItemDTO> generateOrdersItems() {
    List<OrdersItemDTO> items = new ArrayList<OrdersItemDTO>();
    for (long i = 1; i <= 5; i++) {
      OrdersItemDTO item = generateOrdersItemDTO();
      item.setId(i);
      items.add(item);
    }
    return items;
  }
  
  @Test
  public void addOrdersItemTest_장바구니_메뉴저장_성공() {
    OrdersItemDTO peekItem = generateOrdersItemDTO();
    OrdersItemDTO addItem = generateOrdersItemDTO();
    given(dao.findPeekByMemberId("eric")).willReturn(peekItem);
    given(dao.addItem(addItem, "eric")).willReturn(1L);
    
    service.addOrdersItem(addItem, "eric");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addOrdersItemTest_장바구니_메뉴저장_실패_다른매장_메뉴() {
    OrdersItemDTO peekItem = generateOrdersItemDTO();
    OrdersItemDTO addItem = generateOrdersItemDTO();
    
    addItem.setShopId(999L);
    given(dao.findPeekByMemberId("eric")).willReturn(peekItem);
    
    service.addOrdersItem(addItem, "eric");
  }
  
  @Test
  public void getOrdersItemsTest_장바구니_조회_성공() {
    List<OrdersItemDTO> items = new ArrayList<OrdersItemDTO>();
    for (long i = 1; i <= 5; i++) {
      OrdersItemDTO item = generateOrdersItemDTO();
      item.setId(i);
      items.add(item);
    }
    given(dao.findAllByMemberId("eric")).willReturn(items);
    
    service.getOrdersItems("eric");
  }
  
  @Test
  public void clearTest_장바구니_비우기_성공() {
    given(dao.deleteByMemberId("eric")).willReturn(true);
    service.claer("eric");
  }
  
  
  @Test
  public void deleteCartMenu_장바구니_메뉴_제거_성공() {
    OrdersItemDTO item = generateOrdersItemDTO();
    List<OrdersItemDTO> items = generateOrdersItems();
    given(dao.getMenuCount("eric")).willReturn((long)items.size());
    given(dao.deleteByMemberIdAndIndex("eric", 0L)).willReturn(true);
    
    service.deleteCartMenu("eric", 0L);
  }
  
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void deleteCartMenu_장바구니_메뉴_제거_실패_인덱스이상() {
    List<OrdersItemDTO> items = generateOrdersItems();
    given(dao.getMenuCount("eric")).willReturn(items.size() - 200L);
    
    service.deleteCartMenu("eric", 999L);
  }
  
  
  @Test(expected = RuntimeException.class)
  public void deleteCartMenu_장바구니_메뉴_제거_실패_redis실패() {
    List<OrdersItemDTO> items = generateOrdersItems();
    given(dao.getMenuCount("eric")).willReturn((long)items.size());
    given(dao.deleteByMemberIdAndIndex("eric", 0L)).willReturn(false);
    
    service.deleteCartMenu("eric", 0L);
  }
}
