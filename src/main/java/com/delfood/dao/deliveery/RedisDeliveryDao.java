package com.delfood.dao.deliveery;

import com.delfood.dto.order.OrderDTO.OrderStatus;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.service.OrderService;
import com.delfood.utils.RedisKeyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository("redisDeliveryDao")
public class RedisDeliveryDao implements DeliveryDao {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Value("${expire.rider}")
  public Long expireTime;

  private OrderService orderService;

  @Override
  public void updateRiderInfo(DeliveryRiderDTO riderInfo) {
    redisTemplate.opsForHash().put(RedisKeyFactory.DELIVERY_KEY, riderInfo.getRiderId(), riderInfo);
  }

  @Override
  public boolean deleteRiderInfo(String riderId) {
    return redisTemplate.opsForHash().delete(RedisKeyFactory.DELIVERY_KEY, riderId) != null;
  }

  @Override
  public boolean hasRiderInfo(String riderId) {
    return redisTemplate.opsForHash().hasKey(RedisKeyFactory.DELIVERY_KEY, riderId);
  }

  /**
   * 특정 시점을 기준으로 일정시간 자신의 위치를 업데이트하지 않은 라이더들을 삭제한다. 일정 시점을 기준으로 하기에 동시성 제어가 필요하지 않다.
   * 
   * @author jun
   */
  @Override
  public void deleteNonUpdatedRiders() {
    LocalDateTime now = LocalDateTime.now();
    Set<Object> keys = redisTemplate.opsForHash().keys(RedisKeyFactory.DELIVERY_KEY);
    keys.stream().forEach(key -> {
      Object obj = redisTemplate.opsForHash().get(RedisKeyFactory.DELIVERY_KEY, key);
      if (Objects.isNull(obj) == false) {
        DeliveryRiderDTO riderInfo = objectMapper.convertValue(obj, DeliveryRiderDTO.class);
        if (ChronoUnit.SECONDS.between(riderInfo.getUpdatedAt(), now) > expireTime) {
          redisTemplate.opsForHash().delete(RedisKeyFactory.DELIVERY_KEY, key);
          log.info("Rider '{}' (이)가 스케줄에 의해 삭제됨", key);
        }
      }
    });
  }

  @Override
  public DeliveryRiderDTO getRiderInfo(String riderId) {
    return objectMapper.convertValue(
        redisTemplate.opsForHash().get(RedisKeyFactory.DELIVERY_KEY, riderId),
        DeliveryRiderDTO.class);
  }

  @Override
  public List<DeliveryRiderDTO> getRiderList() {
    redisTemplate.watch(RedisKeyFactory.DELIVERY_KEY);
    List<DeliveryRiderDTO> riderList;
    try {
      List<Object> objList = redisTemplate.opsForHash().values(RedisKeyFactory.DELIVERY_KEY);
      riderList = objList.stream().map(e -> objectMapper.convertValue(e, DeliveryRiderDTO.class))
          .collect(Collectors.toList());
    } finally {
      redisTemplate.unwatch();
    }
    return riderList;
  }

  @Override
  public void deleteAll(List<String> idList) {
    redisTemplate.watch(RedisKeyFactory.DELIVERY_KEY);
    try {
      redisTemplate.multi();
      idList.stream().forEach(id -> redisTemplate.delete(id));
      redisTemplate.exec();
    } catch (Exception e) {
      redisTemplate.discard();
    } finally {
      redisTemplate.unwatch();
    }
  }

  /**
   * Redis에 주문 상태 정보가 저장되어 있는지 확인한 후, 저장되어있으면 조회 후 리턴한다. 저장된 주문 정보가 없을 시 RDB에서 조회해 와 Redis에 저장한 후
   * 리턴한다.
   * 
   * @author jun
   */
  @Override
  public OrderStatus getOrderStatus(Long orderId) {
    OrderStatus status;
    redisTemplate.watch(RedisKeyFactory.ORDER_KEY);
    try {
      Object object = redisTemplate.opsForHash().get(RedisKeyFactory.ORDER_KEY, orderId);
      if (Objects.isNull(object)) {
        status = orderService.getOrderStatus(orderId);
        redisTemplate.opsForHash().put(RedisKeyFactory.ORDER_KEY, orderId, status);
      } else {
        status = objectMapper.convertValue(object, OrderStatus.class);
      }
    } finally {
      redisTemplate.unwatch();
    }
    return status;
  }

  @Override
  public void setOrderStatus(Long orderId, OrderStatus status) {
    redisTemplate.opsForHash().put(RedisKeyFactory.ORDER_KEY, orderId, status);
  }

  @Override
  public void deleteOrderStatus(Long orderId) {
    redisTemplate.opsForHash().delete(RedisKeyFactory.ORDER_KEY, orderId);
  }
}
