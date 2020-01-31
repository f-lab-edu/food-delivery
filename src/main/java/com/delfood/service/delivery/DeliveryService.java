package com.delfood.service.delivery;

import com.delfood.dao.deliveery.DeliveryDao;
import com.delfood.dto.address.Position;
import com.delfood.dto.order.OrderDTO.OrderStatus;
import com.delfood.dto.push.PushMessage;
import com.delfood.dto.rider.AcceptDeliveryRequestDTO;
import com.delfood.dto.rider.DeliveryInfoDTO;
import com.delfood.dto.rider.DeliveryOrderInfo;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.mapper.DeliveryMapper;
import com.delfood.mapper.RiderInfoMapper;
import com.delfood.service.OrderService;
import com.delfood.service.PushService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class DeliveryService {
  
  private static final long SCHEDULE_DELETE_DELIVERY_RIDER_SECOND = 300000;
  
  @Autowired
  @Qualifier("multiThreadDeliveryDao")
  private DeliveryDao deliveryDao;
  
  @Autowired
  private PushService pushService;
  
  @Autowired
  private OrderService orderService;
  
  @Autowired
  private RiderInfoMapper riderInfoMapper;
  
  @Autowired
  private DeliveryMapper deliveryMapper;
  
  /**
   * 라이더의 정보를 업데이트한다.
   * 라이더의 정보가 저장되어 있지 않을 시 저장소에 라이더 정보를 추가한다.
   * @param riderInfo 추가할 라이더의 정보
   */
  public void updateRider(@NonNull DeliveryRiderDTO riderInfo) {
    log.info("라이더 정보 수신 후 업데이트 진행. 라이더 아이디 : {}, 위치 : x - {}, y - {}", riderInfo.getRiderId(),
        riderInfo.getPosition().getCoordinateX(), riderInfo.getPosition().getCoordinateY());
    deliveryDao.updateRiderInfo(riderInfo);
  }

  public DeliveryRiderDTO getRider(@NonNull String riderId) {
    return deliveryDao.getRiderInfo(riderId);
  }
  
  /**
   * 일정 시간마다 한번씩 위치가 업데이트되지 않은 라이더들을 제거한다.
   */
  @Scheduled(fixedRate = SCHEDULE_DELETE_DELIVERY_RIDER_SECOND) // 5분에 한번씩 실행시킨다
  public void deleteBySchedule() {
    log.info("업데이트 되지 않는 라이더 삭제 시작");
    deliveryDao.deleteNonUpdatedRiders();
  }
  
  
  /**
   * 거리를 기반으로 근처 라이더들에게 푸시 메세지를 전송한다.
   * @param position 조회할 위치의 중심 점
   * @param distance 중심점에서 몇 미터까지 메세지를 보낼지 정한다.
   */
  public void deliveryRequestByDistance(Position position, long distance) {
    deliveryDao.getRiderList().stream()
        .filter(e -> e.getPosition().distanceMeter(position) < distance).sorted((e1, e2) -> {
          double distanceE1 = position.distanceMeter(e1.getPosition());
          double distanceE2 = position.distanceMeter(e2.getPosition());
          return distanceE1 - distanceE2 > 0 ? 1 : distanceE1 - distanceE2 < 0 ? -1 : 0;
        })
        .forEach(e -> pushService.sendMessageToRider(PushMessage.DELIVERY_REQUEST, e.getRiderId()));
  }

  /**
   * 배달원을 매칭한다.
   * 
   * @author jun
   * @param riderId 라이더 아이디
   * @param orderId 주문 아이디
   * @return
   */
  @Transactional
  public AcceptDeliveryRequestDTO acceptDeliveryRequest(@NonNull String riderId,
      @NonNull Long orderId) {
    OrderStatus status = deliveryDao.getOrderStatus(orderId);
    
    if (Objects.isNull(status)) {
      log.info("잘못된 주문번호를 수락 시도. 라이더 아이디 : {}, 주문 번호 : {}", riderId, orderId);
      throw new IllegalArgumentException("존재하지 않는 주문 번호입니다.");
    }
    
    AcceptDeliveryRequestDTO result;
    if (OrderStatus.ORDER_APPROVAL.equals(status)) {
      log.info("매칭 성공");
      deliveryDao.setOrderStatus(orderId, OrderStatus.IN_DELIVERY);
      orderService.setRider(orderId, riderId);
      result = AcceptDeliveryRequestDTO.builder()
                 .orderId(orderId)
                 .riderId(riderId)
                 .result(AcceptDeliveryRequestDTO.RequestResult.SUCCESS)
                 .build();
    } else {
      log.info("매칭 실패");
      result = AcceptDeliveryRequestDTO.builder()
                .orderId(orderId)
                .riderId(riderId)
                .result(AcceptDeliveryRequestDTO.RequestResult.FAIL)
                .build();
    }
    
    return result;
  }
  
  /**
   * 배달을 완료한다.
   * 현재 서버시간을 기준으로 배달 완료 시간이 기록된다.
   * 주문의 상태를 '배달 완료'로 변경시킨다.
   * @author jun
   * @param orderId 주문번호
   */
  public void deliveryComplete(@NonNull Long orderId) {
    LocalDateTime completeTime = LocalDateTime.now();
    if (OrderStatus.IN_DELIVERY.equals(deliveryDao.getOrderStatus(orderId)) == false) {
      throw new IllegalArgumentException("주문이 현제 배달중인 상태가 아닙니다.");
    }
    orderService.completeOrder(orderId, completeTime);
    deliveryDao.setOrderStatus(orderId, OrderStatus.DELIVERY_COMPLETE);
  }

  /**
   * 해당 라이더가 주문을 맡았는지 조회한다.
   * @param riderId 라이더 아이디
   * @param orderId 주문번호
   * @return
   */
  public boolean isRiderOrder(@NonNull String riderId, @NonNull Long orderId) {
    return riderInfoMapper.isRiderOrder(riderId, orderId);
  }

  public void delete(String riderId) {
    deliveryDao.deleteRiderInfo(riderId);
  }

  public List<DeliveryInfoDTO> getMyAllDeliveries(String riderId,
      @Nullable Long lastViewedOrderId) {
    return deliveryMapper.findByRiderId(riderId, lastViewedOrderId);
  }
  
  public DeliveryInfoDTO getCurrentDelivery(String riderId) {
    return deliveryMapper.findCurrentDeliveryByRiderId(riderId);
  }

  public List<DeliveryOrderInfo> getTodayDeliveryBills(String riderId) {
    LocalDate today = LocalDate.now();
    return deliveryMapper.findTodayBillsByRiderId(riderId);
  }

}
