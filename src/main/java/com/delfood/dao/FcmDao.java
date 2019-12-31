package com.delfood.dao;

import com.delfood.utils.RedisKeyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class FcmDao {
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Value("${expire.fcm.member}")
  private static Long memberTokenExpireSecond;
  
  @Value("${expire.fcm.owner}")
  private static Long ownerTokenExpireSecond;
  
  /**
   * 고객이 발급받은 토큰을 저장한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param token 토큰 정보
   */
  public void addMemberToken(String memberId, String token) {
    String key = RedisKeyFactory.generateFcmMemberKey(memberId);
    redisTemplate.watch(key);
    try {
      if (getMemberTokens(memberId).contains(token)) { // 토큰이 이미 있을 경우
        return;
      }
      redisTemplate.multi();
      
      redisTemplate.opsForList().rightPush(key, token);
      redisTemplate.expire(key, memberTokenExpireSecond, TimeUnit.SECONDS);
      
      redisTemplate.exec();
    } catch (Exception e) {
      log.error("Redis Add Member Token ERROR! key : {}", key);
      log.error("ERROR Info : {} ", e.getMessage());
      redisTemplate.discard();
      throw new RuntimeException(
          "Cannot add member token. key : " + key + ", ERROR Info " + e.getMessage());
    }
  }
  
  /**
   * 사장님이 발급받은 토큰을 저장한다.
   * @author jun
   * @param ownerId 사장님 아이디
   * @param token 토큰 정보
   */
  public void addOwnerToken(String ownerId, String token) {
    String key = RedisKeyFactory.generateFcmOwnerKey(ownerId);
    redisTemplate.watch(key);
    try {
      if (getOwnerTokens(ownerId).contains(token)) { // 토큰이 이미 있을 경우
        return;
      }
      redisTemplate.multi();
      
      redisTemplate.opsForList().rightPush(key, token);
      redisTemplate.expire(key, ownerTokenExpireSecond, TimeUnit.SECONDS);
      
      redisTemplate.exec();
    } catch (Exception e) {
      log.error("Redis Add Owner Token ERROR! key : {}", key);
      log.error("ERROR Info : {} ", e.getMessage());
      redisTemplate.discard();
      throw new RuntimeException(
          "Cannot add owner token. key : " + key + ", ERROR Info " + e.getMessage());
    }
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
