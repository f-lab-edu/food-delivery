package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.delfood.dao.CartDao;
import com.delfood.dto.order.item.ItemDTO;
import com.delfood.dto.order.item.ItemDTO.CacheMenuDTO;
import com.delfood.dto.order.item.ItemDTO.CacheOptionDTO;
import com.delfood.dto.order.item.ItemDTO.CacheShopDTO;
import com.delfood.dto.shop.ShopDTO;
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
  
  /**
   * id에 따른 Item을 생산하여 리턴한다.
   * @author jun
   * @param id 해당 아이디를 기반으로 메뉴와 옵션을 생산하여 아이템을 제작한다.
   * @return
   */
  public ItemDTO generateItem(long id) {
    final long menuId = id * 111;
    final long shopId = 222L;
    final long menuPrice = 11000L;
    final long optionId = id;
    final long[] optionPrices = {100L, 200L, 300L};
    
    CacheMenuDTO menuInfo = new CacheMenuDTO(menuId, "테스트 메뉴 " + menuId, menuPrice);

    List<CacheOptionDTO> options = new ArrayList<ItemDTO.CacheOptionDTO>();
    CacheOptionDTO optionInfo1 = new CacheOptionDTO(optionId, menuId + " 옵션 1", optionPrices[0]);
    CacheOptionDTO optionInfo2 =
        new CacheOptionDTO(optionId * 2L, menuId + " 옵션 2", optionPrices[1]);
    CacheOptionDTO optionInfo3 =
        new CacheOptionDTO(optionId * 3L, menuId + " 옵션 3", optionPrices[2]);
    options.add(optionInfo1);
    options.add(optionInfo2);
    options.add(optionInfo3);

    CacheShopDTO shopInfo = new CacheShopDTO(shopId, "테스트 매장 이름");

    ItemDTO itemInfo = new ItemDTO(menuInfo, options, 1, menuPrice + 600L, shopInfo);

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
    ItemDTO item1 = generateItem(1L);
    ItemDTO item2 = generateItem(2L);
    given(cartDao.findPeekByMemberId("eric")).willReturn(item1);
    cartService.addOrdersItem(item2, "eric");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addOrdersItemTest_장바구니에_다른매장_메뉴_추가() {
    ItemDTO item1 = generateItem(1L);
    ItemDTO item2 = generateItemAnotherShop();
    given(cartDao.findPeekByMemberId("eric")).willReturn(item1);
    cartService.addOrdersItem(item2, "eric");
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void addOrdersItemTest_너무많은메뉴추가() {
    given(cartDao.findPeekByMemberId("eric")).willReturn(generateItem(1L));
    given(cartDao.findAllByMemberId("eric")).willReturn(Arrays.asList(
        new ItemDTO[] {generateItem(1L), generateItem(1L), generateItem(1L), generateItem(1L),
            generateItem(1L), generateItem(1L), generateItem(1L), generateItem(1L),
            generateItem(1L), generateItem(1L), generateItem(1L), generateItem(1L)}));
    cartService.addOrdersItem(generateItem(2L), "eric");
  }
  
  @Test(expected = DuplicateItemException.class)
  public void addOrdersItemTest_같메뉴추가() {
    given(cartDao.findPeekByMemberId("eric")).willReturn(generateItem(1L));
    given(cartDao.findAllByMemberId("eric")).willReturn(Arrays.asList(
        new ItemDTO[] {generateItem(1L)}));
    cartService.addOrdersItem(generateItem(1L), "eric");
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
        .willReturn(Arrays.asList(new ItemDTO[] {generateItem(1L)}));
    
    assertThat(cartService.containsEqualItem("eric", generateItem(1L))).isEqualTo(true);
    assertThat(cartService.containsEqualItem("eric", generateItem(2L))).isEqualTo(false);
  }
  
  @Test
  public void allPriceTest_장바구니_총가격_계산() {
    given(cartDao.findAllByMemberId("eric"))
        .willReturn(Arrays.asList(new ItemDTO[] {generateItem(1L), generateItem(2L)}));
    assertThat(cartService.allPrice("eric")).isEqualTo(23200L);
  }
  
  @Test
  public void priceTest_아이템_가격계산() {
    ItemDTO itemInfo = generateItem(1L);
    assertThat(cartService.price(itemInfo)).isEqualTo(11600L);
  }
  
  @Test
  public void menuPriceTest_아이템_메뉴만_가격계산() {
    ItemDTO itemInfo = generateItem(1L);
    assertThat(CartService.menuPrice(itemInfo)).isEqualTo(11000L);
  }
  
  @Test
  public void menuPriceTest_아이템_옵션만_가격계산() {
    ItemDTO itemInfo = generateItem(1L);
    assertThat(CartService.optionsPrice(itemInfo)).isEqualTo(600L);
  }
  
}
