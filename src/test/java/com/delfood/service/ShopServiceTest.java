package com.delfood.service;

import static org.mockito.BDDMockito.given;
import com.delfood.dto.shop.ShopDTO;
import com.delfood.dto.shop.ShopDTO.DeliveryType;
import com.delfood.dto.shop.ShopDTO.OrderType;
import com.delfood.dto.shop.ShopDTO.Status;
import com.delfood.mapper.DeliveryLocationMapper;
import com.delfood.mapper.ShopMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ShopServiceTest {

  @InjectMocks
  private ShopService shopService;
  
  @Mock
  private ShopMapper shopMapper;
  
  @Mock
  private WorkService workService;
  
  @Mock
  private AddressService addressService;
  
  @Mock
  private DeliveryLocationMapper deliveryLocationMapper;
  
  /**
   * shop 정보 생성.
   * @return
   */
  public ShopDTO generateShop() {
    return ShopDTO.builder()
        .id(999L)
        .name("교촌치킨")
        .deliveryType(DeliveryType.COMPANY_DELIVERY)
        .signatureMenuId(1L)
        .tel("02-2222-2222")
        .addressCode("1111010100100010000031108")
        .addressDetail("교촌치킨")
        .bizNumber("111-11-11111")
        .info("허니콤보 맛집")
        .minOrderPrice(10000L)
        .notice("배달료 3000원 붙습니다")
        .operatingTime("11:00 ~ 02:00")
        .ownerId("ljy2134")
        .orderType(OrderType.THIS_PAYMENT)
        .originInfo("닭 : 국내산")
        .status(Status.DEFAULT)
        .build();
  }
  
  /**
   * 매장 추가 성공 테스트.
   */
  @Test
  public void addShopSuccess() {
    ShopDTO shopInfo = generateShop();
    
    given(shopMapper.insertShop(shopInfo)).willReturn(1);
    
    shopService.addShop(shopInfo);
  }
  
  /**
   * 시그니처 메뉴가 없는 아이디 일 때 실패 테스트.
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseMenuDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setSignatureMenuId(-1L);
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);

    shopService.addShop(shopInfo);
  }
  
  /**
   * Owner가 없는 아이디 일 때 실패 테스트.
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseOwnerDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setOwnerId("noExistId");
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);

    shopService.addShop(shopInfo);
  }
  
  /**
   * address가 없는 아이디 일 때 실패 테스트.
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseAddressDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setAddressCode("0000000000000000000000000");
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);
    
    shopService.addShop(shopInfo);
  }
  
}
