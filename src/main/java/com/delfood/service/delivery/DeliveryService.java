package com.delfood.service.delivery;

import com.delfood.dao.deliveery.DeliveryDao;
import com.delfood.dto.address.Position;
import com.delfood.dto.push.PushMessage;
import com.delfood.dto.push.PushMessage.Type;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.service.PushService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
  
  private static final long SCHEDULE_DELETE_DELIVERY_RIDER_SECOND = 300000;
  
  @Autowired
  @Qualifier("multiThreadDeliveryDao")
  private DeliveryDao deliveryDao;
  
  @Autowired
  private PushService pushService;
  
  /**
   * 라이더의 정보를 업데이트한다.
   * 라이더의 정보가 저장되어 있지 않을 시 저장소에 라이더 정보를 추가한다.
   * @param riderInfo 추가할 라이더의 정보
   */
  public void updateRider(@NonNull DeliveryRiderDTO riderInfo) {
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
    deliveryDao.deleteNonUpdatedRiders();
  }
  
  
  /**
   * 거리를 기반으로 근처 라이더들에게 푸시 메세지를 전송한다.
   * @param position 조회할 위치의 중심 점
   * @param distance 중심점에서 몇 미터까지 메세지를 보낼지 정한다.
   */
  public void deliveryRequestByDistance(Position position, long distance) {
    deliveryDao.toList().stream().filter(e -> e.getPosition().distanceMeter(position) < distance)
        .sorted((e1, e2) -> {
          double distanceE1 = position.distanceMeter(e1.getPosition());
          double distanceE2 = position.distanceMeter(e2.getPosition());
          return distanceE1 - distanceE2 > 0 ? 1 : distanceE1 - distanceE2 < 0 ? -1 : 0;
        }).forEach(e -> pushService
            .sendMessageToRider(PushMessage.getMessasge(Type.deliveryRequest), e.getId()));
  }

}
