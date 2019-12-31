package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.delfood.dao.CartDao;
import com.delfood.dto.ItemDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.dto.ItemDTO.CacheMenuDTO;
import com.delfood.dto.ItemDTO.CacheOptionDTO;
import com.delfood.dto.ItemDTO.CacheShopDTO;
import com.delfood.error.exception.cart.DuplicateItemException;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {
  @InjectMocks
  CartService cartService;
  
  @Mock
  CartDao cartDao;
  
  public ItemDTO generateItem111() {
    CacheMenuDTO menuInfo = new CacheMenuDTO(111L, "테스트 메뉴 111", 11000L);
    
    List<CacheOptionDTO> options = new ArrayList<ItemDTO.CacheOptionDTO>();
    CacheOptionDTO optionInfo1 = new CacheOptionDTO(1L, "111 옵션 1", 100L);
    CacheOptionDTO optionInfo2 = new CacheOptionDTO(1L, "111 옵션 2", 200L);
    CacheOptionDTO optionInfo3 = new CacheOptionDTO(1L, "111 옵션 3", 300L);
    options.add(optionInfo1);
    options.add(optionInfo2);
    options.add(optionInfo3);
    
    CacheShopDTO shopInfo = new CacheShopDTO(222L, "테스트 매장 이름");
    
    ItemDTO itemInfo = new ItemDTO(menuInfo, options, 1, 11600L, shopInfo);
    
    return itemInfo;
  }
  
  public ItemDTO generateItem222() {
    CacheMenuDTO menuInfo = new CacheMenuDTO(222L, "테스트 메뉴 222", 11000L);
    
    List<CacheOptionDTO> options = new ArrayList<ItemDTO.CacheOptionDTO>();
    CacheOptionDTO optionInfo1 = new CacheOptionDTO(2L, "222 옵션 1", 100L);
    CacheOptionDTO optionInfo2 = new CacheOptionDTO(2L, "222 옵션 2", 200L);
    CacheOptionDTO optionInfo3 = new CacheOptionDTO(2L, "222 옵션 3", 300L);
    options.add(optionInfo1);
    options.add(optionInfo2);
    options.add(optionInfo3);
    
    CacheShopDTO shopInfo = new CacheShopDTO(222L, "테스트 매장 이름");
    
    ItemDTO itemInfo = new ItemDTO(menuInfo, options, 1, 11600L, shopInfo);
    
    return itemInfo;
  }
  
  public ItemDTO generateItemAnotherShop() {
    CacheMenuDTO menuInfo = new CacheMenuDTO(222L, "테스트 메뉴 222", 11000L);
    
    List<CacheOptionDTO> options = new ArrayList<ItemDTO.CacheOptionDTO>();
    CacheOptionDTO optionInfo1 = new CacheOptionDTO(2L, "222 옵션 1", 100L);
    CacheOptionDTO optionInfo2 = new CacheOptionDTO(2L, "222 옵션 2", 200L);
    CacheOptionDTO optionInfo3 = new CacheOptionDTO(2L, "222 옵션 3", 300L);
    options.add(optionInfo1);
    options.add(optionInfo2);
    options.add(optionInfo3);
    
    CacheShopDTO shopInfo = new CacheShopDTO(123L, "테스트 매장 이름");
    
    ItemDTO itemInfo = new ItemDTO(menuInfo, options, 1, 11600L, shopInfo);
    
    return itemInfo;
  }
  
  @Test
  public void addOrdersItemTest_장바구니에_메뉴_추가() {
    ItemDTO item1 = generateItem111();
    ItemDTO item2 = generateItem222();
    given(cartDao.findPeekByMemberId("eric")).willReturn(item1);
    cartService.addOrdersItem(item2, "eric");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addOrdersItemTest_장바구니에_다른매장_메뉴_추가() {
    ItemDTO item1 = generateItem111();
    ItemDTO item2 = generateItemAnotherShop();
    given(cartDao.findPeekByMemberId("eric")).willReturn(item1);
    cartService.addOrdersItem(item2, "eric");
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void addOrdersItemTest_너무많은메뉴추가() {
    given(cartDao.findPeekByMemberId("eric")).willReturn(generateItem111());
    given(cartDao.findAllByMemberId("eric")).willReturn(Arrays.asList(
        new ItemDTO[] {generateItem111(), generateItem111(), generateItem111(), generateItem111(),
            generateItem111(), generateItem111(), generateItem111(), generateItem111(),
            generateItem111(), generateItem111(), generateItem111(), generateItem111()}));
    cartService.addOrdersItem(generateItem222(), "eric");
  }
  
  @Test(expected = DuplicateItemException.class)
  public void addOrdersItemTest_같메뉴추가() {
    given(cartDao.findPeekByMemberId("eric")).willReturn(generateItem111());
    given(cartDao.findAllByMemberId("eric")).willReturn(Arrays.asList(
        new ItemDTO[] {generateItem111()}));
    cartService.addOrdersItem(generateItem111(), "eric");
  }
  
  @Test
  public void deleteCartMenuTest_장바구니_메뉴_삭제_인덱스() {
    given(cartDao.getMenuCount("eric")).willReturn(5L);
    given(cartDao.deleteByMemberIdAndIndex("eric", 1L)).willReturn(true);
    cartService.deleteCartMenu("eric", 1L);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void deleteCartMenuTest_장바구니_메뉴_삭제_인덱스_초과() {
    given(cartDao.getMenuCount("eric")).willReturn(5L);
    cartService.deleteCartMenu("eric", 6L);
  }
  
  @Test(expected = RuntimeException.class)
  public void deleteCartMenuTest_장바구니_메뉴_삭제_redis에러() {
    given(cartDao.getMenuCount("eric")).willReturn(5L);
    given(cartDao.deleteByMemberIdAndIndex("eric", 1L)).willReturn(false);
    cartService.deleteCartMenu("eric", 1L);
  }
  
  @Test
  public void containsEqualItemTest_장바구니_동일아이템_포함여부_검사() {
    given(cartDao.findAllByMemberId("eric"))
        .willReturn(Arrays.asList(new ItemDTO[] {generateItem111()}));
    
    assertThat(cartService.containsEqualItem("eric", generateItem111())).isEqualTo(true);
    assertThat(cartService.containsEqualItem("eric", generateItem222())).isEqualTo(false);
  }
  
  @Test
  public void allPriceTest_장바구니_총가격_계산() {
    given(cartDao.findAllByMemberId("eric"))
        .willReturn(Arrays.asList(new ItemDTO[] {generateItem111(), generateItem222()}));
    assertThat(cartService.allPrice("eric")).isEqualTo(23200L);
  }
  
  @Test
  public void priceTest_아이템_가격계산() {
    ItemDTO itemInfo = generateItem111();
    assertThat(cartService.price(itemInfo)).isEqualTo(11600L);
  }
  
  @Test
  public void menuPriceTest_아이템_메뉴만_가격계산() {
    ItemDTO itemInfo = generateItem111();
    assertThat(CartService.menuPrice(itemInfo)).isEqualTo(11000L);
  }
  
  @Test
  public void menuPriceTest_아이템_옵션만_가격계산() {
    ItemDTO itemInfo = generateItem111();
    assertThat(CartService.optionsPrice(itemInfo)).isEqualTo(600L);
  }
  
}
