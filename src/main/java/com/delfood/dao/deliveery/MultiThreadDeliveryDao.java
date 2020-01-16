package com.delfood.dao.deliveery;

import com.delfood.dto.OrderDTO.OrderStatus;
import com.delfood.dto.address.Position;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.service.OrderService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository(value = "multiThreadDeliveryDao")
@ThreadSafe
public class MultiThreadDeliveryDao implements DeliveryDao{
  private Map<String, DeliveryRiderDTO> riders;
  private Map<Long, OrderStatus> orders;
  
  @Value("rider.expire")
  private static Long expireTime;
  
  @Autowired
  private OrderService orderService;
  
  @PostConstruct
  public void init() {
    this.riders = new ConcurrentHashMap<String, DeliveryRiderDTO>();
    this.orders = new ConcurrentHashMap<Long, OrderStatus>();
  }
  
  /**
   * 내부 Map에 라이더 정보를 갱신한다.
   * 만약 Map 내부에 정보가 없다면 새롭게 정보를 추가한다.
   * 라이더 정보가 저장되면 라이더는 실시간으로 정보를 업데이트해야한다.
   * @param riderInfo 라이더 정보
   */
  @Override
  public void updateRiderInfo(DeliveryRiderDTO riderInfo) {
    if (riders.containsKey(riderInfo.getId())) {
      riders.replace(riderInfo.getId(), riderInfo);
    } else {
      riders.put(riderInfo.getId(), riderInfo);
    }
  }
  
  /**
   * 배달 대기를 제거한다.
   * @author jun
   * @param riderId 제거할 라이더의 아이디
   */
  @Override
  public long deleteRiderInfo(String riderId) {
    return riders.remove(riderId) == null ? 0 : 1;
  }
  
  /**
   * 해당 라이더가 저장소 내에 존재하는지 확인한다.
   * @author jun
   * @param riderId 라이더 아이디
   * @return
   */
  @Override
  public boolean hasRiderInfo(String riderId) {
    return riders.containsKey(riderId);
  }
  
  /**
   * 리스트 형태로 라이더를 조회한다.
   * @author jun
   * @return
   */
  @Override
  public List<DeliveryRiderDTO> toList() {
    return riders.values().stream().collect(Collectors.toList());
  }
  
  
  /**
   * 일정 시간동안 자신의 위치를 업데이트 하지 않는 라이더를 제거한다.
   * @author jun
   */
  @Override
  public void deleteNonUpdatedRiders() {
    riders.values().stream()
        .filter(
            e -> ChronoUnit.SECONDS.between(e.getUpdatedAt(), LocalDateTime.now()) > expireTime)
        .forEach(e -> riders.remove(e.getId()));
  }
  
  /**
   * 라이더의 정보를 조회한다.
   * @author jun
   */
  @Override
  public DeliveryRiderDTO getRiderInfo(String riderId) {
    return riders.get(riderId);
  }
  
  /**
   * 리스트로 받은 아이디를 기반으로 라이더를 배달 매칭에서 제거한다.
   * @param idList 라이더의 아이디들
   * @return 지워진 라이더 개수
   */
  @Override
  public long deleteAll(List<String> idList) {
    long deleteCount = 0;
    for (String id : idList) {
      deleteCount += deleteRiderInfo(id);
    }
    return deleteCount;
  }

  /**
   * 주문의 상태를 조회한다.
   * 주문 정보가 내부 메모리에 없다면 DB에서 조회한 후 메모리에 저장한다.
   * @author jun
   * @param orderId 조회할 주문 아이디
   */
  @Override
  public OrderStatus getOrderStatus(Long orderId) {
    OrderStatus status = orders.get(orderId);
    if (Objects.isNull(status)) {
      status = orderService.getOrderStatus(orderId);
      setOrderStatus(orderId, status);
      return status;
    }
    
    return status;
  }
  
  /**
   * 주문 정보를 내부에 저장한다.
   * @author jun
   * @param orderId 저장할 주문 아이디
   * @param status 주문의 상태
   */
  @Override
  public void setOrderStatus(Long orderId, OrderStatus status) {
    if (orders.containsKey(orderId)) {
      orders.replace(orderId, status);
    } else {
      orders.put(orderId, status);
    }
  }
  
  /**
   * 주문 정보를 삭제한다.
   * @param orderId 삭제할 주문 정보 아이디
   * @author jun
   */
  @Override
  public void deleteOrderStatus(Long orderId) {
    orders.remove(orderId);
  }
}
