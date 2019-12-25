package com.delfood.dao;

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
public class FcmDao {
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Value("${expire.fcm.member}")
  private static Long MEMBER_TOKEN_EXPIRE_SECOND;
  
  @Value("${expire.fcm.owner}")
  private static Long OWNER_TOKEN_EXPIRE_SECOND;
  
  /**
   * 고객이 발급받은 토큰을 저장한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param token 토큰 정보
   */
  public void addMemberToken(String memberId, String token) {
    String key = RedisKeyFactory.generateFcmMemberKey(memberId);
    if (getMemberTokens(memberId).contains(token)) { // 토큰이 이미 있을 경우
      return;
    }
    redisTemplate.opsForList().rightPush(key, token);
    redisTemplate.expire(key, MEMBER_TOKEN_EXPIRE_SECOND, TimeUnit.SECONDS);
  }
  
  /**
   * 사장님이 발급받은 토큰을 저장한다.
   * @author jun
   * @param ownerId 사장님 아이디
   * @param token 토큰 정보
   */
  public void addOwnerToken(String ownerId, String token) {
    String key = RedisKeyFactory.generateFcmOwnerKey(ownerId);
    if (getOwnerTokens(ownerId).contains(token)) { // 토큰이 이미 있을 경우
      return;
    }
    redisTemplate.opsForList().rightPush(key, token);
    redisTemplate.expire(key, OWNER_TOKEN_EXPIRE_SECOND, TimeUnit.SECONDS);
  }
  
  /**
   * 해당 고객의 토큰 리스트를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public List<String> getMemberTokens(String memberId) {
    return redisTemplate.opsForList().range(RedisKeyFactory.generateFcmMemberKey(memberId), 0, -1)
        .stream()
        .map(e -> objectMapper.convertValue(e, String.class))
        .collect(Collectors.toList());
  }
  
  /**
   * 해당 사장님의 토큰 리스트를 조회한다.
   * @author jun
   * @param ownerId 사장님 아이디
   * @return
   */
  public List<String> getOwnerTokens(String ownerId) {
    return redisTemplate.opsForList().range(RedisKeyFactory.generateFcmOwnerKey(ownerId), 0, -1)
        .stream()
        .map(e -> objectMapper.convertValue(e, String.class))
        .collect(Collectors.toList());
  }
  
  
}
