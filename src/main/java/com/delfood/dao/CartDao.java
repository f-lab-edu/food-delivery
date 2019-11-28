package com.delfood.dao;

import com.delfood.dto.OrdersItemDTO;
import com.delfood.utils.RedisKeyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartDao {
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  /**
   * redis list에 해당 메뉴를 추가한다.
   * RedisKeyFactory로 고객의 아이디, 내부 키를 이용해 키를 생산한 후 메뉴를 저장시킨다.
   * @author jun
   * @param item 장바구니에 추가할 메뉴
   * @param memberId 고객 아이디
   * @return
   */
  public Long addItem(OrdersItemDTO item, String memberId) {
    return redisTemplate.opsForList().rightPush(RedisKeyFactory.generateCartKey(memberId), item);
  }
  
  /**
   * 해당 고객 장바구니에 있는 모든 메뉴를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public List<OrdersItemDTO> findAllByMemberId(String memberId) {
    List<OrdersItemDTO> items = redisTemplate.opsForList()
        .range(RedisKeyFactory.generateCartKey(memberId), 0, -1)
        .stream()
        .map(e -> objectMapper.convertValue(e, OrdersItemDTO.class))
        .collect(Collectors.toList());
    return items;
  }

  /**
   * 고객 장바구니를 비운다. redis에서 해당 키에있는 내용을 모두 삭제한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public boolean deleteByMemberId(String memberId) {
    return redisTemplate.delete(RedisKeyFactory.generateCartKey(memberId));
  }

  /**
   * 고객 장바구니에 있는 메뉴의 개수를 구한다.
   * 주문 메뉴의 총 개수가 아닌, 장바구니 list의 size를 반환한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public long getMenuCount(String memberId) {
    return redisTemplate.opsForList()
        .range(RedisKeyFactory.generateCartKey(memberId), 0, -1)
        .stream()
        .count();
  }
  
  /**
   * 고객 장바구니에서  해당 인덱스에 해당하는 메뉴를 삭제한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param index 삭제할 메뉴 인덱스
   * @return 삭제에 성공할 시 true
   */
  public boolean deleteByMemberIdAndIndex(String memberId, long index) {
    return redisTemplate.delete(RedisKeyFactory.generateCartKey(memberId));
  }
  
  /**
   * 해당 고객 장바구니의 가장 첫 번째 메뉴데이터를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return 리스트 첫 번째 메뉴데이터. 데이터가 없을 시 null
   */
  public OrdersItemDTO findPeekByMemberId(String memberId) {
    String key = RedisKeyFactory.generateCartKey(memberId);
    return redisTemplate.opsForList().size(key) == 0 ? null
        : objectMapper.convertValue(redisTemplate.opsForList().index(key, 0), OrdersItemDTO.class);
  }
}
