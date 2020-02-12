package com.delfood.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;

import com.delfood.controller.reqeust.GetAddressByZipRequest;
import com.delfood.controller.reqeust.GetAddressesByRoadRequest;
import com.delfood.dto.address.AddressDTO;
import com.delfood.dto.address.Position;
import com.delfood.mapper.AddressMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {
  
  @InjectMocks
  AddressService addressService;
  
  @Mock
  AddressMapper addressMapper;
  
  public AddressDTO generateAddressDTO() {
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
    
    return addressInfo;
  }

  @Test
  public void getTownInfoByShopIdTest_아이디로_주소_조회() {
    final Long shopId = 777L;
    AddressDTO addressInfo = generateAddressDTO();
    List<AddressDTO> addressList = new ArrayList<AddressDTO>();
    addressList.add(addressInfo);
    
    given(addressMapper.findByShopId(shopId)).willReturn(addressList);
    
    assertThat(addressService.getTownInfoByShopId(shopId), equalTo(addressList));
  }
  
  @Test
  public void getAddressByZipAddressTest_지번주소로_주소_검색() {
    AddressDTO addressInfo = generateAddressDTO();
    List<AddressDTO> addressList = new ArrayList<AddressDTO>();
    addressList.add(addressInfo);
    
    GetAddressByZipRequest searchInfo = new GetAddressByZipRequest();
    searchInfo.setBuildingNameForCity(addressInfo.getBuildingNameForCity());
    searchInfo.setBuildingNumber(addressInfo.getBuildingNumber());
    searchInfo.setBuildingSideNumber(addressInfo.getBuildingSideNumber());
    searchInfo.setTownName(addressInfo.getTownName());
    
    given(addressMapper.findByZipName(searchInfo)).willReturn(addressList);
    assertThat(addressService.getAddressByZipAddress(searchInfo), equalTo(addressList));
  }
  
  @Test
  public void getAddressByRoadNameTest_도로명주소로_주소_검색() {
    AddressDTO addressInfo = generateAddressDTO();
    List<AddressDTO> addressList = new ArrayList<AddressDTO>();
    addressList.add(addressInfo);
    
    GetAddressesByRoadRequest searchInfo = new GetAddressesByRoadRequest();
    searchInfo.setBuildingNameForCity(addressInfo.getBuildingNameForCity());
    searchInfo.setBuildingNumber(addressInfo.getBuildingNumber());
    searchInfo.setBuildingSideNumber(addressInfo.getBuildingSideNumber());
    searchInfo.setRoadName(addressInfo.getRoadName());
    
    given(addressMapper.findByRoadName(searchInfo)).willReturn(addressList);
    assertThat(addressService.getAddressByRoadName(searchInfo), equalTo(addressList));
  }
  
  @Test
  public void getDistanceMeterTest_거리_계산() {
    Position startPosition = Position.builder()
        .coordinateX(0.0)
        .coordinateY(0.0)
        .build();
    
    Position endPosition = Position.builder()
        .coordinateX(300.0)
        .coordinateY(400.0)
        .build();
    
    given(addressMapper.findPositionByAddressCode("11111111")).willReturn(startPosition);
    given(addressMapper.findPositionByAddressCode("22222222")).willReturn(endPosition);
    
    assertThat(addressService.getDistanceMeter("11111111", "22222222"), equalTo(500.0));
  }
  
  @Test
  public void deliveryPriceTest_거리기반_배달료_계산() {
    final double distances_300 = 300.0;
    final double distances_1499_9 = 1499.9;
    final double distances_1500 = 1500.0;
    final double distances_1500_1 = 1500.1;
    final double distances_2500 = 2500.0;
    final double distances_1699 = 1699.0;
    
    assertThat(addressService.deliveryPrice(distances_300), equalTo(2000L));
    assertThat(addressService.deliveryPrice(distances_1499_9), equalTo(2000L));
    assertThat(addressService.deliveryPrice(distances_1500), equalTo(2000L));
    assertThat(addressService.deliveryPrice(distances_1500_1), equalTo(2000L));
    assertThat(addressService.deliveryPrice(distances_2500), equalTo(4000L));
    assertThat(addressService.deliveryPrice(distances_1699), equalTo(2200L));
  }
  
  @Test
  public void deliveryPriceTest_아이디기반_배달료_계산() {
    Position memberPosition = Position.builder()
        .coordinateX(0.0)
        .coordinateY(0.0)
        .build();
    
    Position shopPosition = Position.builder()
        .coordinateX(3000.0)
        .coordinateY(4000.0)
        .build();
    
    given(addressMapper.findPositionByMemberId("eric")).willReturn(memberPosition);
    given(addressMapper.findPositionByShopId(555L)).willReturn(shopPosition);
    
    assertThat(addressService.deliveryPrice("eric", 555L), equalTo(9000L));
  }

}
