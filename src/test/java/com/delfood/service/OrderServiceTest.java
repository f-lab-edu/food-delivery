package com.delfood.service;

import static org.mockito.BDDMockito.given;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.delfood.controller.response.OrderResponse;
import com.delfood.dto.address.AddressDTO;
import com.delfood.dto.address.Position;
import com.delfood.dto.member.MemberDTO;
import com.delfood.dto.order.OrderDTO;
import com.delfood.dto.order.OrderDTO.OrderStatus;
import com.delfood.dto.order.bill.ItemsBillDTO;
import com.delfood.dto.order.bill.ItemsBillDTO.MenuInfo;
import com.delfood.dto.order.bill.ItemsBillDTO.ShopInfo;
import com.delfood.dto.order.item.OrderItemDTO;
import com.delfood.dto.order.item.OrderItemOptionDTO;
import com.delfood.dto.pay.PaymentDTO;
import com.delfood.dto.shop.ShopDTO;
import com.delfood.dto.shop.ShopDTO.DeliveryType;
import com.delfood.dto.shop.ShopDTO.OrderType;
import com.delfood.dto.shop.ShopDTO.Status;
import com.delfood.mapper.AddressMapper;
import com.delfood.mapper.MemberMapper;
import com.delfood.mapper.OrderMapper;
import com.delfood.mapper.PaymentMapper;
import com.delfood.utils.OrderUtil;
import com.delfood.utils.SHA256Util;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  @InjectMocks
  OrderService orderService;

  @Mock
  OrderMapper orderMapper;

  @Mock
  MemberService memberService;

  @Mock
  ShopService shopService;

  @Mock
  AddressService addressService;
  
  @Mock
  PaymentService paymentService;
  
  @Mock
  MockPayService mockPayService;
  
  @Mock
  PushService pushService;

  public static String MEMBER_ID = "testMemberId";

  public static long SHOP_ID = 999L;

  public static long ORDER_ID = 333L;

  public static double MEMBER_POSITION_X = 30.0d;
  public static double MEMBER_POSITION_Y = 30.0d;
  public static double SHOP_POSITION_X = 30.0d;
  public static double SHOP_POSITION_Y = 30.0d;

  public static long MENU_ID = 2L;
  public static String MENU_NAME = "바삭바삭 치킨";
  public static long MENU_PRICE = 10000L;

  @Before
  public void testMockInit() {
    AddressDTO addressInfo = new AddressDTO();
    addressInfo.setAdministrativeTownCode("1111051500");
    addressInfo.setAdministrativeTownName("청운효자동");
    addressInfo.setBuildingCenterPointXCoordinate(953035.318387);
    addressInfo.setBuildingCenterPointYCoordinate(1954819.846972);
    addressInfo.setBuildingCount(9);
    addressInfo.setBuildingManagementNumber("1111010100100010000030843");
    addressInfo.setBuildingNameChangeHistory("");
    addressInfo.setBuildingNameForCity("청운벽산빌리지");
    addressInfo.setBuildingNumber(16);
    addressInfo.setBuildingSideNumber(14);
    addressInfo.setBuildingUseClassification("주택");
    addressInfo.setCityCountryName("종로구");
    addressInfo.setCityCountryNameEng("Jongno-gu");
    addressInfo.setCityName("서울특별시");
    addressInfo.setCityNameEng("Seoul");
    addressInfo.setClassificationApartmentBuildings("2");
    addressInfo.setDetailBuildingName("7동");
    addressInfo.setDetailBuildingNameChangeHistory("");
    addressInfo.setExitXCoordinate(953042.185946);
    addressInfo.setExitYCoordinate(1954799.009030);
    addressInfo.setGroundFloorNumber(3);
    addressInfo.setLivingStatus("1");
    addressInfo.setMobileReasonCode("");
    addressInfo.setRoadName("자하문로36길");
    addressInfo.setRoadNameEng("Jahamun-ro 36-gil");
    addressInfo.setTownCode("1111010100");
    addressInfo.setTownMobileClassification("1");
    addressInfo.setTownName("청운동");
    addressInfo.setTownNameEng("Cheongun-dong");
    addressInfo.setUndergroundFloorNumber(0);
    addressInfo.setUndergroundStatus("0");
    addressInfo.setZipCode("03046");

    ShopInfo shopInfo = ShopInfo.builder().id(SHOP_ID).name("delfood 치킨").build();


    MemberDTO memberInfo = new MemberDTO();
    memberInfo.setId(MEMBER_ID);
    memberInfo.setPassword(SHA256Util.encryptSHA256("testMemberPassword"));
    memberInfo.setName("testUserName");
    memberInfo.setTel("010-1111-2222");
    memberInfo.setMail("test@mail.com");
    memberInfo.setAddressCode("1111010100100010000030843");
    memberInfo.setAddressDetail("102호");
    memberInfo.setAddressInfo(addressInfo);
    memberInfo.setStatus(MemberDTO.Status.DEFAULT);
    memberInfo.setCreatedAt(LocalDateTime.now());
    memberInfo.setUpdatedAt(LocalDateTime.now());

    given(memberService.getMemberInfo(MEMBER_ID)).willReturn(memberInfo);
    given(addressService.deliveryPrice(MEMBER_ID, SHOP_ID)).willReturn(3000L);
    given(shopService.getShopByMenuId(MENU_ID)).willReturn(shopInfo);

  }

  @Test
  public void getBillTest_주문_테스트() {
    OrderItemDTO item = new OrderItemDTO();
    item.setId(OrderUtil.generateOrderItemKey(MEMBER_ID, 0));
    item.setMenuId(MENU_ID);
    item.setMenuName(MENU_NAME);
    item.setMenuPrice(MENU_PRICE);
    item.setCount(1L);
    item.setOptions(new ArrayList<OrderItemOptionDTO>());

    MenuInfo menuInfo = MenuInfo.builder().id(MENU_ID).name(MENU_NAME).price(MENU_PRICE).build();
    List<MenuInfo> menus = new ArrayList<MenuInfo>();
    menus.add(menuInfo);

    List<OrderItemDTO> orderItems = new ArrayList<OrderItemDTO>();
    orderItems.add(item);

    given(orderMapper.findItemsBill(orderItems)).willReturn(menus);
    given(orderService.doOrder(MEMBER_ID, orderItems, SHOP_ID)).willReturn(ORDER_ID);
    given(shopService.getShop(SHOP_ID)).willReturn(ShopDTO.builder().id(SHOP_ID).ownerId("testOwner").name("").build());
    
    OrderResponse order = orderService.order(MEMBER_ID, orderItems, SHOP_ID, null);
    ItemsBillDTO bill = orderService.getBill(MEMBER_ID, orderItems, null);
    
    assertThat(bill.getTotalPrice(), equalTo(13000L));
    assertThat(bill.getDeliveryInfo().getDeliveryPrice(), equalTo(3000L));
    assertThat(bill.getItemsPrice(), equalTo(10000L));
    
    assertThat(order.getBill().getMenus(), equalTo(menus));
    assertThat(order.getBill().getItemsPrice(), equalTo(10000L));
  }

}
