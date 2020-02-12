package com.delfood.service.delivery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.delfood.dao.deliveery.DeliveryDao;
import com.delfood.dto.address.Position;
import com.delfood.dto.push.PushMessage;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.service.PushService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryServiceTest {

  @InjectMocks
  public DeliveryService deliveryService;

  @Mock
  public DeliveryDao deliveryDao;

  @Mock
  public PushService pushService;

  public List<DeliveryRiderDTO> generateRiders() {
    List<DeliveryRiderDTO> riderList = new ArrayList<>();
    riderList.add(DeliveryRiderDTO.builder().riderId("rider001")
        .position(Position.builder().coordinateX(500d).coordinateY(400d).build()).build()); // 500m
    riderList.add(DeliveryRiderDTO.builder().riderId("rider002")
        .position(Position.builder().coordinateX(900d).coordinateY(750.235d).build()).build()); // 1030m
    riderList.add(DeliveryRiderDTO.builder().riderId("rider003")
        .position(Position.builder().coordinateX(100.213d).coordinateY(0.222).build()).build()); // 99.77m
    riderList.add(DeliveryRiderDTO.builder().riderId("rider004")
        .position(Position.builder().coordinateX(0d).coordinateY(9000d).build()).build()); // 8900m
    riderList.add(DeliveryRiderDTO.builder().riderId("rider005")
        .position(Position.builder().coordinateX(99999d).coordinateY(99999d).build()).build()); // 141278.5
    return riderList;
  }

  @Test
  public void deliveryRequestByDistanceTest_거리기반_푸시메세지_전송_1000미터() {
    Position position = new Position(100.0, 100.0);
    long distance = 1000L; // 1Km
    
    List<DeliveryRiderDTO> riderList = generateRiders();
    given(deliveryDao.getRiderList()).willReturn(riderList);
    doNothing().when(pushService).sendMessageToRider(any(), anyString());
    
    deliveryService.deliveryRequestByDistance(position, distance);
    
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider001");
    verify(pushService, times(0)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider002");
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider003");
    verify(pushService, times(0)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider004");
    verify(pushService, times(0)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider005");
  }
  
  @Test
  public void deliveryRequestByDistanceTest_거리기반_푸시메세지_전송_100000미터() {
    Position position = new Position(100.0, 100.0);
    long distance = 100000L; // 1Km
    
    List<DeliveryRiderDTO> riderList = generateRiders();
    given(deliveryDao.getRiderList()).willReturn(riderList);
    doNothing().when(pushService).sendMessageToRider(any(), anyString());
    
    deliveryService.deliveryRequestByDistance(position, distance);
    
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider001");
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider002");
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider003");
    verify(pushService, times(1)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider004");
    verify(pushService, times(0)).sendMessageToRider(PushMessage.DELIVERY_REQUEST, "rider005");
  }
  
}
