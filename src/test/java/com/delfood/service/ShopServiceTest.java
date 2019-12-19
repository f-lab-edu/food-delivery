package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import com.delfood.dto.DeliveryLocationDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopDTO.DeliveryType;
import com.delfood.dto.ShopDTO.OrderType;
import com.delfood.dto.ShopDTO.Status;
import com.delfood.dto.ShopUpdateDTO;
import com.delfood.error.exception.shop.CanNotCloseShopException;
import com.delfood.error.exception.shop.CanNotOpenShopException;
import com.delfood.mapper.DeliveryLocationMapper;
import com.delfood.mapper.ShopMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.HttpStatusCodeException;

@RunWith(MockitoJUnitRunner.class)
public class ShopServiceTest {

  @InjectMocks
  private ShopService shopService;
  
  @Mock
  private ShopMapper shopMapper;
  
  @Mock
  private WorkService workService;
  
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
   * 매장 추가 실패 테스트.
   * : 시그니처 메뉴가 없는 아이디 일 때
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseMenuDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setSignatureMenuId(-1L);
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);

    shopService.addShop(shopInfo);
  }
  
  /**
   * 매장 추가 실패 테스트.
   * : Owner가 없는 아이디 일 때
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseOwnerDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setOwnerId("noExistId");
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);

    shopService.addShop(shopInfo);
  }
  
  /**
   * 매장 추가 실패 테스트.
   * : address가 없는 아이디 일 때
   */
  @Test(expected = RuntimeException.class)
  public void addShopFailBeacauseAddressDoesNotExist() {
    ShopDTO shopInfo = generateShop();
    shopInfo.setAddressCode("0000000000000000000000000");
    
    given(shopMapper.insertShop(shopInfo)).willReturn(0);
    
    shopService.addShop(shopInfo);
  }
  
  /**
   * 사장님 가게 정보 조회 성공 테스트.
   */
  @Test
  public void getMyShopsSuccess() {
    String ownerId = "ljy2134";
    Long lastId = 999L;
    List<ShopDTO> shops = new ArrayList<>();
    shops.add(generateShop());
    
    given(shopMapper.findByOwnerId(ownerId, lastId)).willReturn(shops);
    
    shopService.getMyShops(ownerId, lastId);
  }
  
  /**
   * 사장님이 가진 총 가게수 조회 성공 테스트.
   */
  @Test
  public void getMyShopCountSuccess() {
    String ownerId = "ljy2134";

    given(shopMapper.countByOwnerId(ownerId)).willReturn(1L);
    
    shopService.getMyShopCount(ownerId);
  }
  
  /**
   * 한 가게의 정보 조회 성공 테스트.
   */
  @Test
  public void getMyShopInfoSuccess() {
    Long id = 999L;
    Long id2 = 444L;
    
    given(shopMapper.findById(id)).willReturn(generateShop());
    given(shopMapper.findById(id2)).willReturn(null);
    
    shopService.getMyShopInfo(id);
    shopService.getMyShopInfo(id2);
  }
  
  /**
   * 사장아이디, 가게아이디 검증 성공 테스트.
   */
  public void checkShopIdSuccess() {
    String ownerId = "ljy2134";
    Long shopId1 = 999L;
    Long shopId2 = 333L;
    
    given(shopMapper.countByOwnerIdAndShopId(ownerId, shopId1)).willReturn(1);
    given(shopMapper.countByOwnerIdAndShopId(ownerId, shopId2)).willReturn(0);
    
    assertThat(shopService.checkShopId(ownerId, shopId1)).isTrue();
    assertThat(shopService.checkShopId(ownerId, shopId2)).isFalse();
  }
  
  /**
   * 매장 주인이 사장님인지 검증 성공 테스트.
   */
  public void isShopOwnerSuccess() {
    String ownerId = "ljy2134";
    Long shopId1 = 999L;
    Long shopId2 = 333L;
    
    given(shopMapper.countByShopIdAndOwnerId(shopId1, ownerId)).willReturn(1L);
    given(shopMapper.countByShopIdAndOwnerId(shopId2, ownerId)).willReturn(0L);
    
    assertThat(shopService.isShopOwner(shopId1, ownerId)).isTrue();
    assertThat(shopService.isShopOwner(shopId2, ownerId)).isFalse();
  }
 
  /**
   * 매장 주인이 사장님인지 검증 실패 테스트.
   * : 파라미터 중 하나의 데이터가 null인 경우
   */
  @Test(expected = NullPointerException.class)
  public void isShopOwnerFailBecauseNullData() {
    String ownerId = "ljy2134";
    Long shopId = 999L;
    
    shopService.isShopOwner(null, ownerId);
    shopService.isShopOwner(shopId, null);
  }
  
  /**
   * 매장 정보 업데이트 성공 테스트.
   */
  @Test
  public void updatShopSuccess() {
    ShopUpdateDTO updateInfo = new ShopUpdateDTO();
    updateInfo.setId(999L);
    updateInfo.setOrderType(OrderType.MEET_PAYMENT);
    
    given(shopMapper.updateShop(updateInfo)).willReturn(1);
    
    shopService.updateShop(updateInfo);
  }
  
  /**
   * 매장 OPEN 체크 성공 테스트.
   */
  @Test
  public void notOpenCheckSuccess() {
    Long shopId1 = 999L;
    Long shopId2 = 998L;
    
    given(shopMapper.countByIdIsNotOpen(shopId1)).willReturn(1L);
    given(shopMapper.countByIdIsNotOpen(shopId2)).willReturn(0L);
    
    assertThat(shopService.notOpenCheck(shopId1)).isTrue();
    assertThat(shopService.notOpenCheck(shopId2)).isFalse();
  }
  
  /**
   * 매장 영업 시작 성공 테스트.
   */
  @Test
  public void openShopSuccess() {
    Long shopId = 999L;
    
    given(shopMapper.countByIdIsNotOpen(shopId)).willReturn(1L);
    given(shopMapper.updateShopOpenById(shopId)).willReturn(1);
    
    shopService.openShop(shopId);
  }
  
  /**
   * 매장 영업 시작 실패 테스트.
   * : 이미 영업 시작 상태
   */
  @Test(expected = CanNotOpenShopException.class)
  public void openShopFailBecauseShopIsalreadyOpen() {
    Long shopId = 999L;
    
    given(shopMapper.countByIdIsNotOpen(shopId)).willReturn(0L);
    
    shopService.openShop(shopId);
  }
  
  /**
   * 매장 영업 종료 성공 테스트.
   */
  @Test
  public void closeShopSuccess() {
    Long shopId = 999L;
    
    given(shopMapper.countByIdIsNotOpen(shopId)).willReturn(0L);
    given(shopMapper.updateShopCloseById(shopId)).willReturn(1);
    
    shopService.closeShop(shopId);
  }
  
  /**
   * 매장 영업 종료 실패 테스트. 
   * : 이미 영업 종료 상태
   * 
   */
  @Test(expected = CanNotCloseShopException.class)
  public void closeShopFailBecauseShopIsAlreadyClose() {
    Long shopId = 999L;
    
    given(shopMapper.countByIdIsNotOpen(shopId)).willReturn(1L);
    
    shopService.closeShop(shopId);
  }

  /**
   * 배달가능지역 추가 성공 테스트.
   */
  @Test
  public void addDeliveryLocationSuccessTest() {
    Long shopId = 999L;
    Set<String> requestTownCodes = new HashSet<String>();
    for (int i = 0; i < 3; i++) {
      requestTownCodes.add("123456789" + i); 
    }
    
    given(deliveryLocationMapper.insertDeliveryLocation(shopId, requestTownCodes)).willReturn(3);
    
    shopService.addDeliveryLocation(shopId, requestTownCodes);
  }
  
  /**
   * 배달가능지역 추가 실패 테스트.
   * : 중복된 배달 지역 추가 시도
   */
  @Test(expected = HttpStatusCodeException.class)
  public void addDeliveryLocationFailBecauseDuplicateTownCode() {
    Long shopId = 999L;
    Set<String> requestTownCodes = new HashSet<String>();
    
    given(deliveryLocationMapper.insertDeliveryLocation(shopId, requestTownCodes))
      .willThrow(DataIntegrityViolationException.class);
    
    shopService.addDeliveryLocation(shopId, requestTownCodes);
  }
  
  /**
   * 배달가능지역 권한 검증 성공 테스트.
   */
  @Test
  public void isShopOwnerByDeliveryLocationIdSuccess() {
    Long deliveryLocationId1 = 10L;
    Long deliveryLocationId2 = 11L;
    String ownerId = "ljy2134";
    
    given(deliveryLocationMapper.countByOwnerIdAndDeliveryLocationId(deliveryLocationId1, ownerId))
      .willReturn(1L);
    given(deliveryLocationMapper.countByOwnerIdAndDeliveryLocationId(deliveryLocationId2, ownerId))
      .willReturn(0L);
    
    assertThat(shopService.isShopOwnerByDeliveryLocationId(deliveryLocationId1, ownerId)).isTrue();
    assertThat(shopService.isShopOwnerByDeliveryLocationId(deliveryLocationId2, ownerId)).isFalse();
  }
  
  /**
   * 배달가능지역 삭제 성공 테스트.
   */
  @Test
  public void deleteDeliveryLocationSuccess() {
    Long deliveryLocationId = 10L;
    
    given(deliveryLocationMapper.deleteDeliveryLocation(deliveryLocationId)).willReturn(1);
    
    shopService.deleteDeliveryLocation(deliveryLocationId);
  }

  /**
   * 매장 정보 조회 성공 테스트.
   */
  @Test
  public void getShopSuccess() {
    ShopDTO shopInfo = generateShop();
    
    given(shopMapper.findById(shopInfo.getId())).willReturn(shopInfo);
    
    assertThat(shopService.getShop(shopInfo.getId())).isEqualTo(shopInfo);
  }
  
  /**
   * 배달 가능 지역 조회 성공 테스트.
   */
  @Test
  public void getDeliveryLocationsSuccess() {
    Long shopId = 10L;
    List<DeliveryLocationDTO> deliveryLocations = new ArrayList<>();
    
    given(deliveryLocationMapper.findByShopId(shopId)).willReturn(deliveryLocations);
    
    assertThat(shopService.getDeliveryLocations(shopId)).isEqualTo(deliveryLocations);
  }
  
}
