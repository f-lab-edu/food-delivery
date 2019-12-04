package com.delfood.dao;

import com.delfood.dto.ItemDTO;
import com.delfood.utils.RedisKeyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartDao {
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Value("${redis.expire.second.cart}")
  private long cartExpireSecond;
     
  /**
   * redis list에 해당 메뉴를 추가한다.
   * RedisKeyFactory로 고객의 아이디, 내부 키를 이용해 키를 생산한 후 메뉴를 저장시킨다.
   * @author jun
   * @param item 장바구니에 추가할 메뉴
   * @param memberId 고객 아이디
   * @return
   */
  public Long addItem(ItemDTO item, String memberId) {
    final String key = RedisKeyFactory.generateCartKey(memberId);
    
    redisTemplate.watch(key); // 해당 키를 감시한다. 변경되면 로직 취소.
    Long result = -1L;
    
    try {
      if (redisTemplate.opsForList().size(key) >= 10) {
        throw new IndexOutOfBoundsException("장바구니에는 10종류 이상 담을 수 없습니다.");
      }

      redisTemplate.multi();
      redisTemplate.opsForList().rightPush(key, item);
      redisTemplate.expire(key, cartExpireSecond, TimeUnit.SECONDS);

      redisTemplate.exec();
    } catch (Exception e) {
      redisTemplate.discard(); // 트랜잭션 종료시 unwatch()가 호출된다
      throw e;
    }
    
    return result;
  }
  
  /**
   * 해당 고객 장바구니에 있는 모든 메뉴를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public List<ItemDTO> findAllByMemberId(String memberId) {
    List<ItemDTO> items = redisTemplate.opsForList()
        .range(RedisKeyFactory.generateCartKey(memberId), 0, -1)
        .stream()
        .map(item -> objectMapper.convertValue(item, ItemDTO.class))
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
        .size();
  }
  
  /**
   * 장바구니에 있는모든 메뉴의 개수를 더하여 반환한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public long getMenuCountSum(String memberId) {
    return redisTemplate.opsForList()
        .range(RedisKeyFactory.generateCartKey(memberId), 0, -1)
        .stream()
        .mapToLong(item -> objectMapper.convertValue(item, ItemDTO.class).getCount())
        .sum();
  }
  
  /**
   * 고객 장바구니에서  해당 인덱스에 해당하는 메뉴를 삭제한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param index 삭제할 메뉴 인덱스
   * @return 삭제에 성공할 시 true
   */
  public boolean deleteByMemberIdAndIndex(String memberId, long index) {
    /*
     * opsForList().remove(key, count, value)
     * key : list를 조회할 수 있는 key
     * count > 0이면 head부터 순차적으로 조회하며 count의 절대값에 해당하는 개수만큼 제거
     * count < 0이면 tail부터 순차적으로 조회하며 count의 절대값에 해당하는 개수만큼 제거
     * count = 0이면 모두 조회한 후 value에 해당하는 값 모두 제거
     * value : 주어진 값과 같은 value를 가지는 대상이 삭제 대상이 된다
     * return값으로는 삭제한 인자의 개수를 리턴한다.
     * 
     * 해당 리스트에서 인덱스에 해당하는 값을 조회한 후, remove의 value값 인자로 넘겨준다.
     * 그 후 count에 1 값을 주면 head부터 순차적으로 조회하며 index에 해당하는 값을 제거할것이다.
     * return값이 1이면 1개를 삭제한 것이니 성공, 1이 아니라면 잘 삭제된것이 아니니 실패이다.
     */
    Long remove = redisTemplate.opsForList().remove(RedisKeyFactory.generateCartKey(memberId), 1,
        redisTemplate.opsForList().index(RedisKeyFactory.generateCartKey(memberId), index));
    return remove == 1;
  }
  
  /**
   * 해당 고객 장바구니의 가장 첫 번째 메뉴데이터를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return 리스트 첫 번째 메뉴데이터. 데이터가 없을 시 null
   */
  public ItemDTO findPeekByMemberId(String memberId) {
    String key = RedisKeyFactory.generateCartKey(memberId);
    return redisTemplate.opsForList().size(key) == 0 ? null
        : objectMapper.convertValue(redisTemplate.opsForList().index(key, 0), ItemDTO.class);
  }
  
}
